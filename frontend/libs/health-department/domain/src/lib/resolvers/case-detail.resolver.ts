import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { CaseDto } from '../model/case';
import { CaseEntityService } from '../data-access/case-entity.service';

@Injectable()
export class CaseDetailResolver implements Resolve<CaseDto> {
  constructor(private entityService: CaseEntityService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<CaseDto> {
    const id = route.paramMap.get('id');
    if (id) {
      return this.entityService.getByKey(id);
    }
    return of(this.entityService.emptyCase);
  }
}
