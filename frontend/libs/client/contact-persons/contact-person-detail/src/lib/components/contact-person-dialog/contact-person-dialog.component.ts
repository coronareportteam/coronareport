import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ContactPersonDto } from '@qro/client/contact-persons/domain';

@Component({
  selector: 'qro-contact-person-dialog',
  templateUrl: './contact-person-dialog.component.html',
  styleUrls: ['./contact-person-dialog.component.scss'],
})
export class ContactPersonDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      contactPerson: ContactPersonDto;
    },
    private matDialogRef: MatDialogRef<ContactPersonDialogComponent>
  ) {}

  public close() {
    this.matDialogRef.close();
  }

  onContactCreated(createdContact: ContactPersonDto) {
    this.matDialogRef.close(createdContact);
  }
}
