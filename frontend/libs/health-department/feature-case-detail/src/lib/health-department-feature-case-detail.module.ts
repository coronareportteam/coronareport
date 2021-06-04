import { SharedUiAgGridModule } from '@qro/shared/ui-ag-grid';
import { SharedUiErrorModule } from '@qro/shared/ui-error';
import { SharedUiMultipleAutocompleteModule } from '@qro/shared/ui-multiple-autocomplete';
import { SharedUtilTranslationModule } from '@qro/shared/util-translation';
import { ContactListComponent } from './contact-list/contact-list.component';
import { SharedUiButtonModule } from '@qro/shared/ui-button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedUiMaterialModule } from '@qro/shared/ui-material';
import { SymptomsResolver, SharedUtilSymptomModule } from '@qro/shared/util-symptom';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { CaseDetailComponent } from './case-detail/case-detail.component';
import {
  CaseDetailResolver,
  HealthDepartmentDomainModule,
  ReportCaseActionsResolver,
} from '@qro/health-department/domain';
import { QuestionnaireComponent } from './questionnaire/questionnaire.component';
import { MailComponent } from './mail/mail.component';
import { IndexContactsComponent } from './index-contacts/index-contacts.component';
import { EditComponent } from './edit/edit.component';
import { CommentsComponent } from './comments/comments.component';
import { CloseCaseDialogComponent } from './close-case-dialog/close-case-dialog.component';
import { AnomalyComponent } from './anomaly/anomaly.component';
import { ActionComponent } from './action/action.component';
import { DiaryEntriesListComponent } from './diary-entries-list/diary-entries-list.component';
import { DiaryEntriesListItemComponent } from './diary-entries-list-item/diary-entries-list-item.component';
import { OccasionListComponent } from './occasion/occasion-list/occasion-list.component';
import { OccasionCardComponent } from './occasion/occasion-card/occasion-card.component';
import { OccasionDetailDialogComponent } from './occasion/occasion-detail-dialog/occasion-detail-dialog.component';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

const routes: Routes = [
  {
    path: 'new/:type',
    component: CaseDetailComponent,
    children: [
      {
        path: '',
        redirectTo: 'edit',
        pathMatch: 'full',
      },
      {
        path: 'edit',
        component: EditComponent,
      },
    ],
  },
  {
    path: ':type/:id',
    component: CaseDetailComponent,
    resolve: { caseDetail: CaseDetailResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: '',
        redirectTo: 'edit',
        pathMatch: 'full',
      },
      {
        path: 'edit',
        component: EditComponent,
      },
      {
        path: 'actions',
        component: ActionComponent,
        resolve: { actions: ReportCaseActionsResolver },
      },
      {
        path: 'comments',
        component: CommentsComponent,
      },
      {
        path: 'questionnaire',
        component: QuestionnaireComponent,
        resolve: { symptomsLoaded: SymptomsResolver },
      },
      {
        path: 'index-case-data',
        component: IndexContactsComponent,
      },
      {
        path: 'contacts',
        component: ContactListComponent,
      },
      {
        path: 'email',
        component: MailComponent,
      },
      {
        path: 'diary',
        component: DiaryEntriesListComponent,
      },
      {
        path: 'events',
        component: OccasionListComponent,
      },
    ],
  },
];

@NgModule({
  declarations: [
    ActionComponent,
    AnomalyComponent,
    CaseDetailComponent,
    CloseCaseDialogComponent,
    CommentsComponent,
    EditComponent,
    IndexContactsComponent,
    MailComponent,
    QuestionnaireComponent,
    ContactListComponent,
    DiaryEntriesListComponent,
    DiaryEntriesListItemComponent,
    OccasionListComponent,
    OccasionCardComponent,
    OccasionDetailDialogComponent,
  ],
  imports: [
    CommonModule,
    SharedUiErrorModule,
    RouterModule.forChild(routes),
    SharedUiMaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SharedUiButtonModule,
    HealthDepartmentDomainModule,
    SharedUtilSymptomModule,
    SharedUiMultipleAutocompleteModule,
    SharedUtilTranslationModule,
    SharedUiAgGridModule,
    NgxMaterialTimepickerModule,
  ],
})
export class HealthDepartmentFeatureCaseDetailModule {}
