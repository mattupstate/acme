import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { PracticesFormComponent } from './practices-form.component';

describe('PracticesFormComponent', () => {
  let component: PracticesFormComponent;
  let fixture: ComponentFixture<PracticesFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PracticesFormComponent],
      imports: [
        BrowserModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
        ReactiveFormsModule,
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PracticesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
