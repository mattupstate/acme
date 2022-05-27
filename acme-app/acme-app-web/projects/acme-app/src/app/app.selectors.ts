import { createFeatureSelector, createSelector } from '@ngrx/store';
import { SecurityState, SignInError, StartupState } from './app.state';
import { IdentityServiceRecoveryError, IdentityServiceVerifyError } from './features/identity/identity.service';

export const selectStartupState = createFeatureSelector<StartupState>('startup');

export const selectBusy = createFeatureSelector<boolean>('busy');

export const selectSecurity = createFeatureSelector<SecurityState>('security');

export const selectPrincipal = createSelector(
  selectSecurity,
  (state: SecurityState) => state.principal
);

export const selectAuthenticated = createSelector(
  selectPrincipal,
  (user) => !!user
);

export const selectRegistrationRequested = createSelector(
  selectSecurity,
  (state: SecurityState) => state.registrationRequested
);

export const selectRegistrationError = createSelector(
  selectSecurity,
  (state: SecurityState) => state.registrationError
);

export const selectSignInError = createSelector(
  selectSecurity,
  (state: SecurityState) => {
    let error = state.signInError;
    switch(error) {
      case SignInError.INVALID_CREDENTIALS:
        return 1;
      case SignInError.INACTIVE_ACCOUNT:
        return 2;
      case SignInError.UNEXPECTED:
        return 100;
      default:
        return null;
    }
  }
);

export const selectRecoverError = createSelector(
  selectSecurity,
  (state: SecurityState) => {
    let error = state.recoveryError;
    if (error != null) {
      switch (error.constructor) {
        case IdentityServiceRecoveryError:
          return 1;
        default:
          return 100;
      }
    }
    return null;
  }
);

export const selectRecoveryRequested = createSelector(
  selectSecurity,
  (state: SecurityState) => state.recoveryRequested
);

export const selectVerifyRequested = createSelector(
  selectSecurity,
  (state: SecurityState)  => state.verifyRequested
)

export const selectVerifyError = createSelector(
  selectSecurity,
  (state: SecurityState) => {
    let error = state.verifyError
    if (error != null) {
      switch (error.constructor) {
        case IdentityServiceVerifyError:
          return 1;
        default:
          return 100;
      }
    }
    return null;
  }
)