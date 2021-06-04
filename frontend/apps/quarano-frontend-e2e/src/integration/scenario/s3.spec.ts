/// <reference types="cypress" />

describe(
  'S3 - GAMA kann neuen Kontaktfall anlegen',
  {
    defaultCommandTimeout: 20000,
  },
  () => {
    before((done) => {
      cy.restartBackend(done);

      cy.server();
      cy.route('POST', '/hd/cases/?type=contact').as('newContact');
      cy.route('GET', '/hd/cases/*').as('case');
      cy.route('PUT', '/hd/cases/*/registration').as('registration');
      cy.route('GET', '/hd/cases?type=index*').as('search');
    });

    function extractActivationCode(elem: JQuery) {
      const regex = /\/client\/enrollment\/landing\/contact\/(.*)/g;
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

    it('new contact case is accessible', () => {
      cy.logInAgent();

      cy.location('pathname').should('eq', Cypress.env('index_cases_url'));

      cy.get('[data-cy="contact-cases"]').click();

      cy.location('pathname').should('eq', Cypress.env('contact_cases_url'));

      cy.get('[data-cy="action-list"]').should('exist');
      cy.get('[data-cy="contact-list"]').should('exist');

      cy.get('[data-cy="new-case-button"]').click();

      cy.location('pathname').should('eq', Cypress.env('health_department_url') + 'case-detail/new/contact/edit');

      cy.get('[data-cy="client-submit-button"] button').should('be.disabled');
      cy.get('[data-cy="client-submit-and-close-button"] button').should('be.disabled');

      cy.get('[data-cy="input-firstname"]').type('Jack');
      cy.get('[data-cy="input-lastname"]').type('Randel');
      cy.get('[data-cy="input-dayofbirth"]').type('01.01.1970');
      cy.get('[data-cy="input-phone"]').type('162156156156');
      cy.get('[data-cy="input-email"]').type('jack@gmail.com');
      cy.get('[data-cy="chip-list-input"]').type('Aalen');
      cy.wait('@search').its('status').should('eq', 200);
      cy.get('mat-option').click();

      cy.get('[data-cy="client-submit-button"] button').should('be.enabled');
      cy.get('[data-cy="client-submit-and-close-button"] button').should('be.enabled');

      cy.get('[data-cy="client-submit-button"] button').click();

      cy.wait('@newContact').its('status').should('eq', 201);
      cy.wait('@case').its('status').should('eq', 200);
      cy.get('@newContact')
        .its('response.body')
        .then((body) => {
          const caseId = body.caseId;
          expect(caseId).not.to.eq(null);
          expect(caseId).not.to.eq('');
          expect(body.email).to.eq('jack@gmail.com');
          expect(body.caseType).to.eq('contact');
          expect(body.caseTypeLabel).to.eq('Kontakt');
          expect(body.firstName).to.eq('Jack');
          expect(body.lastName).to.eq('Randel');
          expect(body.phone).to.eq('162156156156');
          expect(body.status).to.eq('angelegt');
          expect(body.infected).to.eq(false);
          expect(body.dateOfBirth).to.eq('1970-01-01');
          expect(body._embedded.originCases).to.be.an('array').that.does.have.length(1);
          expect(body._embedded.originCases[0].dateOfBirth).to.eq('1990-01-01');
          expect(body._embedded.originCases[0].firstName).to.eq('Peter');
          expect(body._embedded.originCases[0].lastName).to.eq('Aalen');

          cy.location('pathname').should('eq', '/health-department/case-detail/contact/' + caseId + '/edit');
        });

      cy.get('[data-cy="start-tracking-button"]').should('be.enabled');
      cy.get('.mat-tab-links').children().should('have.length', 5);

      cy.get('[data-cy="start-tracking-button"]').click();
      cy.wait('@registration').its('status').should('eq', 200);

      cy.get('.mat-tab-links').children().should('have.length', 5);

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

      cy.get('[data-cy="input-username"] input[matInput]').type('Jack');
      cy.get('[data-cy="input-password"] input[matInput]').type('Password01!');

      cy.get('[data-cy="input-password-confirm"] input[matInput]').type('Password01!');
      cy.get('[data-cy="input-dateofbirth"] input[matInput]').type('01.01.1970');

      cy.get('[data-cy="input-privacy-policy"]').click();
      cy.get('[data-cy="registration-submit-button"] button').should('be.enabled');
      cy.get('[data-cy="registration-submit-button"] button').click();

      cy.location('pathname').should('eq', '/client/enrollment/basic-data');

      cy.get('[data-cy="profile-user-button"] .mat-button-wrapper span').should('have.text', 'Jack Randel ');
    });
  }
);
