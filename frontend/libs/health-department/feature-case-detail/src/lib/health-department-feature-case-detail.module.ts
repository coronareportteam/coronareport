import { SharedUiMultipleAutocompleteModule } from '@qro/shared/ui-multiple-autocomplete';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ContactListComponent } from './contact-list/contact-list.component';
import { SharedUiButtonModule } from '@qro/shared/ui-button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedUiMaterialModule } from '@qro/shared/ui-material';
import { SymptomsResolver, SharedUtilSymptomModule } from '@qro/shared/util-symptom';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { CaseDetailComponent } from './case-detail/case-detail.component';
import { HealthDepartmentDomainModule, ReportCaseActionsResolver } from '@qro/health-department/domain';
import { QuestionnaireComponent } from './questionnaire/questionnaire.component';
import { MailComponent } from './mail/mail.component';
import { IndexContactsComponent } from './index-contacts/index-contacts.component';
import { EditComponent } from './edit/edit.component';
import { CommentsComponent } from './comments/comments.component';
import { CloseCaseDialogComponent } from './close-case-dialog/close-case-dialog.component';
import { AnomalyComponent } from './anomaly/anomaly.component';
import { ActionComponent } from './action/action.component';

const routes: Routes = [
  {
    path: 'new/:type',
    component: CaseDetailComponent,
  },
  {
    path: ':type/:id',
    component: CaseDetailComponent,
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
        resolve: { symptoms: SymptomsResolver },
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
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedUiMaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SharedUiButtonModule,
    HealthDepartmentDomainModule,
    SharedUtilSymptomModule,
    NgxDatatableModule,
    SharedUiMultipleAutocompleteModule,
  ],
})
export class HealthDepartmentFeatureCaseDetailModule {}
