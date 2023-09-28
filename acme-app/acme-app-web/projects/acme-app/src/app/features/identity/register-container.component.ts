import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { register, registerViaOpenId } from '../../app.actions';
import { selectBusy, selectRegistrationError, selectRegistrationRequested } from '../../app.selectors';

@Component({
  selector: 'app-register-container',
  styleUrls: ["./register-container.component.less"],
  templateUrl: './register-container.component.html',
})
export class RegisterContainerComponent {
  asyncErrors$ = this.store.select(selectRegistrationError)
  busy$ = this.store.select(selectBusy);
  requested$ = this.store.select(selectRegistrationRequested);

  constructor(private store: Store) { }

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
