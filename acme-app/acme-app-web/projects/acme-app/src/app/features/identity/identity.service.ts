import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import {
  Identity as KratosIdentity,
  SelfServiceLoginFlow,
  SelfServiceLogoutUrl,
  SelfServiceRecoveryFlow,
  SelfServiceRegistrationFlow,
  SelfServiceBrowserLocationChangeRequiredError,
  Session,
  UiNodeInputAttributes,
  SelfServiceVerificationFlow,
} from '@ory/kratos-client';
import { EMPTY, Observable, of, throwError } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';

export interface RefreshResult {
  refreshed: boolean;
  token?: string;
}

export interface Identity {
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

export abstract class IdentityService {
  /**
   * Initialize the auth service, return true if the client has an existing session with the authentication service.
   */
  abstract whoAmI(): Observable<Identity>;

  /**
   * Navigate to the authentication service login interface.
   */
  abstract signIn(email: string, password: string): Observable<Identity>;

  abstract signInViaOpenId(provider: string): Observable<string>;

  abstract logout(): Observable<void>;

  abstract register(
    email: string,
    password: string,
    givenName: string,
    familyName: string
  ): Observable<void>;

  abstract registerViaOpenId(provider: string): Observable<string>;

  abstract recover(email: string, code?: string): Observable<void>;

  abstract verify(email: string): Observable<void>;
}

function extractCsrfToken(
  res:
    | SelfServiceLoginFlow
    | SelfServiceRegistrationFlow
    | SelfServiceRecoveryFlow
    | SelfServiceVerificationFlow
) {
  const inputAttrs = res.ui.nodes
    .map((n) => n.attributes)
    .filter((a) => 'name' in a && 'value' in a) as UiNodeInputAttributes[];
  return inputAttrs.filter((a) => a.name === 'csrf_token')[0].value;
}

function mapKratosIdentityToIdentity(identity: KratosIdentity): Identity {
  return {
    id: identity.id,
    name: identity.traits.name,
    email: identity.traits.email,
  };
}

function extractAttributeErrorMessages(res:
  | SelfServiceLoginFlow
  | SelfServiceRegistrationFlow
  | SelfServiceRecoveryFlow
  | SelfServiceVerificationFlow
): {
  givenName: Array<string>
  familyName: Array<string>
  email: Array<string>
  password: Array<string>
} {
  const errors: { [field: string]: Array<string> } = res.ui.nodes
    .map((node) => ({
      field: (node.attributes as UiNodeInputAttributes).name,
      messages: node.messages.filter((msg) => msg.type === 'error'),
    }))
    .reduce((ac, a) => ({ ...ac, [a.field]: a.messages.map(msg => msg.text) }), {});
  return {
    givenName: errors['traits.name.given'],
    familyName: errors['traits.name.family'],
    password: errors['password'],
    email: errors['traits.email'],
  }
}

function extractRootErrorMessages(
  res:
    | SelfServiceLoginFlow
    | SelfServiceRegistrationFlow
    | SelfServiceRecoveryFlow
    | SelfServiceVerificationFlow
): Array<string> {
  return res.ui.messages && res.ui.messages
    .filter((msg) => msg.type === 'error')
    .map((msg) => msg.text) || [];
}

function extractErrorMessages(
  res:
    | SelfServiceLoginFlow
    | SelfServiceRegistrationFlow
    | SelfServiceRecoveryFlow
    | SelfServiceVerificationFlow
) {
  return {
    '': extractRootErrorMessages(res),
    ...extractNodeErrorMessages(res),
  };
}

function extractNodeErrorMessages(
  res:
    | SelfServiceLoginFlow
    | SelfServiceRegistrationFlow
    | SelfServiceRecoveryFlow
    | SelfServiceVerificationFlow
): {
  [field: string]: Array<string>;
} {
  return res.ui.nodes
    .map((node) => ({
      field: (node.attributes as UiNodeInputAttributes).name,
      messages: node.messages.filter((msg) => msg.type === 'error'),
    }))
    .filter((node) => node.messages.length > 0)
    .reduce((ac, a) => ({ ...ac, [a.field]: a.messages }), {});
}

function extractSignInCause(res:
  | SelfServiceLoginFlow
  | SelfServiceRegistrationFlow
  | SelfServiceRecoveryFlow): SignInErrorCause {
  return {
    4000006: SignInErrorCause.INVALID_CREDENTIALS,
    4000010: SignInErrorCause.INACTIVE_ACCOUNT,
  }[res.ui.messages?.[0]?.id || -1] || SignInErrorCause.UNKNOWN
}

export enum SignInErrorCause {
  INVALID_CREDENTIALS = "INVALID_CREDENTIALS",
  INACTIVE_ACCOUNT = "INACTIVE_ACCOUNT",
  UNKNOWN = "UNKNOWN",
}

export class IdentityServiceError { }

export class IdentityServiceSignInError extends IdentityServiceError {
  constructor(public cause: SignInErrorCause, public errors: { [field: string]: Array<string> }) {
    super()
  }
}

export class IdentityServiceRecoveryError extends IdentityServiceError {
  constructor(public errors: { [field: string]: Array<string> }) {
    super()
  }
}

export class IdentityServiceVerifyError extends IdentityServiceError {
  constructor(public errors: { [field: string]: Array<string> }) {
    super()
  }
}

export class IdentityServiceRegistrationError extends IdentityServiceError {
  constructor(public errors: Array<string>, public attributes: {
    givenName: Array<string>
    familyName: Array<string>
    email: Array<string>
    password: Array<string>
  }) {
    super()
  }
}

export class IdentityServiceUnauthorizedError extends IdentityServiceError { }

export class IdentityServiceUnexpectedError extends IdentityServiceError {
  constructor(public res: HttpErrorResponse) {
    super()
  }
}

export class KratosIdentityService implements IdentityService {
  constructor(
    private http: HttpClient,
    private baseUrl: string,
    private registrationReturnTo: string,
    private recoveryReturnTo: string,
  ) { }

  whoAmI(): Observable<Identity> {
    return this.http
      .get<Session>(`${this.baseUrl}/sessions/whoami`)
      .pipe(
        map((res: Session) => mapKratosIdentityToIdentity(res.identity)),
        catchError((res: HttpErrorResponse) => {
          if (res.status === 401) {
            return throwError(new IdentityServiceUnauthorizedError());
          } else {
            return throwError(new IdentityServiceUnexpectedError(res));
          }
        })
      );
  }

  signIn(email: string, password: string): Observable<Identity> {
    return this.http
      .get<SelfServiceLoginFlow>(`${this.baseUrl}/self-service/login/browser`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post<{ session: Session }>(
              res.ui.action,
              {
                password_identifier: email,
                password: password,
                method: 'password',
                csrf_token: extractCsrfToken(res),
              }
            )
            .pipe(
              map((res: { session: Session }) =>
                mapKratosIdentityToIdentity(res.session.identity)
              ),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceSignInError(
                      extractSignInCause(res.error as SelfServiceLoginFlow),
                      extractErrorMessages(res.error as SelfServiceLoginFlow)
                    )
                  );
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  signInViaOpenId(provider: string): Observable<string> {
    return this.http
      .get<SelfServiceRegistrationFlow>(`${this.baseUrl}/self-service/login/browser`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post<Session>(
              res.ui.action,
              {
                provider,
                method: 'oidc',
                csrf_token: extractCsrfToken(res),
              }
            )
            .pipe(
              map((res: Session) => 'google'),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceSignInError(
                      extractSignInCause(res.error as SelfServiceLoginFlow),
                      extractAttributeErrorMessages(res.error as SelfServiceLoginFlow)
                    )
                  );
                } else if (res.status === 422) {
                  const body = res.error as SelfServiceBrowserLocationChangeRequiredError
                  if (body.redirect_browser_to) {
                    return of(body.redirect_browser_to)
                  } else {
                    return throwError(
                      new IdentityServiceSignInError(
                        extractSignInCause(res.error as SelfServiceLoginFlow),
                        extractAttributeErrorMessages(res.error as SelfServiceLoginFlow)
                      )
                    );
                  }
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  registerViaOpenId(provider: string): Observable<string> {
    return this.http
      .get<SelfServiceRegistrationFlow>(`${this.baseUrl}/self-service/registration/browser?return_to=${this.registrationReturnTo}`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post<Session>(
              res.ui.action,
              {
                provider,
                method: 'oidc',
                csrf_token: extractCsrfToken(res),
              }
            )
            .pipe(
              map((res: Session) => 'google'),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceRegistrationError(
                      extractRootErrorMessages(res.error as SelfServiceRegistrationFlow),
                      extractAttributeErrorMessages(res.error as SelfServiceRegistrationFlow)
                    )
                  );
                } else if (res.status === 422) {
                  const body = res.error as SelfServiceBrowserLocationChangeRequiredError
                  return of(body.redirect_browser_to!)
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  register(
    email: string,
    password: string,
    givenName: string,
    familyName: string
  ): Observable<void> {
    return this.http
      .get<SelfServiceRegistrationFlow>(`${this.baseUrl}/self-service/registration/browser?verified=true&after_verification_return_to=${this.registrationReturnTo}`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post<Session>(
              res.ui.action,
              {
                traits: {
                  email,
                  name: {
                    given: givenName,
                    family: familyName,
                    preferred: givenName
                  },
                },
                method: 'password',
                password,
                csrf_token: extractCsrfToken(res),
              }
            )
            .pipe(
              map(() => { }),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceRegistrationError(
                      extractRootErrorMessages(res.error as SelfServiceRegistrationFlow),
                      extractAttributeErrorMessages(res.error as SelfServiceRegistrationFlow)
                    )
                  );
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  verify(email: string): Observable<void> {
    return this.http
      .get<SelfServiceVerificationFlow>(`${this.baseUrl}/self-service/verification/browser`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post(
              res.ui.action,
              {
                csrf_token: extractCsrfToken(res),
                email: email,
                method: 'link',
              }
            )
            .pipe(
              map(() => { }),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceVerifyError(
                      extractErrorMessages(res.error as SelfServiceVerificationFlow)
                    )
                  );
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  recover(email: string, code?: string): Observable<void> {
    return this.http
      .get<SelfServiceRecoveryFlow>(`${this.baseUrl}/self-service/recovery/browser?return_to=${this.recoveryReturnTo}`)
      .pipe(
        mergeMap((res) =>
          this.http
            .post(
              res.ui.action,
              {
                csrf_token: extractCsrfToken(res),
                email,
                method: 'code',
                code
              }
            )
            .pipe(
              map(() => { }),
              catchError((res: HttpErrorResponse) => {
                if (res.status === 400) {
                  return throwError(
                    new IdentityServiceRecoveryError(
                      extractErrorMessages(res.error as SelfServiceRecoveryFlow)
                    )
                  );
                } else {
                  return throwError(new IdentityServiceUnexpectedError(res));
                }
              })
            )
        )
      );
  }

  logout(): Observable<void> {
    return this.http
      .get<SelfServiceLogoutUrl>(`${this.baseUrl}/self-service/logout/browser`)
      .pipe(
        mergeMap((res) =>
          this.http
            .get(res.logout_url!)
            .pipe(map(() => { }))
        )
      );
  }

  updateToken(minValidity: number): Observable<RefreshResult> {
    return EMPTY;
  }
}
