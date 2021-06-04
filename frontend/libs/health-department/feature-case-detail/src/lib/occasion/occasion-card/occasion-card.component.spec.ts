import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OccasionCardComponent } from './occasion-card.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('occasionCardComponent', () => {
  let component: OccasionCardComponent;
  let fixture: ComponentFixture<OccasionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OccasionCardComponent],
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
    fixture = TestBed.createComponent(OccasionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
