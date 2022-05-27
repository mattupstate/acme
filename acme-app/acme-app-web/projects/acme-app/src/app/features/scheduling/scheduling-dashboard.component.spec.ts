import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SchedulingDashboardComponent } from './scheduling-dashboard.component';

describe('SchedulingDashboardComponent', () => {
  let component: SchedulingDashboardComponent;
  let fixture: ComponentFixture<SchedulingDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SchedulingDashboardComponent],
      imports: [
        RouterTestingModule.withRoutes([])
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulingDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
