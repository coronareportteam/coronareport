import {
  CaseDto,
  CaseEntityService,
  CaseStatus,
  HealthDepartmentService,
  StartTracking,
} from '@qro/health-department/domain';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, combineLatest } from 'rxjs';
import { filter, map, switchMap, tap, shareReplay } from 'rxjs/operators';
import { SubSink } from 'subsink';
import { SnackbarService } from '@qro/shared/util-snackbar';
import { ConfirmationDialogComponent } from '@qro/shared/ui-confirmation-dialog';
import { CloseCaseDialogComponent } from '../close-case-dialog/close-case-dialog.component';
import { ApiService, HalResponse } from '@qro/shared/util-data-access';
import { CaseType } from '@qro/auth/api';
import { OccasionService } from '../occasion/occasion.service';
import { OccasionDto } from '../../../../domain/src/lib/model/occasion';

@Component({
  selector: 'qro-case-detail',
  templateUrl: './case-detail.component.html',
  styleUrls: ['./case-detail.component.scss'],
})
export class CaseDetailComponent implements OnDestroy {
  private type$$: BehaviorSubject<CaseType> = new BehaviorSubject<CaseType>(null);
  private subs = new SubSink();

  caseId: string;
  type$: Observable<CaseType> = this.type$$.asObservable();
  caseLabel$: Observable<string>;
  ClientType = CaseType;
  caseDetail$: Observable<CaseDto>;
  occasions$: Observable<OccasionDto[]>;

  constructor(
    private route: ActivatedRoute,
    private healthDepartmentService: HealthDepartmentService,
    private snackbarService: SnackbarService,
    private apiService: ApiService,
    private dialog: MatDialog,
    private entityService: CaseEntityService,
    private router: Router,
    private occasionService: OccasionService
  ) {
    this.initData();
    this.setCaseLabel();
  }

  get isNew(): boolean {
    return !this.caseId;
  }

  private setCaseLabel() {
    this.caseLabel$ = this.type$.pipe(
      map((type: CaseType) => {
        if (type === CaseType.Index) {
          return 'Index';
        }
        if (type === CaseType.Contact) {
          return 'Kontakt';
        }
      })
    );
  }

  initData(): void {
    this.caseDetail$ = combineLatest([this.route.paramMap, this.entityService.entityMap$]).pipe(
      tap(([params, entityMap]) => this.type$$.next(params.get('type') as CaseType)),
      tap(([params, entityMap]) => (this.caseId = params.get('id'))),
      map(([params, entityMap]) => {
        const id = params.get('id');
        if (id) {
          return entityMap[id];
        }
        return this.entityService.emptyCase;
      })
    );

    this.occasions$ = this.occasionService.getOccasions();
  }

  getStartTrackingTitle(caseDetail: CaseDto, buttonIsDisabled: boolean): string {
    if (caseDetail.status === CaseStatus.Abgeschlossen) {
      return 'Der Fall ist bereits abgeschlossen worden';
    }
    if (
      caseDetail.status === CaseStatus.InNachverfolgung ||
      caseDetail.status === CaseStatus.InRegistrierung ||
      caseDetail.status === CaseStatus.RegistrierungAbgeschlossen
    ) {
      return 'Nachverfolgung bereits aktiv';
    }
    if (!buttonIsDisabled) {
      return 'Sobald Sie die Person telefonisch kontaktiert haben, können Sie hier die Nachverfolgung starten';
    }
    if (caseDetail.infected) {
      return 'Um die Nachverfolgung zu starten müssen zunächst folgende Daten erfasst werden: Vorname, Nachname, Geburtsdatum, Abstrichdatum, Quarantänezeitraum, eine Telefonnummer sowie die Emailadresse';
    }
    return 'Um die Nachverfolgung zu starten müssen zunächst folgende Daten erfasst werden: Vorname, Nachname, Geburtsdatum, eine Telefonnummer sowie die Emailadresse';
  }

  getAnalogTrackingTitle(caseDetail: CaseDto, buttonIsDisabled: boolean): string {
    if (!buttonIsDisabled) {
      return 'Hiermit stoppen Sie die systemseitige Nachverfolgung des Falles und geben an, dass Sie den Fall manuell weiter verfolgen möchten';
    }
    if (caseDetail.status === CaseStatus.Abgeschlossen) {
      return 'Der Fall ist bereits abgeschlossen worden';
    }
    return '';
  }

  getAccountDeletionInfoText(caseDetail: CaseDto, buttonName: string) {
    return (
      `Mit Klick auf den Button "${buttonName}" löschen Sie den User-Account von ${caseDetail.firstName} ${caseDetail.lastName}. ` +
      `Ein Login mit den bisherigen Accountdaten wird nicht mehr möglich sein. ` +
      `Bereits hinterlegte Falldaten bleiben jedoch erhalten. ` +
      `Verwenden Sie den Button "${buttonName}", wenn ${caseDetail.firstName} ${caseDetail.lastName} ` +
      `seinen/ihren Usernamen vergessen hat und sich deshalb nicht mehr einloggen kann. ` +
      `Ein vergessenes Passwort dagegen kann ${caseDetail.firstName} ${caseDetail.lastName} selbst auf der Login-Seite zurücksetzen.`
    );
  }

  deleteUserAccount(caseDetail: CaseDto) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        abortButtonText: 'Abbrechen',
        confirmButtonText: 'Ja',
        title: `User-Account von ${caseDetail.firstName} ${caseDetail.lastName} löschen`,
        text: this.getAccountDeletionInfoText(caseDetail, 'Ja') + ' Möchten Sie fortfahren?',
      },
    });

    this.subs.add(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.subs.sink = this.apiService
            .deleteApiCall<StartTracking>(caseDetail, 'account')
            .pipe(switchMap((result) => this.entityService.getByKey(caseDetail.caseId)))
            .subscribe((caseDto) => {
              this.router.navigate([`/health-department/case-detail/${this.type$$.value}/${caseDto.caseId}/comments`]);
            });
        }
      })
    );
  }

  get returnLink() {
    return `/health-department/${this.type$$.value}-cases/case-list`;
  }

  startTracking(caseDetail: CaseDto) {
    this.subs.sink = this.apiService
      .putApiCall<StartTracking>(caseDetail, 'start-tracking')
      .pipe(switchMap((result) => this.entityService.getByKey(caseDetail.caseId).pipe(shareReplay())))
      .subscribe((caseDto) => {
        this.router.navigate([`/health-department/case-detail/${this.type$$.value}/${caseDto.caseId}/comments`]);
      });
  }

  checkForClose(halResponse: HalResponse) {
    this.subs.sink = this.dialog
      .open(CloseCaseDialogComponent, { width: '640px' })
      .afterClosed()
      .pipe(
        filter((comment) => comment),
        switchMap((comment: string) => this.healthDepartmentService.addComment(this.caseId, comment)),
        map(() => this.closeCase(halResponse))
      )
      .subscribe();
  }

  closeCase(halResponse: HalResponse) {
    this.subs.sink = this.apiService
      .deleteApiCall<any>(halResponse, 'conclude')
      .pipe(switchMap(() => this.entityService.getByKey(this.caseId)))
      .subscribe((data) => {
        this.snackbarService.success('Fall abgeschlossen.');
      });
  }

  changeToIndexType() {
    this.type$$.next(CaseType.Index);
  }

  onChangeTypeKeyPressed(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        abortButtonText: 'Abbrechen',
        confirmButtonText: 'ok',
        title: 'Zum Indexfall machen?',
        text:
          'Sind Sie sich sicher, dass ein positiver Befund vorliegt und Sie diese Kontaktperson als Indexfall weiter bearbeiten wollen?',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.changeToIndexType();
        this.router.navigate([`/health-department/case-detail/index/${this.caseId}`]);
      }
    });
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
}
