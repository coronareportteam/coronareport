/// <reference types="cypress" />

describe(
  'S2 - Neu erstellter Indexfall kann sich registrieren',
  {
    defaultCommandTimeout: 20000,
  },
  () => {
    before((done) => {
      cy.restartBackend(done);

      cy.server();
      cy.route('POST', '/hd/cases/?type=index').as('newIndex');
      cy.route('GET', '/hd/cases/*').as('case');
      cy.route('PUT', '/hd/cases/*/registration').as('registration');
    });

    function extractActivationCode(elem: JQuery) {
      const regex = /\/client\/enrollment\/landing\/index\/(.*)/g;
      let content;

      if (typeof elem !== 'string') {
        content = elem.text();
      } else {
        content = elem;
      }

      try {
        return regex.exec(content)[1];
      } catch (e) {
        cy.log(e);
        throw e;
      }
    }

    it('new index case is able to complete registration', () => {
      cy.logInAgent();

      cy.location('pathname').should('eq', Cypress.env('index_cases_url'));

      cy.get('[data-cy="action-list"]').should('exist');
      cy.get('[data-cy="case-list"]').should('exist');

      cy.get('[data-cy="new-case-button"]').click();

      cy.location('pathname').should('eq', Cypress.env('health_department_url') + 'case-detail/new/index/edit');

      cy.get('[data-cy="client-submit-button"] button').should('be.disabled');
      cy.get('[data-cy="client-submit-and-close-button"] button').should('be.disabled');

      cy.get('[data-cy="input-firstname"]').type('Jamie');
      cy.get('[data-cy="input-lastname"]').type('Fraser');
      cy.get('[data-cy="input-dayofbirth"]').type('01.01.1970');
      cy.get('[data-cy="input-phone"]').type('162156156156');
      cy.get('[data-cy="input-email"]').type('james.fraser@gmail.com');

      cy.get('[data-cy="client-submit-button"] button').should('be.enabled');
      cy.get('[data-cy="client-submit-and-close-button"] button').should('be.enabled');

      cy.get('[data-cy="client-submit-button"] button').click();

      cy.wait('@newIndex').its('status').should('eq', 201);
      cy.wait('@case').its('status').should('eq', 200);
      cy.get('@newIndex')
        .its('response.body')
        .then((body) => {
          const caseId = body.caseId;
          expect(caseId).not.to.eq(null);
          expect(caseId).not.to.eq('');
          expect(body.email).to.eq('james.fraser@gmail.com');
          expect(body.caseType).to.eq('index');
          expect(body.caseTypeLabel).to.eq('Index');
          expect(body.firstName).to.eq('Jamie');
          expect(body.lastName).to.eq('Fraser');
          expect(body.phone).to.eq('162156156156');
          expect(body.status).to.eq('angelegt');
          expect(body.infected).to.eq(true);
          expect(body.dateOfBirth).to.eq('1970-01-01');

          cy.location('pathname').should('eq', '/health-department/case-detail/index/' + caseId + '/edit');
        });

      cy.get('[data-cy="start-tracking-button"]').should('be.enabled');
      cy.get('.mat-tab-links').children().should('have.length', 6);

      cy.get('[data-cy="start-tracking-button"]').click();
      cy.wait('@registration').its('status').should('eq', 200);

      cy.get('.mat-tab-links').children().should('have.length', 6);

      cy.location('pathname').should('include', '/comments');

      cy.get('[data-cy="email-tab"]').click();

      cy.location('pathname').should('include', '/email');
      cy.get('[data-cy="new-activation-code"]').should('be.enabled');

      cy.get('[data-cy="copy-to-clipboard"]').click();

      cy.get('[data-cy="mail-text"]').then((elem) => {
        const extractedActivationCode = extractActivationCode(elem);

        cy.logOut();

        cy.get('[data-cy="profile-user-button"]').should('not.exist');
        cy.visit('/client/enrollment/landing/index/' + extractedActivationCode);

        cy.get('h1').should('contain.text', 'Herzlich Willkommen bei quarano');

        cy.location('pathname').should('eq', '/client/enrollment/landing/index/' + extractedActivationCode);
      });

      cy.get('[data-cy="cta-button-index"]').click();

      cy.get('[data-cy="registration-submit-button"] button').should('be.disabled');

      cy.get('[data-cy="input-username"] input[matInput]').type('Jamie');
      cy.get('[data-cy="input-password"] input[matInput]').type('Password01!');

      cy.get('[data-cy="input-password-confirm"] input[matInput]').type('Password01!');
      cy.get('[data-cy="input-dateofbirth"] input[matInput]').type('01.01.1970');

      cy.get('[data-cy="input-privacy-policy"]').click();
      cy.get('[data-cy="registration-submit-button"] button').should('be.enabled');
      cy.get('[data-cy="registration-submit-button"] button').click();

      cy.location('pathname').should('eq', '/client/enrollment/basic-data');

      cy.get('[data-cy="profile-user-button"] .mat-button-wrapper span').should('have.text', 'Jamie Fraser ');
    });
  }
);
