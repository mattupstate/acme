import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PracticesContainerComponent } from './practices-container.component';

describe('PracicesContainerComponent', () => {
  let component: PracticesContainerComponent;
  let fixture: ComponentFixture<PracticesContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PracticesContainerComponent],
      imports: [
        RouterTestingModule.withRoutes([])
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PracticesContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
