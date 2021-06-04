const characterSet = 'a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒß';

export interface IValidationPattern {
  pattern: string | RegExp;
  errorMessage: string;
}

export enum VALIDATION_PATTERNS {
  integerUnsigned = 'integerUnsigned',
  alphanumeric = 'alphanumeric',
  alphabetic = 'alphabetic',
  textual = 'textual',
  uuid = 'uuid',
  zip = 'zip',
  street = 'street',
  houseNumber = 'houseNumber',
  name = 'name',
  username = 'username',
  phoneNumber = 'phoneNumber',
  email = 'email',
  city = 'city',
  extReferenceNumber = 'extReferenceNumber',
  timestamp = 'timestamp',
}

export class ValidationPattern {
  private static _patterns: Map<VALIDATION_PATTERNS, IValidationPattern>;

  public static get validationPatterns() {
    if (!this._patterns) {
      this.initialize();
    }
    return this._patterns;
  }

  private static initialize() {
    this._patterns = new Map<VALIDATION_PATTERNS, IValidationPattern>();
    this._patterns.set(VALIDATION_PATTERNS.integerUnsigned, {
      pattern: '^[0-9]+$',
      errorMessage: 'VALIDATION.INTEGER_UNSIGNED',
    });
    this._patterns.set(VALIDATION_PATTERNS.alphanumeric, {
      pattern: `[${characterSet}\\s\\d]*`,
      errorMessage: 'VALIDATION.ALPHANUMERIC',
    });
    this._patterns.set(VALIDATION_PATTERNS.alphabetic, {
      pattern: `[${characterSet}\\s]*`,
      errorMessage: 'VALIDATION.ALPHABETIC',
    });
    this._patterns.set(VALIDATION_PATTERNS.textual, {
      pattern: `[${characterSet}\\s\\d\\.\\,\\?\\!\\(\\)\\-\\n\\r]*`,
      errorMessage: 'VALIDATION.TEXTUAL',
    });
    this._patterns.set(VALIDATION_PATTERNS.uuid, {
      pattern: '\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b',
      errorMessage: 'VALIDATION.UUID',
    });
    this._patterns.set(VALIDATION_PATTERNS.zip, {
      pattern: '[0-9]{5}',
      errorMessage: 'VALIDATION.ZIP',
    });
    this._patterns.set(VALIDATION_PATTERNS.street, {
      pattern: `[${characterSet}0-9\\s\\.\\-]*`,
      errorMessage: 'VALIDATION.STREET',
    });
    this._patterns.set(VALIDATION_PATTERNS.houseNumber, {
      pattern: `[${characterSet}0-9\\s\\.\\-*\\/]{0,15}`,
      errorMessage: 'VALIDATION.HOUSE_NUMBER',
    });
    this._patterns.set(VALIDATION_PATTERNS.name, {
      pattern: `([${characterSet}\\s\\-]*)`,
      errorMessage: 'VALIDATION.NAME',
    });
    this._patterns.set(VALIDATION_PATTERNS.username, {
      pattern: `([${characterSet}0-9\\-\\_]*)`,
      errorMessage: 'VALIDATION.USERNAME',
    });
    this._patterns.set(VALIDATION_PATTERNS.phoneNumber, {
      pattern: '^[\\+\\(\\)\\s\\-0-9]*?$',
      errorMessage: 'VALIDATION.PHONE_NUMBER',
    });
    this._patterns.set(VALIDATION_PATTERNS.email, {
      // tslint:disable-next-line: max-line-length quotemark
      pattern:
        '(?:[a-zA-Z0-9!#$%&\' * +/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&\'*+/=?^_`{|}~-]+)*|"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])',
      errorMessage: 'VALIDATION.EMAIL',
    });
    this._patterns.set(VALIDATION_PATTERNS.city, {
      pattern: `[${characterSet}\\s\\.\\-\\(\\)\\/]*`,
      errorMessage: 'VALIDATION.CITY',
    });
    this._patterns.set(VALIDATION_PATTERNS.extReferenceNumber, {
      pattern: `[${characterSet}0-9\\-\\_\\/]*`,
      errorMessage: 'VALIDATION.EXT_REFERENCE_NUMBER',
    });
    this._patterns.set(VALIDATION_PATTERNS.timestamp, {
      pattern: '((?:(?:0|1)\\d|2[0-3])):([0-5]\\d)',
      errorMessage: 'VALIDATION.TIMESTAMP',
    });
  }
}
