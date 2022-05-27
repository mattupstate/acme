import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { logout } from './app.actions';
import { DeviceAwarenessService } from './device-awareness.service';
import { NotificationsAlertService } from './features/notifications/notifications-alert.service';

@Component({
  selector: 'app-root-container',
  templateUrl: './app-container.component.html',
  styleUrls: ['./app-container.component.less'],
})
export class AppContainerComponent {
  isHandset$: Observable<boolean>;
  hasNotifications$: Observable<boolean>;
  showSideNav = false;

  constructor(
    breakpointService: DeviceAwarenessService,
    notificationsAlertService: NotificationsAlertService,
    private store: Store
  ) {
    this.isHandset$ = breakpointService.isHandset$;
    this.hasNotifications$ = notificationsAlertService.hasNotifications$;
  }

  openSideNav() {
    this.showSideNav = true;
  }

  closeSideNav() {
    this.showSideNav = false;
  }

  manageAccount() {
    // this.store.dispatch(manageAccount());
  }

  logout() {
    this.store.dispatch(logout());
  }
}
