import { Component, OnInit } from '@angular/core';
import { ActionListItemDto, AlertConfiguration, getAlertConfigurations, Alert } from '@qro/health-department/domain';
import { ActivatedRoute, Router } from '@angular/router';
import { DateFunctions } from '@qro/shared/util-date';
import { CaseType } from '@qro/auth/api';
import { Observable } from 'rxjs';
import { CheckboxFilterComponent, DE_LOCALE, EmailButtonComponent, DATE_FILTER_PARAMS } from '@qro/shared/ui-ag-grid';
import { ColDef, ColumnApi, GridApi } from 'ag-grid-community';
import { map } from 'rxjs/operators';
import { ActionAlertComponent, ActionAlertFilterComponent } from '@qro/health-department/ui-action-alert';

class ActionRowViewModel {
  lastName: string;
  firstName: string;
  type: CaseType;
  dateOfBirth: string;
  email: string;
  phone: string;
  quarantineStart: string;
  status: string;
  alerts: string[];
  caseId: string;
  rowHeight: number;
}

@Component({
  selector: 'qro-index-case-action-list',
  templateUrl: './action-list.component.html',
  styleUrls: ['./action-list.component.scss'],
})
export class ActionListComponent implements OnInit {
  actions$: Observable<ActionListItemDto[]>;
  rows$: Observable<ActionRowViewModel[]>;
  alertConfigs$: Observable<AlertConfiguration[]>;
  defaultColDef: ColDef = {
    editable: false,
    filter: 'agTextColumnFilter',
    sortable: false,
  };
  columnDefs: ColDef[] = [];
  locale = DE_LOCALE;
  frameworkComponents;

  constructor(private route: ActivatedRoute, private router: Router) {
    this.frameworkComponents = {
      checkboxFilter: CheckboxFilterComponent,
      actionAlertComponent: ActionAlertComponent,
      actionAlertFilter: ActionAlertFilterComponent,
    };
    this.columnDefs = [
      {
        headerName: 'Auffälligkeiten',
        field: 'alerts',
        filter: 'actionAlertFilter',
        cellRenderer: 'actionAlertComponent',
        width: 300,
      },
      { headerName: 'Nachname', field: 'lastName' },
      { headerName: 'Vorname', field: 'firstName' },
      {
        headerName: 'Geburtsdatum',
        field: 'dateOfBirth',
        filter: 'agDateColumnFilter',
        filterParams: DATE_FILTER_PARAMS,
      },
      { headerName: 'Status', field: 'status', filter: 'checkboxFilter' },
      {
        headerName: 'Quarantäne seit',
        field: 'quarantineStart',
        filter: 'agDateColumnFilter',
        filterParams: DATE_FILTER_PARAMS,
      },
      {
        headerName: 'Telefon',
        field: 'phone',
      },
      {
        headerName: 'E-Mail',
        field: 'email',
        cellRendererFramework: EmailButtonComponent,
        filter: false,
        sortable: false,
        width: 100,
      },
    ];
  }

  dateFormatter(value: Date): string {
    return value ? DateFunctions.toCustomLocaleDateString(value) : '-';
  }

  ngOnInit() {
    this.actions$ = this.route.data.pipe(map((data) => data.actions));

    this.rows$ = this.actions$.pipe(map((actions) => actions.map((action) => this.getRowData(action))));

    this.alertConfigs$ = this.actions$.pipe(
      map((actions) =>
        actions
          .reduce((acc, next) => {
            next.alerts.forEach((alert) => {
              if (!acc.includes(alert)) {
                acc.push(alert);
              }
            });

            return acc;
          }, [])
          .map((alert) => {
            return getAlertConfigurations().find((c) => c.alert === alert);
          })
          .sort((a, b) => a.order - b.order)
      )
    );
  }

  getRowData(action: ActionListItemDto): ActionRowViewModel {
    return {
      lastName: action.lastName || '-',
      firstName: action.firstName || '-',
      type: action.caseType,
      dateOfBirth: this.dateFormatter(action.dateOfBirth),
      email: action.email,
      phone: action.phone || '-',
      quarantineStart: this.dateFormatter(action.quarantineStart),
      status: action.status,
      alerts: action.alerts || [],
      caseId: action.caseId,
      rowHeight: Math.min(50 + action.alerts.length * 9),
    };
  }

  onGridReady(event: { api: GridApi; columnApi: ColumnApi }) {
    event.api.setFilterModel({
      status: [
        {
          selected: false,
          label: 'abgeschlossen',
        },
      ],
    });
    event.api.onFilterChanged();
  }

  onSelect(event) {
    this.router.navigate(['/health-department/case-detail', event.node.data.type, event.node.data.caseId, 'actions']);
  }

  getRowHeight(params) {
    return params.data.rowHeight;
  }
}
