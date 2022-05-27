import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-practices-list-item',
  templateUrl: './practices-list-item.component.html',
  styleUrls: ['./practices-list-item.component.less'],
})
export class PracticesListItemComponent {
  @Input() name: String = '';
  @Input() location: String = '';

  constructor() {}
}
