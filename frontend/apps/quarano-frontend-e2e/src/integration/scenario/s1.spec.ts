/// <reference types="cypress" />

describe('S1 - Externe PLZ führt zu Status externe PLZ', { defaultCommandTimeout: 20000 }, () => {
  before((done) => {
    cy.restartBackend(done);
  });

  beforeEach(() => {
    cy.intercept('GET', '**/hd/cases').as('getAllCases');
    cy.intercept({
      method: 'GET',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/registration/,
    }).as('getEmailText');
    cy.intercept('POST', '**/hd/cases/?type=index').as('newIndex');
    cy.intercept('PUT', '/enrollment/questionnaire').as('updateQuestionnaire');
    cy.intercept({
      method: 'GET',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}$/,
    }).as('getCaseDetails');
    cy.intercept({
      method: 'GET',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/contacts$/,
    }).as('getCaseContacts');
    cy.intercept({
      method: 'POST',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}$/,
    }).as('postCaseDetails');
    cy.intercept({
      method: 'PUT',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}$/,
    }).as('putCaseDetails');
    cy.intercept({
      method: 'PUT',
      url: /.*\/hd\/cases\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/registration$/,
    }).as('putCaseDetailsRegistration');
    cy.intercept('GET', '**/registration/checkusername/**').as('getUserName');
    cy.intercept('POST', '**/registration').as('postRegistration');
    cy.intercept('PUT', '**/enrollment/details**').as('putEnrollmentDetails');
    cy.intercept('GET', '**/enrollment').as('getEnrollment');
    cy.intercept('GET', '**/symptoms**').as('getSymptoms');
    cy.intercept('GET', '**/contacts').as('getContacts');
    cy.intercept('GET', '**/enrollment/questionnaire').as('getEnrollmentQuestionnaire');
    cy.intercept('GET', '**/enrollment/details').as('getEnrollmentDetails');
    cy.intercept('GET', '**/encounters').as('getEncounters');
    cy.intercept('POST', '**/contacts').as('postContacts');
    cy.intercept('POST', '**/encounters').as('postEncounters');
    cy.intercept('POST', '**/enrollment/**').as('postEnrollment');
    cy.intercept('GET', '**/diary').as('getDiary');
  });

  it('run scenario 1', () => {
    // 0 - Login als Gama "agent1"
    cy.logInAgent();

    // 1 - wähle Übersichtsseite "Indexfälle"
    cy.get('[data-cy="index-cases"]').should('exist').click();
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/health-department/index-cases/case-list');
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);

    // 2 - wähle "neuen Indexfall anlegen"
    cy.get('[data-cy="new-case-button"]').should('exist').click();

    // 3 - Vorname -> "Julia"
    cy.get('[data-cy="input-firstname"]').should('exist').click().type('Julia');

    // 4 - Nachname ->  "Klein"
    cy.get('[data-cy="input-lastname"]').should('exist').click().type('Klein');

    // 5 - Geburtsdatum -> "06.10.1982"
    cy.get('[data-cy="input-dayofbirth"]').should('exist').click().type('06.10.1982');

    // 6 - Telefonnummer -> "0621842357"
    cy.get('[data-cy="phone-number-input"]').should('exist').click().type('0621842357');

    // 7 - Email -> "jklein@gmx.de"
    cy.get('[data-cy="input-email"]').should('exist').click().type('jklein@gmx.de');

    // 8 - Straße -> "Hauptstraße"
    cy.get('[data-cy="street-input"]').should('exist').click().type('Hauptstraße');

    // 9 - Hausnummer -> "152"
    cy.get('[data-cy="house-number-input"]').should('exist').click().type('152');

    // 10 - PLZ von Mannheim -> "68199"
    cy.get('[data-cy="zip-code-input"]').should('exist').click().type('68199');

    // 11 - Stadt -> "Mannheim"
    cy.get('[data-cy="city-input"]').should('exist').click().type('Mannheim');

    // 12 - wähle "Speichern"
    cy.get('[data-cy="client-submit-button"]').should('exist').click();
    cy.wait('@newIndex').its('response.statusCode').should('eq', 201);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/index\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/edit$/
    );

    // 13 - wähle "Nachverfolgung Starten"
    cy.get('[data-cy="start-tracking-span"]').should('exist').click();
    cy.wait('@putCaseDetailsRegistration').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/index\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/comments$/
    );

    // 14 - Extrahiere Anmeldelink aus dem Template
    cy.get('[data-cy="email-tab"]').should('exist').click();
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/index\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/email$/
    );
    cy.wait('@getEmailText').its('response.statusCode').should('eq', 200);
    cy.get('qro-client-mail > div > pre')
      .should('exist')
      .should('be.visible')
      .extractActivationCode(0, 'index')
      .then((extractedActivationCode) => {
        // 15 - Logout als GAMA
        cy.logOut();

        // 16 - Anmeldelink aufrufen
        cy.visit(extractedActivationCode);
        cy.url().should('include', extractedActivationCode);
      });

    // 17 - Klick auf Weiter
    cy.get('[data-cy="cta-button-index"]').should('exist').click();
    cy.url().should(
      'match',
      /.*\/client\/enrollment\/register\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}$/
    );

    // 18 - Benutzername: "Julia"
    cy.get('[data-cy="input-username"]').should('exist').click().type('Julia');

    // 19 - Passwort: "Password01!"
    cy.get('[data-cy="input-password"]').should('exist').click().type('Password01!');
    cy.wait('@getUserName').its('response.statusCode').should('eq', 200);

    // 20 - Passwort bestätgen  "Password01!"
    cy.get('[data-cy="input-password-confirm"]').should('exist').click().type('Password01!');

    // 21 - Geburtsdatum: 06.10.1982
    cy.get('[data-cy="input-dateofbirth"]').should('exist').click().type('06.10.1982');

    // 22 - AGB aktivieren
    cy.get('[data-cy="input-privacy-policy"]').should('exist').click();

    // 23 - Klick auf "Registrieren" Button
    cy.get('[data-cy="registration-submit-button"]').should('exist').click();
    cy.wait('@postRegistration').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getSymptoms').its('response.statusCode').should('eq', 200);
    cy.wait('@getContacts').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollmentQuestionnaire').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollmentDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getEncounters').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/client/enrollment/basic-data');

    // 24 - Klick auf "weiter"
    cy.get('[data-cy="first-step-button"]').should('exist').click();
    cy.wait('@putEnrollmentDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@putEnrollmentDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);

    // 25 - Haben Sie bereits Covid-19 charakteristische Symptome festgestellt? -> "Nein"
    cy.get('[data-cy="has-no-pre-existing-conditions-option"]').should('exist').click();

    // 26 - Bitte geben Sie Ihren behandelnden Hausarzt an. -> Dr. Schmidt
    cy.get('[data-cy="familyDoctor"]').should('exist').click().type('Dr. Schmidt');

    // 27 - Nennen Sie uns bitte den (vermuteten) Ort der Ansteckung: -> "Familie"
    // TODO data-cy
    cy.get('input[formcontrolname="guessedOriginOfInfection"]').should('exist').click().type('Familie');

    // 28 - Haben Sie eine oder mehrere relevante Vorerkrankungen? -> "nein"
    cy.get('[data-cy="has-no-symptoms-option"]').should('exist').click();

    // 29 - Arbeiten Sie im medizinischen Umfeld oder in der Pflege? -> "nein"
    cy.get('[data-cy="no-medical-staff-option"]').should('exist').click();

    // 30 - Haben Sie Kontakt zu Risikopersonen? -> "nein"
    cy.get('[data-cy="no-contact-option"]').should('exist').click();

    // 31 - Klick "weiter"
    cy.get('[data-cy="second-step-button"]').should('exist').click();
    cy.wait('@updateQuestionnaire').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);

    // 32 - Kontakte mit anderen Menschen (Kontaktperson heute) -> "Manfred Klein"
    // 33 - Klick enter / Eingabefeld abwählen
    cy.get('[data-cy="multiple-auto-complete-input"]').should('exist').first().click().type('Manfred Klein').blur();

    // 34 - wähle "Kontakt anlegen" in Popup
    cy.get('[data-cy="confirm-button"]').should('exist').click();

    // 35 - Telefonnummer (mobil) -> "01758631534"
    cy.get('[data-cy="input-mobilePhone"]').should('exist').click().type('01758631534');

    // 36 - Klick auf "speichern"
    cy.get('[data-cy="submit-button"]').should('exist').click();
    cy.wait('@postContacts').its('response.statusCode').should('eq', 201);
    cy.wait('@postEncounters').its('response.statusCode').should('eq', 201);

    // 37 - Klick auf "Erfassung abschließen"
    cy.get('[data-cy="third-step-button"]').should('exist').click();
    cy.wait('@postEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getDiary').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/client/diary/diary-list');

    // 38 - Logout als Bürger
    cy.logOut();

    // 39 - Login als GAMA "agent1"
    cy.logInAgent();
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/health-department/index-cases/case-list');
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);

    // 40 - suche Indexfall "Julia Klein"
    cy.get('[data-cy="search-index-case-input"]').should('exist').click().type('Julia Klein');
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .should('have.length.greaterThan', 0);
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .then(($elems) => {
        $elems[0].click();
      });
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/index\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/edit$/
    );

    // 41 - wähle Reiter "Kontakte"
    cy.get('[data-cy="contacts-tab"]').should('exist').click();
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/index\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/contacts$/
    );
    cy.wait('@getCaseContacts').its('response.statusCode').should('eq', 200);

    // 42 - klick auf "Manfred Klein"
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .should('have.length.greaterThan', 0);
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .then(($elems) => {
        $elems[0].click();
      });
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseContacts').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/contact\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/edit$/
    );

    // 43 - Geburtsdatum -> "25.07.1980"
    cy.get('[data-cy="input-dayofbirth"]').should('exist').click().type('25.07.1980');

    // 44 - Email -> "mklein@gmx.de"
    cy.get('[data-cy="input-email"]').should('exist').click().type('mklein@gmx.de');

    // 45 - wähle "Speichern"
    cy.get('[data-cy="client-submit-button"]').should('exist').click();
    cy.wait('@putCaseDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);

    // 46 - wähle "Nachverfolgung Starten"
    cy.get('[data-cy="start-tracking-button"]').should('exist').should('be.enabled').click();
    cy.wait('@putCaseDetailsRegistration').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/contact\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/comments$/
    );

    // 47 - Extrahiere Anmeldelink aus dem Template
    cy.get('[data-cy="email-tab"]').should('exist').click();
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/contact\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/email$/
    );
    cy.wait('@getEmailText').its('response.statusCode').should('eq', 200);
    cy.get('qro-client-mail > div > pre')
      .should('exist')
      .should('be.visible')
      .extractActivationCode(0, 'contact')
      .then((extractedActivationCode) => {
        // 48 - Logout als GAMA
        cy.logOut();

        // 49 - Anmeldelink aufrufen
        cy.visit(extractedActivationCode);
        cy.url().should('include', extractedActivationCode);
      });

    // 50 - Klick auf "Weiter"
    cy.get('[data-cy="cta-button-index"]').should('exist').click();
    cy.url().should(
      'match',
      /.*\/client\/enrollment\/register\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}$/
    );

    // 51 - Benutzername: "Manfred"
    cy.get('[data-cy="input-username"]').should('exist').click().type('Manfred');

    // 52 - Passwort: "Password02!"
    cy.get('[data-cy="input-password"]').should('exist').click().type('Password02!');
    cy.wait('@getUserName').its('response.statusCode').should('eq', 200);

    // 53 - Passwort bestätgen  "Password02!"
    cy.get('[data-cy="input-password-confirm"]').should('exist').click().type('Password02!');

    // 54 - Geburtsdatum: "25.07.1980"
    cy.get('[data-cy="input-dateofbirth"]').should('exist').click().type('25.07.1980');

    // 55 - AGB aktivieren
    cy.get('[data-cy="input-privacy-policy"]').should('exist').click();

    // 56 - Klick auf "Registrieren" Button
    cy.get('[data-cy="registration-submit-button"]').should('exist').click();
    cy.wait('@postRegistration').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.wait('@getSymptoms').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollmentQuestionnaire').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollmentDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getEncounters').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/client/enrollment/basic-data');

    // 57 - Straße -> "Hauptstraße"
    cy.get('[data-cy="street-input"]').should('exist').click().type('Hauptstraße');

    // 58 - Hausnummer -> "152"
    cy.get('[data-cy="house-number-input"]').should('exist').click().type('152');

    // 59 - PLZ von Ilvesheim -> "68549"
    cy.get('[data-cy="zip-code-input"]').should('exist').click().type('68549');

    // 60 - Stadt -> "Ilvesheim"
    cy.get('[data-cy="city-input"]').should('exist').click().type('Ilvesheim');

    // 61 - Klick auf "weiter"
    cy.get('[data-cy="first-step-button"]').should('exist').click();
    cy.wait('@putEnrollmentDetails').its('response.statusCode').should('eq', 422);

    // CHECK: Popup erscheint mit Text "Bitte prüfen Sie die eingegebene PLZ
    // Das für die PLZ 68549 zuständige Gesundheitsamt arbeitet nicht mit dieser Software. Bitte überprüfen Sie zur Sicherheit Ihre Eingabe. Ist diese korrekt, dann verwenden Sie diese Software nicht weiter und wenden Sie sich bitte an Ihr zuständiges Gesundheitsamt."
    cy.get('[data-cy="dialog-content"]')
      .should('exist')
      .should(
        'have.text',
        'Das für die PLZ 68549 zuständige Gesundheitsamt arbeitet nicht mit dieser Software. Bitte überprüfen Sie zur Sicherheit Ihre Eingabe. Ist diese korrekt, dann verwenden Sie diese Software nicht weiter und wenden Sie sich bitte an Ihr zuständiges Gesundheitsamt.'
      );
    cy.get('[data-cy="dialog-title"]').should('exist').should('have.text', 'Bitte prüfen Sie die eingegebene PLZ');

    // 62 - Klick "PLZ bestätigen"
    cy.get('[data-cy="confirm-button"]').should('exist').click();
    cy.wait('@putEnrollmentDetails').its('response.statusCode').should('eq', 200);
    cy.wait('@getEnrollment').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'include',
      'client/enrollment/health-department?healthDepartment=%257B%2522name%2522%253A%2522Landratsamt%2520Rhein-Neckar-Kreis%2522%252C%2522department%2522%253A%2522Gesundheitsamt%2522%252C%2522street%2522%253A%2522Kurf%25C3%25BCrstenanlage%252038-40%2522%252C%2522city%2522%253A%2522Heidelberg%2522%252C%2522zipCode%2522%253A%252269115%2522%252C%2522fax%2522%253A%2522062215221899%2522%252C%2522phone%2522%253A%2522062215221817%2522%252C%2522email%2522%253A%2522infektionsschutz%2540rhein-neckar-kreis.de%2522%252C%2522_links%2522%253A%257B%2522enrollment%2522%253A%257B%2522href%2522%253A%2522http%253A%252F%252Flocalhost%253A8080%252Fenrollment%2522%257D%257D%257D'
    );

    // CHECK: Es erscheint folgender Text: "Das für Sie zuständige Gesundheitsamt arbeitet nicht mit Quarano. Bitte wenden Sie sich direkt an Ihr Gesundheitsamt.
    // Landratsamt Rhein-Neckar-Kreis; Gesundheitsamt; Kurfürstenanlage 38-40; 69115 Heidelberg
    // E-Mail:	infektionsschutz@rhein-neckar-kreis.de; Telefon:	062215221817; Fax:	062215221899"
    cy.get('[data-cy="health-department-name"]').should('exist').should('have.text', 'Landratsamt Rhein-Neckar-Kreis');
    cy.get('[data-cy="PLZ-info-text"]')
      .should('exist')
      .should(
        'have.text',
        ' Das für Sie zuständige Gesundheitsamt arbeitet nicht mit Quarano. Bitte wenden Sie sich direkt an Ihr Gesundheitsamt. '
      );
    cy.get('[data-cy="health-department-name-2"]')
      .should('exist')
      .should('have.text', 'Landratsamt Rhein-Neckar-Kreis');
    cy.get('[data-cy="health-department-street"]').should('exist').should('have.text', 'Kurfürstenanlage 38-40');
    cy.get('[data-cy="health-department-zipCode"]').should('exist').should('have.text', '69115 Heidelberg');
    cy.get('[data-cy="health-department-email"]')
      .should('exist')
      .should('have.text', 'infektionsschutz@rhein-neckar-kreis.de');
    cy.get('[data-cy="health-department-phone"]').should('exist').should('have.text', '062215221817');
    cy.get('[data-cy="health-department-fax"]').should('exist').should('have.text', '062215221899');

    // 62b) Klick auf “Browser Back” button
    cy.go('back');

    // CHECK: User ist auf Loginseite und ist nicht mehr eingeloggt (rechts oben wird kein Name angezeigt)
    cy.get('[data-cy="profile-user-button"]').should('not.exist');

    // CHECK: neue Anmeldung als "Manfred" (Passwort: "Password02!") ist nicht möglich
    cy.logIn('Manfred', 'Password02!');
    cy.url().should(
      'include',
      'auth/forbidden?message=F%25C3%25BCr%2520Sie%2520ist%2520ein%2520anderes%2520Gesundheitsamt%2520zust%25C3%25A4ndig.%2520Wenden%2520Sie%2520sich%2520bitte%2520an%2520dieses!'
    );
    cy.get('[data-cy="forbidden-title"]').should('exist').should('have.text', 'Da ist etwas schief gelaufen...');
    cy.get('[data-cy="forbidden-subtitle"]').should('exist').should('have.text', 'Zugriff verweigert');
    cy.get('[data-cy="forbidden-message"]')
      .should('exist')
      .should('have.text', 'Für Sie ist ein anderes Gesundheitsamt zuständig. Wenden Sie sich bitte an dieses!');

    // 63 - Login als GAMA "agent1"
    cy.logInAgent();
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/health-department/index-cases/case-list');
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);

    // 64 - wähle Übersichtsseite "Kontaktfälle"
    cy.get('[data-cy="contact-cases"]').should('exist').click();
    cy.wait('@getAllCases').its('response.statusCode').should('eq', 200);
    cy.url().should('eq', 'http://localhost:4200/health-department/contact-cases/case-list');

    // CHECK: Status bei "Manfred Klein" ist "Externe PLZ"
    cy.get('[data-cy="search-contact-case-input"]').should('exist').click().type('Manfred Klein');
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .should('have.length.greaterThan', 0);
    cy.get('[data-cy="case-data-table"]')
      .find('.ag-center-cols-container > .ag-row')
      .then(($elems) => {
        $elems[0].click();
      });
    cy.wait('@getCaseDetails').its('response.statusCode').should('eq', 200);
    cy.url().should(
      'match',
      /.*\/health-department\/case-detail\/contact\/[0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12}\/edit$/
    );
    cy.get('[data-cy="case-status"]').should('exist').should('have.text', 'Externe PLZ');

    // 65 - Logout als GAMA
    cy.logOut();
  });
});
