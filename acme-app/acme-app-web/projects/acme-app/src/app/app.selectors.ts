import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ApplicationState, SecurityState } from './app.state';

export const selectStartupState = (state: ApplicationState) => state.startup;

export const selectBusy = (state: ApplicationState) => state.busy;

export const selectSecurity = createFeatureSelector<
  ApplicationState,
  SecurityState
>('security');

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
  (state: SecurityState) => state.signInError
);

export const selectRecoverError = createSelector(
  selectSecurity,
  (state: SecurityState) => state.recoveryError
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
  (state: SecurityState)  => state.verifyError
)