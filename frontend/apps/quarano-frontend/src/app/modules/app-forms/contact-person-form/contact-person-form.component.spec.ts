/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactPersonFormComponent } from './contact-person-form.component';
import { FormBuilder } from '@angular/forms';
import { ApiService } from '../../../../../../../../apps/quarano-frontend/src/app/services/api.service';
import { SnackbarService } from './node_modules/@qro/shared/util';
import { ContactPersonDto } from '../../../../../domain/src/lib/models/contact-person';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ContactPersonFormComponent', () => {
  let component: ContactPersonFormComponent;
  let fixture: ComponentFixture<ContactPersonFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContactPersonFormComponent],
      providers: [FormBuilder, { provide: ApiService, useValue: {} }, { provide: SnackbarService, useValue: {} }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContactPersonFormComponent);
    component = fixture.componentInstance;
    component.contactPerson = {} as ContactPersonDto;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
