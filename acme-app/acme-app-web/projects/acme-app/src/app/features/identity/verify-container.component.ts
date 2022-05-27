import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { verifyAccount } from '../../app.actions';
import { selectBusy, selectVerifyRequested, selectVerifyError } from '../../app.selectors';
import { ApplicationState } from '../../app.state';
import { IdentityServiceVerifyError } from './identity.service';

@Component({
  selector: 'app-verify-container',
  template: `
    <div fxLayout="row" fxLayoutAlign="center">
      <app-verify
        fxFlex.xs
        fxFlex.gt-xs="400px"
        [toggleView]="(requested$ | async)"
        [errorCode]="errorCode$ | async"
        [enabled]="(busy$ | async) === false"
        (formSubmit)="dispatch($event)"
      >
      </app-verify>
    </div>
  `,
})
export class VerifyContainerComponent {
  errorCode$ = this.store.select(selectVerifyError).pipe(
    map((error) => {
      if (error != null) {
        switch (error.constructor) {
          case IdentityServiceVerifyError:
            return 1;
          default:
            return 100;
        }
      }
      return null;
    })
  );

  busy$ = this.store.select(selectBusy);

  requested$ = this.store.select(selectVerifyRequested);

  constructor(private store: Store<ApplicationState>) {}

  dispatch(data: { email: string }) {
    this.store.dispatch(verifyAccount(data));
  }
}
