import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { PracticesViewComponent } from './practices-view.component';

describe('PracticesViewComponent', () => {
  let component: PracticesViewComponent;
  let fixture: ComponentFixture<PracticesViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PracticesViewComponent],
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
    fixture = TestBed.createComponent(PracticesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
