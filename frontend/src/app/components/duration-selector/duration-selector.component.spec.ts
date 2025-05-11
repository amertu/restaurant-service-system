import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { DurationSelectorComponent } from './duration-selector.component';

describe('DurationSelectorComponent', () => {
  let component: DurationSelectorComponent;
  let fixture: ComponentFixture<DurationSelectorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DurationSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DurationSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
