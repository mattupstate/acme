import { Component } from '@angular/core';

@Component({
  selector: 'app-feature-notifications-list',
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.less'],
})
export class NotificationsListComponent {
  notifications = Array(5);

  constructor() {}
}
