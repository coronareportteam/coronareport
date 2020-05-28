import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedUiMaterialModule } from '@qro/shared/ui-material';
import { RouterModule } from '@angular/router';
import { ContactCasesComponent } from './contact-cases/contact-cases.component';

@NgModule({
  imports: [
    CommonModule,
    SharedUiMaterialModule,
    RouterModule.forChild([{
      path: '',
      component: ContactCasesComponent,
      children: [
        {
          path: '',
          redirectTo: 'case-list',
          pathMatch: 'full',
        },
        {
          path: 'action-list', loadChildren: () =>
            import('@qro/health-department/contact-cases/action-list')
              .then(m => m.HealthDepartmentContactCasesActionListModule)
        },
        {
          path: 'case-list', loadChildren: () =>
            import('@qro/health-department/contact-cases/case-list')
              .then(m => m.HealthDepartmentContactCasesCaseListModule)
        }
      ]
    }
    ])],
  declarations: [ContactCasesComponent]
})
export class HealthDepartmentContactCasesShellModule { }
