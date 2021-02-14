/// <reference types="cypress" />

describe('landing page', () => {
  const clientCode = 'af12bd44-70b3-44f1-82d6-79bfd31dd4f4';

  describe('correct content is shown', () => {
    it.skip('index person', () => {
      cy.visit(`client/enrollment/landing/index/${clientCode}`);
      cy.get('[data-cy="cta-button-index"]').should('exist');
    });

    it.skip('contact person', () => {
      cy.visit(`client/enrollment/landing/contact/${clientCode}`);
      cy.get('[data-cy="cta-button-index"]').should('exist');
    });
  });

  describe('user is directed to the right page with correct client code', () => {
    it.skip('index person', () => {
      cy.visit(`client/enrollment/landing/index/${clientCode}`);
      cy.get('[data-cy="cta-button-index"]').click();
      cy.location('pathname').should('eq', `/client/enrollment/register/${clientCode}`);
      cy.get('[data-cy="input-client-code"] input[matInput]').should('exist').should('have.value', clientCode);
    });

    it.skip('contact person', () => {
      cy.visit(`client/enrollment/landing/contact/${clientCode}`);
      cy.get('[data-cy="cta-button-index"]').click();
      cy.location('pathname').should('eq', `/client/enrollment/register/${clientCode}`);
      cy.get('[data-cy="input-client-code"] input[matInput]').should('exist').should('have.value', clientCode);
    });
  });
});
