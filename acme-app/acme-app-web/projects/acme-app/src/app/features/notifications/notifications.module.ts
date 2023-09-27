import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatLineModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NotificationsListComponent } from './notifications-list.component';
import { NotificationsRoutingModule } from './notifications-routing.module';
import { NotificationsViewComponent } from './notifications-view.component';
import { NotificationsComponent } from './notifications.component';

@NgModule({
  imports: [
    CommonModule,
    MatCardModule,
    MatToolbarModule,
    MatDividerModule,
    MatListModule,
    MatLineModule,
    MatIconModule,
    NotificationsRoutingModule,
  ],
  declarations: [
    NotificationsComponent,
    NotificationsListComponent,
    NotificationsViewComponent,
  ],
})
export class NotificationsModule {}
