import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppContainerComponent } from './app-container.component';
import { IsAuthenticated, IsUnauthenticated } from './app-route-guard.service';
import { RecoverContainerComponent } from './features/identity/recover-container.component';
import { RegisterContainerComponent } from './features/identity/register-container.component';
import { SignInContainerComponent } from './features/identity/sign-in-container.component';
import { VerifyContainerComponent } from './features/identity/verify-container.component';

export const routes: Routes = [
  {
    path: '',
    title: $localize`Dashboard | Acme`,
    component: AppContainerComponent,
    canActivate: [IsAuthenticated],
    children: [
      {
        path: 'scheduling',
        loadChildren: () =>
          import('./features/scheduling/scheduling.module').then(
            (m) => m.SchedulingModule
          ),
      },
      // {
      //   path: 'practices',
      //   loadChildren: () =>
      //     import('./features/practices/practices.module').then(
      //       (m) => m.PracticesModule
      //     ),
      // },
      // {
      //   path: 'notifications',
      //   loadChildren: () =>
      //     import('./features/notifications/notifications.module').then(
      //       (m) => m.NotificationsModule
      //     ),
      // },
      // {
      //   path: 'settings',
      //   component: SettingsComponent,
      // },
    ],
  },
  {
    path: 'register',
    component: RegisterContainerComponent,
    canActivate: [IsUnauthenticated],
    title: $localize`Register | Acme`
  },
  {
    path: 'sign-in',
    component: SignInContainerComponent,
    canActivate: [IsUnauthenticated],
    title: $localize`Sign In | Acme`
  },
  {
    path: 'recover',
    component: RecoverContainerComponent,
    canActivate: [IsUnauthenticated],
    title: $localize`Recover Your Account | Acme`
  },
  {
    path: 'verify',
    component: VerifyContainerComponent,
    canActivate: [IsUnauthenticated],
    title: $localize`Verify Your Email Address | Acme`
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
