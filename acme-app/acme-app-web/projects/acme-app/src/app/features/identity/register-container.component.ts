import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { register, registerViaOpenId } from '../../app.actions';
import { selectBusy, selectRegistrationError, selectRegistrationRequested } from '../../app.selectors';
import { ApplicationState } from '../../app.state';

@Component({
  selector: 'app-register-container',
  template: `
    <div fxLayout="row" fxLayoutAlign="center">
      <app-register
        fxFlex.xs
        fxFlex.gt-xs="400px"
        [toggleView]="(requested$ | async)"
        [asyncErrors]="asyncErrors$ | async"
        [enabled]="(busy$ | async) === false"
        (formSubmit)="onFormSubmit($event)"
        (openIdRequest)="onOpenIdRequest($event)"
      >
      </app-register>
    </div>
  `,
})
export class RegisterContainerComponent {
  asyncErrors$ = this.store.select(selectRegistrationError)
  busy$ = this.store.select(selectBusy);
  requested$ = this.store.select(selectRegistrationRequested);

  constructor(private store: Store<ApplicationState>) { }

  onFormSubmit(data: {
    email: string;
    password: string;
    givenName: string;
    familyName: string;
  }) {
    this.store.dispatch(register(data));
  }

  onOpenIdRequest(event: { provider: string} ) {
    this.store.dispatch(registerViaOpenId(event))
  }
}
