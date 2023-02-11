import { createAction, props } from '@ngrx/store';
import { SignInError, RegistrationError, Principal } from './app.state';

export const appStartup = createAction('[@acme/app] startup');

export const appStartupComplete = createAction(
  '[@acme/app] startupComplete',
  props<{ principal: Principal | null }>()
);

export const signIn = createAction(
  '[@acme/app] signIn',
  props<{ email: string; password: string }>()
);

export const signInViaOpenId = createAction(
  '[@acme/app] signInViaOpenId',
  props<{ provider: string }>()
)

export const signInComplete = createAction(
  '[@acme/app] signInComplete',
  props<{ principal: Principal }>()
);
export const signInFailure = createAction(
  '[@acme/app] signInFailure',
  props<{ error: SignInError }>()
);

export const registerFailure = createAction(
  '[@acme/app] registerFailure',
  props<{ error: RegistrationError }>()
);

export const logout = createAction('[@acme/app] logout');
export const logoutComplete = createAction('[@acme/app] logoutComplete');
export const logoutFailure = createAction(
  '[@acme/app] logoutFailure',
  props<{ error: any }>()
);

export const register = createAction(
  '[@acme/app] register',
  props<{
    email: string;
    password: string;
    givenName: string;
    familyName: string;
  }>()
);

export const registerViaOpenId = createAction(
  '[@acme/app] registerViaOpenid',
  props<{ provider: string }>()
)

export const registerComplete = createAction(
  '[@acme/app] registerComplete'
);

export const unauthenticatedAccess = createAction(
  '[@acme/app] unauthenticatedAccess'
);

export const unauthorizedAccess = createAction(
  '[@acme/app] unauthorizedAccess',
  props<{ error: any }>()
);

export const invalidAccess = createAction('[@acme/app] invalidAccess');

export const verifyAccount = createAction(
  '[@acme/app] verifyAccount',
  props<{ email: string }>()
);

export const verifyRequestComplete = createAction('[@acme/app] verifyRequestComplete');

export const requestRecoveryCode = createAction(
  '[@acme/app] requestRecoveryCode',
  props<{ email: string }>()
);

export const recoveryCodeSent = createAction(
  '[@acme/app] recoveryCodeSent'
);

export const submitRecoveryCode = createAction(
  '[@acme/app] submitRecoveryCode',
  props<{ email: string, code: string }>()
)

export const recoveryCompleted = createAction(
  '[@acme/app] recoveryCompleted'
);

export const externalLink = createAction(
  '[@acme/app] externalLink',
  props<{ url: string }>()
)