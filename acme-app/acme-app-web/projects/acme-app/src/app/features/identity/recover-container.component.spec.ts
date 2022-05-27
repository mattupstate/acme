import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { ApplicationState, StartupState } from '../../app.state';
import { RecoverContainerComponent } from './recover-container.component';
import { RecoverComponent } from './recover/recover.component';

describe('RecoverMediatorComponent', () => {
  let component: RecoverContainerComponent;
  let fixture: ComponentFixture<RecoverContainerComponent>;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecoverContainerComponent, RecoverComponent],
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
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecoverContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
