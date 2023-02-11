import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { environment } from 'projects/acme-app/src/environments/environment';
import { IdentityServiceHttpInterceptor } from './http-interceptor';
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
    FlexLayoutModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ],
})
export class IdentityModule {
  static forRoot(baseUrl: string): ModuleWithProviders<IdentityModule> {
    return {
      ngModule: IdentityModule,
      providers: [
        {
          provide: IdentityService,
          useFactory: (http: HttpClient) => {
            return new KratosIdentityService(http, baseUrl, environment.identity.afterVerificationReturnTo, environment.identity.afterVerificationReturnTo);
          },
          deps: [HttpClient],
        },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: IdentityServiceHttpInterceptor,
          multi: true,
        },
      ],
    };
  }
}
