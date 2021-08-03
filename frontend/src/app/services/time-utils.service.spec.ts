import { TestBed } from '@angular/core/testing';

import { TimeUtilsService } from './time-utils.service';

describe('TimeUtilsService', () => {
  let service: TimeUtilsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimeUtilsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
