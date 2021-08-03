import { TestBed } from '@angular/core/testing';

import { FloorLayoutService } from './floor-layout.service';

describe('FloorLayoutService', () => {
  let service: FloorLayoutService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FloorLayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
