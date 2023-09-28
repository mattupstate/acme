import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { requestRecoveryCode, submitRecoveryCode } from '../../app.actions';
import {
  selectBusy,
  selectRecoveryCodeRequested,
  selectRecoverError,
} from '../../app.selectors';

@Component({
  selector: 'app-recover-container',
  styleUrls: ['./recover-container.component.less'],
  templateUrl: './recover-container.component.html',
})
export class RecoverContainerComponent {
  errorCode$ = this.store.select(selectRecoverError);
  busy$ = this.store.select(selectBusy);
  requested$ = this.store.select(selectRecoveryCodeRequested);

  constructor(private store: Store) {}

  dispatch(data: { email: string }) {
    this.store.dispatch(requestRecoveryCode(data));
  }

  dispatchCode(data: { email: string; code: string }) {
    this.store.dispatch(submitRecoveryCode(data));
  }
}
