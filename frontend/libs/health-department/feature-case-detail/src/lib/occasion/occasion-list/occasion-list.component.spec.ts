import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OccasionListComponent } from './occasion-list.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OccasionListComponent', () => {
  let component: OccasionListComponent;
  let fixture: ComponentFixture<OccasionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OccasionListComponent],
      imports: [
        TranslateModule.forRoot(),
        FormsModule,
        ReactiveFormsModule,
        MatDatepickerModule,
        HttpClientTestingModule,
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OccasionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
