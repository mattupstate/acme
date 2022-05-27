import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { first } from 'rxjs/operators';
import { RecoverComponent } from './recover.component';

describe('RecoverComponent', () => {
  let component: RecoverComponent;
  let fixture: ComponentFixture<RecoverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecoverComponent],
      imports: [
        BrowserModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch formSubmit event', () => {
    let formData = {email: 'hello@world.com'};
    component.formSubmit.pipe(first()).subscribe((event) => expect(event).toBe(formData));
    component.onSubmit(formData);
  });

  it('should dispatch formReset event', () => {
    component.formReset.pipe(first()).subscribe((event) => expect(event).toBeUndefined());
    component.reset();
    expect(component.requested).toBeFalse();
    expect(component.recoverForm.errors).toBeNull();
    expect(component.recoverForm.dirty).toBeFalse();
    expect(component.recoverForm.touched).toBeFalse();
  });
});
