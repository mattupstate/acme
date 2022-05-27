import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, concatAll, map } from 'rxjs/operators';
import { IdentityService } from '../features/identity/identity.service';

@Injectable()
export class IdentityTokenHttpInterceptor implements HttpInterceptor {
  constructor(private auth: IdentityService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return this.auth.updateToken(60).pipe(
      map((result) =>
        next.handle(
          req.clone({
            withCredentials: true,
//             setHeaders: { Authorization: `Bearer ${result.token}` },
          })
        )
      ),
      catchError((err) => {
        this.auth.logout();
        return throwError(`Bad things: ${err}`);
      }),
      concatAll()
    );
  }
}
