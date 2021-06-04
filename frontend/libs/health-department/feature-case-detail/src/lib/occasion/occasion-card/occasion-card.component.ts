import { Component, EventEmitter, Input, Output } from '@angular/core';
import { OccasionDetailDialogComponent } from '../occasion-detail-dialog/occasion-detail-dialog.component';
import { filter, take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { OccasionDto } from '../../../../../domain/src/lib/model/occasion';
import { ConfirmationDialogComponent } from '@qro/shared/ui-confirmation-dialog';
import { ConfirmDialogData } from '@qro/client/ui-contact-person-detail';

@Component({
  selector: 'qro-occasion-card',
  templateUrl: './occasion-card.component.html',
  styleUrls: ['./occasion-card.component.scss'],
})
export class OccasionCardComponent {
  @Input()
  occasion: OccasionDto;

  @Output()
  saveOccasionEvent = new EventEmitter<OccasionDto>();

  @Output()
  deleteOccasionEvent = new EventEmitter<OccasionDto>();

  constructor(private dialog: MatDialog) {}

  deleteOccasion() {
    this.openConfirmDialog()
      .afterClosed()
      .pipe(filter((response) => !!response))
      .subscribe((_) => this.deleteOccasionEvent.emit(this.occasion));
  }

  editOccasion(occasion: OccasionDto) {
    const dialogRef = this.dialog.open(OccasionDetailDialogComponent);
    dialogRef.componentInstance.occasion(occasion);

    dialogRef
      .afterClosed()
      .pipe(
        take(1),
        filter((occasionData) => occasionData)
      )
      .subscribe((occasionData) => {
        this.saveOccasionEvent.emit(occasionData);
      });
  }

  private openConfirmDialog() {
    const dialogData: ConfirmDialogData = {
      text: 'Möchten Sie dieses Ereignis löschen?',
      title: 'Ereignis löschen',
      abortButtonText: 'Abbrechen',
      confirmButtonText: 'Löschen',
    };

    return this.dialog.open(ConfirmationDialogComponent, { data: dialogData });
  }
}
