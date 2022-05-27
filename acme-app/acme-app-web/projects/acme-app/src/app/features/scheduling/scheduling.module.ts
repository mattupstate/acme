import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { PracticesContainerComponent } from './practices-container.component';
import { PracticesFormComponent } from './practices/practices-form.component';
import { PracticesListComponent } from './practices/practices-list.component';
import { PracticesViewComponent } from './practices/practices-view.component';
import { SchedulingContainerComponent } from './scheduling-container.component';
import { SchedulingDashboardComponent } from './scheduling-dashboard.component';
import { SchedulingRoutingModule } from './scheduling-routing.module';

@NgModule({
  declarations: [
    SchedulingContainerComponent,
    PracticesListComponent,
    PracticesViewComponent,
    SchedulingDashboardComponent,
    PracticesContainerComponent,
    PracticesFormComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    SchedulingRoutingModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatTableModule,
  ],
})
export class SchedulingModule {}
