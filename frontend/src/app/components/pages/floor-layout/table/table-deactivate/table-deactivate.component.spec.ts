import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableDeactivateComponent } from './table-deactivate.component';

describe('TableDeactivateComponent', () => {
  let component: TableDeactivateComponent;
  let fixture: ComponentFixture<TableDeactivateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TableDeactivateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableDeactivateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
