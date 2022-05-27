import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { recoverAccount } from '../../app.actions';
import { selectBusy, selectRecoveryRequested, selectRecoverError } from '../../app.selectors';
import { IdentityServiceRecoveryError } from './identity.service';

@Component({
  selector: 'app-recover-container',
  template: `
    <div fxLayout="row" fxLayoutAlign="center">
      <app-recover
        fxFlex.xs
        fxFlex.gt-xs="400px"
        [toggleView]="(requested$ | async)"
        [errorCode]="errorCode$ | async"
        [enabled]="(busy$ | async) === false"
        (formSubmit)="dispatch($event)"
      >
      </app-recover>
    </div>
  `,
})
export class RecoverContainerComponent {
  errorCode$ = this.store.select(selectRecoverError);

  busy$ = this.store.select(selectBusy);

  requested$ = this.store.select(selectRecoveryRequested);

  constructor(private store: Store) {}

  dispatch(data: { email: string }) {
    this.store.dispatch(recoverAccount(data));
  }
}
