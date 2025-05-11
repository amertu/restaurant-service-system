import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingAddComponent } from './billing-add.component';

describe('ReservationAddComponent', () => {
  let component: BillingAddComponent;
  let fixture: ComponentFixture<BillingAddComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BillingAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BillingAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
