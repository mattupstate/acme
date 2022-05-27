import { ActionReducerMap, createReducer, on } from '@ngrx/store';

import * as actions from './app.actions';

export enum StartupState {
  REQUEST,
  COMPLETE,
}

export interface SecurityState {
  principal: Principal | null
  signInError: SignInError | null
  registrationRequested: boolean
  registrationError: RegistrationError | null
  recoveryError: RecoveryError | null
  recoveryRequested: boolean
  verifyRequested: boolean
  verifyError: VerifyError | null
}

export interface Principal {
  id: string
  email: string
  name: {
    given: string
    family: string
    preferred: string
    prefix?: string
    suffix?: string
  }
}

export enum SignInError {
  INVALID_CREDENTIALS,
  INACTIVE_ACCOUNT,
  UNEXPECTED
}

export interface RegistrationError {
  errors: Array<string>
  attributes: {
    givenName: Array<string>
    familyName: Array<string>
    email: Array<string>
    password: Array<string>
  }
}

export interface RecoveryError {
  errors: Array<string>
}

export interface VerifyError {
  errors: Array<string>
}

export interface ApplicationState {
  busy: boolean;
  startup: StartupState;
  security: SecurityState;
}

export const startupReducer = createReducer<StartupState>(
  StartupState.REQUEST,
  on(actions.appStartupComplete, (): StartupState => {
    return StartupState.COMPLETE;
  })
);

export const busyReducer = createReducer<boolean>(
  false,
  on(actions.signIn, (): boolean => true),
  on(actions.signInComplete, (): boolean => false),
  on(actions.signInFailure, (): boolean => false),
  on(actions.logout, (): boolean => true),
  on(actions.logoutComplete, (): boolean => false),
  on(actions.logoutFailure, (): boolean => false)
);

export const securityReducer = createReducer<SecurityState>(
  {
    principal: null,
    recoveryRequested: false,
    signInError: null,
    recoveryError: null,
    registrationRequested: false,
    registrationError: null,
    verifyRequested: false,
    verifyError: null,
  },
  on(actions.appStartupComplete, (state: SecurityState, props): SecurityState => ({
    ...state,
    principal: props.principal,
    signInError: null,
  })),
  on(actions.signIn, (state: SecurityState): SecurityState => ({
    ...state,
    principal: null,
    signInError: null,
  })),
  on(actions.signInComplete, (state: SecurityState, props): SecurityState => ({
    ...state,
    principal: props.principal,
    signInError: null,
  })),
  on(actions.signInFailure, (state: SecurityState, props): SecurityState => ({
    ...state,
    principal: null,
    signInError: props.error,
  })),
  on(actions.logoutComplete, (state: SecurityState): SecurityState => ({
    ...state,
    principal: null,
  })),
  on(actions.unauthorizedAccess, (state: SecurityState, props): SecurityState => ({
    ...state,
    principal: null,
    signInError: props.error,
  })),
  on(actions.register, (state: SecurityState, props): SecurityState => ({
    ...state,
    registrationRequested: true,
  })),
  on(actions.registerComplete, (state: SecurityState, props): SecurityState => ({
    ...state,
    registrationRequested: true,
    registrationError: null,
  })),
  on(actions.registerFailure, (state: SecurityState, props): SecurityState => ({
    ...state,
    principal: null,
    registrationError: props.error,
  })),
  on(actions.recoverAccount, (state: SecurityState): SecurityState => ({
    ...state,
    recoveryRequested: false,
  })),
  on(actions.recoverRequestComplete, (state: SecurityState): SecurityState => ({
    ...state,
    recoveryRequested: true,
  })),
  on(actions.verifyAccount, (state: SecurityState): SecurityState => ({
    ...state,
    verifyRequested: false,
  })),
  on(actions.verifyRequestComplete, (state: SecurityState): SecurityState => ({
    ...state,
    verifyRequested: true,
  })),
);

export const reducers: ActionReducerMap<ApplicationState> = {
  busy: busyReducer,
  startup: startupReducer,
  security: securityReducer,
};
