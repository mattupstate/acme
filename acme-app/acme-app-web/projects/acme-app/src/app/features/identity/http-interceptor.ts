import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
  } from '@angular/common/http';
  import { Injectable } from '@angular/core';
  import { Observable } from 'rxjs';

  @Injectable()
  export class IdentityServiceHttpInterceptor implements HttpInterceptor {
    constructor() {}

    intercept(
      req: HttpRequest<any>,
      next: HttpHandler
    ): Observable<HttpEvent<any>> {
      return next.handle(
        req.clone({
          responseType: 'json',
          withCredentials: true,
        })
      );
    }
  }
