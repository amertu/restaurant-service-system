import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { FloorLayoutComponent } from './floor-layout.component';

describe('FloorLayoutComponent', () => {
  let component: FloorLayoutComponent;
  let fixture: ComponentFixture<FloorLayoutComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ FloorLayoutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FloorLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
