import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PracticesContainerComponent } from './practices-container.component';
import { PracticesListComponent } from './practices/practices-list.component';
import { PracticesViewComponent } from './practices/practices-view.component';
import { SchedulingContainerComponent } from './scheduling-container.component';
import { SchedulingDashboardComponent } from './scheduling-dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: SchedulingContainerComponent,
    children: [
      {
        path: 'practices',
        component: PracticesContainerComponent,
        children: [
          {
            path: ':practiceId',
            component: PracticesViewComponent,
          },
          {
            path: '**',
            component: PracticesListComponent,
          },
        ],
      },
      {
        path: '**',
        component: SchedulingDashboardComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SchedulingRoutingModule {}
