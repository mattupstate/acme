import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { signIn, signInViaOpenId } from '../../app.actions';
import { selectBusy, selectSignInError } from '../../app.selectors';
import { SignInError } from '../../app.state';

@Component({
  selector: 'app-sign-in-container',
  template: `
    <div fxLayout="row" fxLayoutAlign="center">
      <app-sign-in
        fxFlex.xs
        fxFlex.gt-xs="400px"
        [errorCode]="errorCode$ | async"
        [postVerify]="postVerify"
        [enabled]="(busy$ | async) === false"
        (formSubmit)="dispatch($event)"
        (openIdRequest)="onOpenIdRequest($event)">
      </app-sign-in>
    </div>
  `,
})
export class SignInContainerComponent {
  errorCode$ = this.store.select(selectSignInError);

  busy$: Observable<boolean> = this.store.select(selectBusy);

  postVerify = false;

  constructor(private store: Store, route: ActivatedRoute) {
    this.postVerify = route.snapshot.queryParamMap.has('flow') && route.snapshot.queryParamMap.has('verified')
  }

  dispatch(data: { email: string; password: string }) {
    this.store.dispatch(signIn(data));
  }

  onOpenIdRequest(event: { provider: string} ) {
    this.store.dispatch(signInViaOpenId(event))
  }
}
