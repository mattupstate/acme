import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { StartupState } from '../../app.state';
import { RegisterContainerComponent } from './register-container.component';
import { RegisterComponent } from './register/register.component';

describe('RegisterMediatorComponent', () => {
  let component: RegisterContainerComponent;
  let fixture: ComponentFixture<RegisterContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterContainerComponent, RegisterComponent],
      providers: [
        provideMockStore({
          initialState: {
            busy: false,
            startup: StartupState.COMPLETE,
            security: {
              principal: null,
              signInError: null,
              registrationRequested: false,
              registrationError: null,
              recoveryError: null,
              recoveryRequested: false,
              verifyRequested: false,
              verifyError: null,
            }
          }
        })
      ],
      imports: [
        MatCardModule,
        MatDialogModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        NoopAnimationsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
