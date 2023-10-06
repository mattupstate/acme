import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { environment } from 'projects/acme-app/src/environments/environment';
import {
  OryKratosHttpInterceptor,
  OryKratosHttpClient,
  ORY_KRATOS_HTTP_INTERCEPTORS,
} from './identity.http';
import { IdentityService, KratosIdentityService } from './identity.service';
import { RecoverContainerComponent } from './recover-container.component';
import { RecoverComponent } from './recover/recover.component';
import { RegisterContainerComponent } from './register-container.component';
import { RegisterComponent } from './register/register.component';
import { SignInContainerComponent } from './sign-in-container.component';
import { SignInComponent } from './signin/sign-in.component';
import { VerifyContainerComponent } from './verify-container.component';
import { VerifyComponent } from './verify/verify.component';

@NgModule({
  declarations: [
    RegisterComponent,
    SignInComponent,
    RecoverComponent,
    VerifyComponent,
    SignInContainerComponent,
    RecoverContainerComponent,
    RegisterContainerComponent,
    VerifyContainerComponent,
  ],
  imports: [
    RouterModule,
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ],
  providers: [
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}}
  ]
})
export class IdentityModule {
  static forRoot(): ModuleWithProviders<IdentityModule> {
    return {
      ngModule: IdentityModule,
      providers: [
        {
          provide: IdentityService,
          useFactory: (http: OryKratosHttpClient, window: Window) => {
            const baseUrl = `${window.location.protocol}//${window.location.host}`
            return new KratosIdentityService(
              http,
              environment.identity.kratosUrl,
              `${baseUrl}${environment.identity.afterVerifyReturnTo}`,
              `${baseUrl}${environment.identity.afterRecoveryReturnTo}`
            );
          },
          deps: [OryKratosHttpClient, Window],
        },
        {
          provide: ORY_KRATOS_HTTP_INTERCEPTORS,
          useClass: OryKratosHttpInterceptor,
          multi: true,
        },
        OryKratosHttpClient,
      ],
    };
  }
}
