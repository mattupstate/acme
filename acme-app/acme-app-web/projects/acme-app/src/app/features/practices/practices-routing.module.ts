import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PracticesListComponent } from './practices-list.component';
import { PracticesViewComponent } from './practices-view.component';
import { PracticesComponent } from './practices.component';

const routes: Routes = [
  {
    path: '',
    component: PracticesComponent,
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
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PracticesRoutingModule {}
