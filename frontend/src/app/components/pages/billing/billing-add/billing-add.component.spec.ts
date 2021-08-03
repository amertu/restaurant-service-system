import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingAddComponent } from './billing-add.component';

describe('ReservationAddComponent', () => {
  let component: BillingAddComponent;
  let fixture: ComponentFixture<BillingAddComponent>;

  beforeEach(async(() => {
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
