import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

export const CONTENT_TYPE_HEADER_VALUE = 'Content-Type';
export const VND_ERROR_CONTENT_TYPE_VALUE = 'application/vnd.error+json';

export interface HalLink {
  href: string;
  title?: string;
  name?: string;
  templated?: boolean;
}

export interface HalLinkCollection {
  self: HalLink;

  [key: string]: HalLink | Array<HalLink>;
}

export interface HalEmbeddedCollection {
  [key: string]: HalDocument | Array<HalDocument>;
}

export interface HalDocument {
  _links: HalLinkCollection;
  _embedded?: HalEmbeddedCollection;

  [key: string]:
    | HalEmbeddedCollection
    | HalLinkCollection
    | string
    | number
    | boolean
    | object
    | null
    | undefined;
}

export interface VndError {
  message: string;
  logref?: number | string;
  path?: string;
  total?: number;
  _embedded?: {
    errors: Array<VndError>;
  };
}

export class VndErrorResponse {
  constructor(public response: VndError) {}
}

export class BadRequestError extends VndErrorResponse {
  constructor(response: VndError) {
    super(response);
  }
}

export class AuthenticationError extends VndErrorResponse {
  constructor(response: VndError) {
    super(response);
  }
}

export class AuthorizationError extends VndErrorResponse {
  constructor(response: VndError) {
    super(response);
  }
}

export class UnexpectedError extends VndErrorResponse {
  constructor(response: VndError) {
    super(response);
  }
}

export const onHttpError = (err: any) => {
  if (err instanceof HttpErrorResponse) {
    if (
      err.headers.get(CONTENT_TYPE_HEADER_VALUE) ===
      VND_ERROR_CONTENT_TYPE_VALUE
    ) {
      return throwError(onVndError(err.status, err.error as VndError));
    }
  }
  return throwError(err);
};

const onVndError = (status: number, response: VndError) => {
  return new (statusErrorTypes[status] ?? UnexpectedError)(response);
};

const statusErrorTypes: { [key: number]: any } = {
  400: BadRequestError,
  401: AuthenticationError,
  403: AuthorizationError,
};
