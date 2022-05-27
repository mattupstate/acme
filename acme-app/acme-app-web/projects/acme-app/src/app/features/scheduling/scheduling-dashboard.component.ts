import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError, publish, tap } from 'rxjs/operators';

@Component({
  selector: 'app-scheduling-dashboard',
  templateUrl: './scheduling-dashboard.component.html',
  styleUrls: ['./scheduling-dashboard.component.less'],
})
export class SchedulingDashboardComponent {
  constructor() {}

  callApi(e: Event) {
    e.preventDefault();
    // this.http.get('https://api-127-0-0-1.nip.io/scheduling', {
    //   observe: 'body',
    //   responseType: 'json',
    // }).pipe(
    //   catchError((res) => {
    //     console.log(res)
    //     return throwError(res)
    //   })
    // ).subscribe((res) => console.log(res))
  }
}
