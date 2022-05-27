import { Injectable } from '@angular/core';
import { timer } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class NotificationsAlertService {
  hasNotifications$ = timer(3000).pipe(map(() => true));

  constructor() {}
}
