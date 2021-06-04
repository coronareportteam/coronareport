import { Component, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SubSink } from 'subsink';
import { filter, take, tap } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';
import { OccasionDetailDialogComponent } from '../occasion-detail-dialog/occasion-detail-dialog.component';
import { BehaviorSubject } from 'rxjs';
import { OccasionDto } from '../../../../../domain/src/lib/model/occasion';
import { OccasionService } from '../occasion.service';
import { SnackbarService } from '@qro/shared/util-snackbar';

@Component({
  selector: 'qro-occasion-list',
  templateUrl: './occasion-list.component.html',
  styleUrls: ['./occasion-list.component.scss'],
})
export class OccasionListComponent implements OnDestroy {
  subs = new SubSink();
  caseId = null;

  $$occasions: BehaviorSubject<OccasionDto[]> = new BehaviorSubject<OccasionDto[]>(null);

  constructor(
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private occasionService: OccasionService,
    private snackbarService: SnackbarService
  ) {
    this.route.parent.paramMap
      .pipe(
        take(1),
        tap((params) => (this.caseId = params.get('id')))
      )
      .subscribe();
    this.loadOccasions();
  }

  get occasions() {
    return this.$$occasions.asObservable();
  }

  openOccasionDetailDialog() {
    this.subs.add(
      this.dialog
        .open(OccasionDetailDialogComponent)
        .afterClosed()
        .pipe(filter((occasionData) => occasionData))
        .subscribe((occasionData) => this.createNewOccasion(occasionData))
    );
  }

  editOccasion(occasion: OccasionDto) {
    this.occasionService
      .editOccasion(occasion.occasionCode, occasion)
      .pipe(take(1))
      .subscribe((_) => {
        this.snackbarService.success('Ereignis erfolgreich bearbeitet');
        this.loadOccasions();
      });
  }

  deleteOccasion(occasion: OccasionDto) {
    this.occasionService
      .deleteOccasion(occasion)
      .pipe(take(1))
      .subscribe((_) => {
        this.snackbarService.success('Ereignis erfolgreich gelöscht');
        this.loadOccasions();
      });
  }

  private loadOccasions() {
    this.occasionService
      .getOccasions()
      .pipe(take(1))
      .subscribe((occasions) => this.$$occasions.next(occasions));
  }

  private createNewOccasion(newOccasion) {
    this.occasionService
      .saveOccasion(this.caseId, newOccasion)
      .pipe(take(1))
      .subscribe((_) => {
        this.snackbarService.success('Ereignis erfolgreich erstellt');
        this.loadOccasions();
      });
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
