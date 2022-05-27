import { Component, Input } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface PracticeListItem {
  id: string;
  name: string;
  location: string;
  resourceUrl: string;
}

@Component({
  selector: 'app-practices-list',
  templateUrl: './practices-list.component.html',
  styleUrls: ['./practices-list.component.less'],
})
export class PracticesListComponent {
  displayedColumns: string[] = ['name', 'location'];

  @Input()
  dataSource: Observable<Array<PracticeListItem>> = of([
    {
      id: 'abc',
      name: 'Hello World',
      location: '123 Somewhere Avenue',
      resourceUrl: 'https://api.acme.minikube/scheduling/practices/abc',
    },
  ]);

  constructor() {}
}
