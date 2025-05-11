import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationFinishComponent } from './reservation-finish.component';

describe('ReservationFinishComponent', () => {
  let component: ReservationFinishComponent;
  let fixture: ComponentFixture<ReservationFinishComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ReservationFinishComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReservationFinishComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
