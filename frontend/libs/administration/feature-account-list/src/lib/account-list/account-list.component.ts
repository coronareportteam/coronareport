import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { SubSink } from 'subsink';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { finalize, map, shareReplay, tap } from 'rxjs/operators';
import { AccountDto, AccountEntityService } from '@qro/administration/domain';
import { IRole, roles } from '@qro/auth/api';
import { ApiService } from '@qro/shared/util-data-access';
import { SnackbarService } from '@qro/shared/util-snackbar';
import { ConfirmationDialogComponent } from '@qro/shared/ui-confirmation-dialog';
import { ArrayFunctions } from '@qro/shared/util-common-functions';

@Component({
  selector: 'qro-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss'],
})
export class AccountListComponent implements OnInit, OnDestroy {
  private subs = new SubSink();
  accounts$: Observable<AccountDto[]>;
  loading = false;
  roles: IRole[] = roles;

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private entityService: AccountEntityService,
    private snackbarService: SnackbarService,
    private apiService: ApiService
  ) {}

  ngOnInit() {
    this.loading = true;
    this.accounts$ = this.entityService.entities$.pipe(tap(() => (this.loading = false)));
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  onSelect(event) {
    this.router.navigate(['/administration/accounts/account-detail', event?.selected[0]?.accountId]);
  }

  getRoleDisplayName(role: string) {
    return this.roles.find((r) => r.name === role).displayName;
  }

  deleteUser(event, account: AccountDto) {
    event.stopPropagation();
    this.confirmDeletion(account).subscribe((result) => {
      if (result) {
        this.apiService
          .delete(account._links)
          .pipe(tap((_) => this.entityService.removeOneFromCache(account)))
          .subscribe((_) => {
            this.snackbarService.success(`${account.firstName} ${account.lastName} wurde erfolgreich gelöscht.`);
          });
      }
    });
  }

  confirmDeletion(user: AccountDto): Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: 'Löschen?',
        text: `Sind Sie sicher, dass Sie ${user.firstName} ${user.lastName} löschen wollen?`,
      },
    });

    return dialogRef.afterClosed().pipe(
      map((result) => {
        return !!result;
      })
    );
  }
}
