import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {
  Actions,
  createEffect,
  EffectNotification,
  ofType,
  OnRunEffects,
} from '@ngrx/effects';
import { Observable, of } from 'rxjs';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import * as actions from './app.actions';
import { GlobalErrorsService } from './app.global-errors.service';
import { SignInError } from './app.state';

import {
  IdentityService,
  IdentityServiceRegistrationError,
  IdentityServiceSignInError,
} from './features/identity/identity.service';

@Injectable()
export class StartupAffects implements OnRunEffects {
  constructor(
    private actions$: Actions,
    private authService: IdentityService
  ) {}

  appStartup$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.appStartup),
      switchMap(() =>
        this.authService.whoAmI().pipe(
          map((principal) => actions.appStartupComplete({ principal })),
          catchError(() => of(actions.appStartupComplete({ principal: null })))
        )
      )
    );
  });

  ngrxOnRunEffects(
    resolvedEffects$: Observable<EffectNotification>
  ): Observable<EffectNotification> {
    return resolvedEffects$.pipe(
      takeUntil(this.actions$.pipe(ofType(actions.appStartupComplete)))
    );
  }
}

@Injectable()
export class AppEffects implements OnRunEffects {
  signIn$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.signIn),
      exhaustMap((action) =>
        this.authService.signIn(action.email, action.password).pipe(
          map((userProfile) =>
            actions.signInComplete({ principal: userProfile })
          ),
          catchError((e) => {
            if (e instanceof IdentityServiceSignInError) {
              return of(
                actions.signInFailure({
                  error: {
                    INVALID_CREDENTIALS: SignInError.INVALID_CREDENTIALS,
                    INACTIVE_ACCOUNT: SignInError.INACTIVE_ACCOUNT,
                    UNKNOWN: SignInError.UNEXPECTED,
                  }[e.cause],
                })
              );
            } else {
              return of(
                actions.signInFailure({
                  error: SignInError.UNEXPECTED,
                })
              );
            }
          })
        )
      )
    );
  });

  signInViaOpenId$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.signInViaOpenId),
      exhaustMap((action) =>
        this.authService
          .signInViaOpenId(action.provider)
          .pipe(
            map((redirectUrl) => actions.externalLink({ url: redirectUrl }))
          )
      )
    );
  });

  register$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.register),
      exhaustMap((action) =>
        this.authService
          .register(
            action.email,
            action.password,
            action.givenName,
            action.familyName
          )
          .pipe(
            map(() => actions.registerComplete()),
            catchError((error: IdentityServiceRegistrationError) => {
              return of(
                actions.registerFailure({
                  error: {
                    errors: error.errors,
                    attributes: {
                      givenName: error.attributes.givenName,
                      familyName: error.attributes.familyName,
                      password: error.attributes.password,
                      email: error.attributes.email,
                    },
                  },
                })
              );
            })
          )
      )
    );
  });

  registerViaOpenId$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.registerViaOpenId),
      exhaustMap((action) =>
        this.authService
          .registerViaOpenId(action.provider)
          .pipe(
            map((redirectUrl) => actions.externalLink({ url: redirectUrl }))
          )
      )
    );
  });

  redirectToDashboard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actions.signInComplete),
        tap((action) => this.router.navigate(['']))
      );
    },
    { dispatch: false }
  );

  requestRecoveryCode$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.requestRecoveryCode),
      exhaustMap((action) =>
        this.authService.recover(action.email).pipe(
          map((res) => actions.recoveryCodeSent()),
          catchError((error: IdentityServiceRegistrationError) => {
            return of(
              actions.registerFailure({
                error: {
                  errors: error.errors,
                  attributes: {
                    givenName: error.attributes.givenName,
                    familyName: error.attributes.familyName,
                    password: error.attributes.password,
                    email: error.attributes.email,
                  },
                },
              })
            );
          })
        )
      )
    );
  });

  submitRecoveryCode$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.submitRecoveryCode),
      exhaustMap((action) =>
        this.authService
          .recover(action.email, action.code)
          .pipe(map((res) => actions.recoveryCompleted()))
      )
    );
  });

  verifyAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.verifyAccount),
      exhaustMap((action) =>
        this.authService
          .verify(action.email)
          .pipe(map((res) => actions.verifyRequestComplete()))
      )
    );
  });

  redirectToSignIn$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actions.logoutComplete, actions.unauthenticatedAccess),
        tap((res) => this.router.navigate(['sign-in']))
      );
    },
    { dispatch: false }
  );

  displayUnauthenticatedAccessError$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actions.unauthenticatedAccess),
        tap((res) => this.globalErrors.unauthenticatedAccess())
      );
    },
    { dispatch: false }
  );

  redirectToRoot$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actions.invalidAccess),
        tap((res) => this.router.navigate(['']))
      );
    },
    { dispatch: false }
  );

  logout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actions.logout),
      exhaustMap(() =>
        this.authService.logout().pipe(
          map(() => actions.logoutComplete()),
          catchError((error) => of(actions.logoutFailure({ error })))
        )
      )
    );
  });

  redirect$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actions.externalLink),
        tap((action) => window.location.replace(action.url))
      );
    },
    { dispatch: false }
  );

  constructor(
    private authService: IdentityService,
    private globalErrors: GlobalErrorsService,
    private actions$: Actions,
    private router: Router
  ) {}

  ngrxOnRunEffects(
    resolvedEffects$: Observable<EffectNotification>
  ): Observable<EffectNotification> {
    return this.actions$.pipe(
      ofType(actions.appStartupComplete),
      mergeMap(() => resolvedEffects$)
    );
  }
}
