import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotificationsListComponent } from './notifications-list.component';
import { NotificationsViewComponent } from './notifications-view.component';
import { NotificationsComponent } from './notifications.component';

const routes: Routes = [
  {
    path: '',
    component: NotificationsComponent,
    children: [
      {
        path: ':notificationId',
        component: NotificationsViewComponent,
      },
      {
        path: '**',
        component: NotificationsListComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificationsRoutingModule {}
