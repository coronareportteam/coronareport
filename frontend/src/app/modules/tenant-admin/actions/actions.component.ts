import {ClientService} from './../../../services/client.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SubSink} from 'subsink';
import {ActionListItemDto, Alert} from '@models/action';
import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ClientType} from '@models/report-case';
import {MatSelect} from '@angular/material/select';
import {MatOption} from '@angular/material/core';
import {AlertConfiguration, alertConfigurations} from '../action-alert/AlertConfiguration';

export class ActionRowViewModel {
  lastName: string;
  firstName: string;
  typeName: string;
  type: ClientType;
  dateOfBirth: string;
  email: string;
  phone: string;
  quarantineStart: string;
  status: string;
  alerts: string[];
  caseId: string;
}

@Component({
  selector: 'app-actions',
  templateUrl: './actions.component.html',
  styleUrls: ['./actions.component.scss']
})
export class ActionsComponent implements OnInit, OnDestroy {
  actions: ActionListItemDto[] = [];
  private subs = new SubSink();
  loading = false;
  rows: ActionRowViewModel[] = [];
  filteredRows: ActionRowViewModel[] = [];
  alertConfigs: AlertConfiguration[] = [];
  @ViewChild(MatSelect) filterSelect: MatSelect;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clientService: ClientService) { }

  ngOnInit() {
    this.loading = true;

    this.subs.add(this.route.data.subscribe(
      data => {
        this.actions = data.actions;
        this.rows = this.actions.map(action => this.getRowData(action));
        this.filteredRows = [...this.rows];
        this.loading = false;

        this.alertConfigs = this.actions.reduce((acc, next) => {
          next.alerts.forEach(alert => {
            if (!acc.includes(alert)) {
              acc.push(alert);
            }
          });

          return acc;
        }, [])
          .map(alert => {
            return alertConfigurations().find(c => c.alert === alert);
          })
          .sort((a, b) => a.order - b.order)
          ;
      },
      () => this.loading = false));
  }

  get isFiltered(): boolean {
    if (this.filterSelect) {
      return (this.filterSelect.selected as MatOption[]).length > 0;
    }
    return false;
  }

  getRowData(action: ActionListItemDto): ActionRowViewModel {
    return {
      lastName: action.lastName || '-',
      firstName: action.firstName || '-',
      typeName: this.clientService.getTypeName(action.caseType),
      type: action.caseType,
      dateOfBirth: action.dateOfBirth ? action.dateOfBirth.toCustomLocaleDateString() : '-',
      email: action.email,
      phone: action.phone || '-',
      quarantineStart: action.quarantineStart ? action.quarantineStart.toCustomLocaleDateString() : '-',
      status: action.status,
      alerts: action.alerts || [],
      caseId: action.caseId
    };
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  sendMail(event, to: string) {
    event.stopPropagation();
    window.location.href = `mailto:${to}`;
  }

  onSelect(event) {
    this.router.navigate(
      ['/tenant-admin/client', event?.selected[0]?.type, event?.selected[0]?.caseId],
      {
        queryParams: { tab: 1 }
      });
  }

  onAlertFilterChanged(selectedValues: string[]) {
    if (selectedValues.length === 0) { this.filteredRows = this.rows; return; }
    this.filteredRows = this.rows.filter(r => r.alerts.filter(a => selectedValues.includes(a)).length > 0);
  }

  resetFilter() {
    this.filterSelect.value = null;
    this.onAlertFilterChanged([]);
  }

  alertConfigurationFor(alert: Alert) {
    return alertConfigurations().find(c => c.alert === alert);
  }
}
