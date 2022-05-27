import { Injectable } from '@angular/core';
import { CanActivate, UrlTree } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { invalidAccess, unauthenticatedAccess } from './app.actions';
import { selectAuthenticated } from './app.selectors';
import { ApplicationState } from './app.state';

@Injectable()
export class IsAuthenticated implements CanActivate {
  constructor(private store: Store<ApplicationState>) {}

  canActivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.store.select(selectAuthenticated).pipe(
      map((authenticated) => {
        if (!authenticated) {
          this.store.dispatch(unauthenticatedAccess());
          return false;
        }
        return true;
      }),
      take(1)
    );
  }
}

@Injectable()
export class IsUnauthenticated implements CanActivate {
  constructor(private store: Store<ApplicationState>) {}

  canActivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.store.select(selectAuthenticated).pipe(
      map((authenticated) => {
        if (authenticated) {
          this.store.dispatch(invalidAccess());
          return false;
        }
        return true;
      }),
      take(1)
    );
  }
}
