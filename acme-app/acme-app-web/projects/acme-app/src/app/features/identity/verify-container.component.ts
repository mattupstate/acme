import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { verifyAccount } from '../../app.actions';
import { selectBusy, selectVerifyRequested, selectVerifyError } from '../../app.selectors';
import { IdentityServiceVerifyError } from './identity.service';

@Component({
  selector: 'app-verify-container',
  styleUrls: ['./verify-container.component.less'],
  templateUrl: './verify-container.component.html',
})
export class VerifyContainerComponent {
  errorCode$ = this.store.select(selectVerifyError);

  busy$ = this.store.select(selectBusy);

  requested$ = this.store.select(selectVerifyRequested);

  constructor(private store: Store) {}

  dispatch(data: { email: string }) {
    this.store.dispatch(verifyAccount(data));
  }
}
