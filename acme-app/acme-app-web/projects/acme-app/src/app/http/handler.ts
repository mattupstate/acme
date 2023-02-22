import { InjectionToken, Injector } from '@angular/core';
import {
  HttpBackend,
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * `HttpHandler` which applies an `HttpInterceptor` to an `HttpRequest`.
 */
class _HttpHandler implements HttpHandler {
  constructor(
    private next: HttpHandler,
    private interceptor: HttpInterceptor
  ) {}

  handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    return this.interceptor.intercept(req, this.next);
  }
}

export class CustomHttpHandler implements HttpHandler {
  private chain: HttpHandler | null = null;

  constructor(
    private backend: HttpBackend,
    private injector: Injector,
    private interceptors: InjectionToken<HttpInterceptor[]>
  ) {}

  handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    if (this.chain === null) {
      const interceptors = this.injector.get(this.interceptors, []);
      this.chain = interceptors.reduceRight(
        (next, interceptor) => new _HttpHandler(next, interceptor),
        this.backend
      );
    }
    return this.chain.handle(req);
  }
}
