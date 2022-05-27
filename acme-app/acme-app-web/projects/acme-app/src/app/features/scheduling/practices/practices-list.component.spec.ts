import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { PracticesListComponent } from './practices-list.component';

describe('PracticesListComponent', () => {
  let component: PracticesListComponent;
  let fixture: ComponentFixture<PracticesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PracticesListComponent],
      imports: [
        BrowserModule,
        MatFormFieldModule,
        MatInputModule,
        MatTableModule,
        NoopAnimationsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([])
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PracticesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
