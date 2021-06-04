import { TestBed } from '@angular/core/testing';

import { OccasionService } from './occasion.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthStore } from '@qro/auth/domain';
import { API_URL } from '@qro/shared/util-data-access';

describe('OccasionService', () => {
  let service: OccasionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: API_URL, useValue: '' },
        { provide: AuthStore, useValue: {} },
      ],
    });
    service = TestBed.inject(OccasionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
