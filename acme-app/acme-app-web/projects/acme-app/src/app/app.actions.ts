import { createAction, props } from '@ngrx/store';
import { SignInError, RegistrationError, Principal } from './app.state';

export const appStartup = createAction('@acme/startup');

export const appStartupComplete = createAction(
  '@acme/startupComplete',
  props<{ principal: Principal | null }>()
);

export const signIn = createAction(
  '@acme/signIn',
  props<{ email: string; password: string }>()
);

export const signInViaOpenId = createAction(
  '@acme/signInViaOpenId',
  props<{ provider: string }>()
)

export const signInComplete = createAction(
  '@acme/signInComplete',
  props<{ principal: Principal }>()
);
export const signInFailure = createAction(
  '@acme/signInFailure',
  props<{ error: SignInError }>()
);

export const registerFailure = createAction(
  '@acme/registerFailure',
  props<{ error: RegistrationError }>()
);

export const logout = createAction('@acme/logout');
export const logoutComplete = createAction('@acme/logoutComplete');
export const logoutFailure = createAction(
  '@acme/logoutFailure',
  props<{ error: any }>()
);

export const register = createAction(
  '@acme/register',
  props<{
    email: string;
    password: string;
    givenName: string;
    familyName: string;
  }>()
);

export const registerViaOpenId = createAction(
  '@acme/registerViaOpenid',
  props<{ provider: string }>()
)

export const registerComplete = createAction(
  '@acme/registerComplete'
);

export const unauthenticatedAccess = createAction(
  '@acme/unauthenticatedAccess'
);

export const unauthorizedAccess = createAction(
  '@acme/unauthorizedAccess',
  props<{ error: any }>()
);

export const invalidAccess = createAction('@acme/invalidAccess');

export const verifyAccount = createAction(
  '@acme/verifyAccount',
  props<{ email: string }>()
);

export const verifyRequestComplete = createAction('@acme/verifyRequestComplete');

export const recoverAccount = createAction(
  '@acme/recoverAccount',
  props<{ email: string }>()
);

export const recoverRequestComplete = createAction(
  '@acme/recoverRequestComplete'
);

export const externalLink = createAction(
  '@acme/externalLink',
  props<{ url: string }>()
)