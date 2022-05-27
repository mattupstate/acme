import { BreakpointObserver } from '@angular/cdk/layout';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class DeviceAwarenessService {
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe('(max-width: 767px)')
    .pipe(map((result) => result.matches));

  constructor(private breakpointObserver: BreakpointObserver) {}
}
