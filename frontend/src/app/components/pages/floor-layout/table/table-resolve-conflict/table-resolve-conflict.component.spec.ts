import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableResolveConflictComponent } from './table-resolve-conflict.component';

describe('TableResolveConflictComponent', () => {
  let component: TableResolveConflictComponent;
  let fixture: ComponentFixture<TableResolveConflictComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ TableResolveConflictComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableResolveConflictComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
