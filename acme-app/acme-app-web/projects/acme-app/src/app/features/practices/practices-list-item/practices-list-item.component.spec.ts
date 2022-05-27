import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';

import { PracticesListItemComponent } from './practices-list-item.component';

describe('PracticesListItemComponent', () => {
  let component: PracticesListItemComponent;
  let fixture: ComponentFixture<PracticesListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PracticesListItemComponent],
      imports: [
        MatCardModule
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PracticesListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
