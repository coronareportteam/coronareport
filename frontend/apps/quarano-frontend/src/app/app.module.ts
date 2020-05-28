import { SharedUtilErrorModule } from '@qro/shared/util-error';
import { SharedUiMaterialModule } from '@qro/shared/ui-material';
import { API_URL } from '@qro/shared/util';
import { AuthChangePasswordModule } from '@qro/auth/change-password';
import { TenantAdminModule } from './modules/tenant-admin/tenant-admin.module';
import { IsAdminDirective } from './directives/is-admin.directive';
import { DataProtectionComponent } from './components/data-protection/data-protection.component';
import { ImpressumComponent } from './components/impressum/impressum.component';
import { AgbComponent } from './components/agb/agb.component';
import { AsideComponent } from './components/aside/aside.component';
import { ProfileModule } from './modules/profile/profile.module';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DiaryModule } from './modules/diary/diary.module';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import { WelcomeModule } from './modules/welcome/welcome.module';
import { ContactPersonsModule } from './modules/contact-persons/contact-persons.module';
import { AuthInterceptor } from './interceptors/auth-interceptor';
import { FooterComponent } from './components/layout/footer/footer.component';
import { HeaderLeftComponent } from './components/layout/header-left/header-left.component';
import { HeaderRightComponent } from './components/layout/header-right/header-right.component';
import { ProgressBarInterceptor } from './interceptors/progress-bar.interceptor';
import { BasicDataModule } from './modules/basic-data/basic-data.module';
import { DateInterceptor } from './interceptors/date-interceptor';
import { HdContactComponent } from './components/hd-contact/hd-contact.component';
import { IsHealthDepartmentUserDirective } from './directives/is-health-department-user.directive';
import { IsEnrolledClientDirective } from './directives/is-enrolled-client.directive';
import { AsideHostDirective } from './directives/aside-host.directive';
import { HasRoleDirective } from './directives/has-role.directive';
import { SnackbarService } from '../../../../libs/shared/util/src/lib/snackbar.service';
import { environment } from '../environments/environment';

registerLocaleData(localeDe, 'de');

const DIRECTIVES = [
  HasRoleDirective,
  IsHealthDepartmentUserDirective,
  AsideHostDirective,
  IsEnrolledClientDirective,
  IsAdminDirective
];

const COMPONENTS = [
  AppComponent,
  NotFoundComponent,
  FooterComponent,
  HeaderLeftComponent,
  HeaderRightComponent,
  ForbiddenComponent,
  AsideComponent,
  AgbComponent,
  ImpressumComponent,
  DataProtectionComponent,
  HdContactComponent
];

const SUB_MODULES = [
  DiaryModule,
  WelcomeModule,
  ContactPersonsModule,
  BasicDataModule,
  ProfileModule,
  TenantAdminModule,
  AuthChangePasswordModule,
  SharedUtilErrorModule
];

@NgModule({
  declarations: [
    ...COMPONENTS,
    ...DIRECTIVES
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    SharedUiMaterialModule,
    FormsModule,
    HttpClientModule,
    ...SUB_MODULES
  ],
  entryComponents: [
    HdContactComponent
  ],
  providers: [
    SnackbarService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ProgressBarInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DateInterceptor,
      multi: true
    },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: LOCALE_ID, useValue: 'de-de' },
    { provide: API_URL, useValue: environment.api.baseUrl }
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {
}
