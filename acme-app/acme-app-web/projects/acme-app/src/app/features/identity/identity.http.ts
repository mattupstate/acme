import {
  HttpBackend,
  HttpClient,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Injectable, InjectionToken, Injector } from '@angular/core';
import {
  SelfServiceLoginFlow,
  UiNodeInputAttributes,
} from '@ory/kratos-client';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CustomHttpHandler } from '../../http/handler';

function extractCsrfToken(res: SelfServiceLoginFlow) {
  const inputAttrs = res.ui.nodes
    .map((n) => n.attributes)
    .filter((a) => 'name' in a && 'value' in a) as UiNodeInputAttributes[];
  return inputAttrs.filter((a) => a.name === 'csrf_token')[0].value;
}

export const ORY_KRATOS_HTTP_INTERCEPTORS = new InjectionToken<
  HttpInterceptor[]
>('ORY_KRATOS_HTTP_INTERCEPTORS');

@Injectable()
export class OryKratosHttpClient extends HttpClient {
  constructor(backend: HttpBackend, private injector: Injector) {
    super(
      new CustomHttpHandler(backend, injector, ORY_KRATOS_HTTP_INTERCEPTORS)
    );
  }
}

@Injectable()
export class OryKratosHttpInterceptor implements HttpInterceptor {
  csrfToken?: string;
  volatileMethods: string[] = ['POST', 'PUT', 'PATCH', 'DELETE'];

  constructor() {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let baseReq = req.clone({
      responseType: 'json',
      withCredentials: true,
    });
    if (this.volatileMethods.indexOf(req.method.toUpperCase()) >= 0) {
      return next.handle(
        baseReq.clone({ body: { csrf_token: this.csrfToken, ...baseReq.body } })
      );
    } else {
      return next.handle(baseReq).pipe(
        tap((event: HttpEvent<any>) => {
          if (event instanceof HttpResponse) {
            this.csrfToken = extractCsrfToken(
              event.body as SelfServiceLoginFlow
            );
          }
        })
      );
    }
  }
}
