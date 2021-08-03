import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertContentForNgbModalComponent } from './alert-content-for-ngb-modal.component';

describe('AlertContentForNgbModalComponent', () => {
  let component: AlertContentForNgbModalComponent;
  let fixture: ComponentFixture<AlertContentForNgbModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlertContentForNgbModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertContentForNgbModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
