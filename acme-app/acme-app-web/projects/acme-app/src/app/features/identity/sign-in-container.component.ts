import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { signIn, signInViaOpenId } from '../../app.actions';
import { selectBusy, selectSignInError } from '../../app.selectors';

@Component({
  selector: 'app-sign-in-container',
  styleUrls: ["./sign-in-container.component.less"],
  templateUrl: "./sign-in-container.component.html",
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
