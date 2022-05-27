import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HalDocument, onHttpError } from '../../http/hal';
import { AcmeHostService } from '../../http/urls';

export abstract class PracticesCollectionService {
  protected constructor() {}

  abstract get(): Observable<HalDocument>;

  abstract post(body: { name: string }): Observable<HalDocument>;
}

@Injectable()
export class HttpPracticesCollectionService extends PracticesCollectionService {
  private resourceUrl: string;

  constructor(private http: HttpClient, hosts: AcmeHostService) {
    super();
    this.resourceUrl = `//${hosts.forService('api')}/practices`;
  }

  get() {
    return this.http
      .get<HalDocument>(this.resourceUrl)
      .pipe(catchError(onHttpError));
  }

  post(body: { name: string }) {
    return this.http
      .post<HalDocument>(this.resourceUrl, body)
      .pipe(catchError(onHttpError));
  }
}
