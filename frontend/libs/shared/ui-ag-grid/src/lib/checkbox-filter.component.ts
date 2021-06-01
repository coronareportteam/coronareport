import { Component } from '@angular/core';
import { IFilterAngularComp } from 'ag-grid-angular';
import { IDoesFilterPassParams, IFilterParams, RowNode } from 'ag-grid-community';
import { uniq } from 'lodash';

@Component({
  selector: 'qro-checkbox-filter',
  template: `
    <section class="example-section">
      <span class="example-list-section">
        <ul>
          <li *ngFor="let option of options">
            <mat-checkbox data-cy="filter-checkbox" [(ngModel)]="option.selected" (ngModelChange)="onChange()">
              {{ option.label }}
            </mat-checkbox>
          </li>
        </ul>
      </span>
      <button data-cy="select-all-button" mat-stroked-button (click)="selectAll()" *ngIf="hasUnselectedOptions()">
        Alle
      </button>
      <button data-cy="unselect-all-button" mat-stroked-button (click)="deselectAll()" *ngIf="areAllOptionsSelected()">
        Alle abwählen
      </button>
    </section>
  `,
  styles: [
    `
      .example-section {
        margin: 12px 0;
      }

      .example-margin {
        margin: 0 12px;
      }

      ul {
        list-style-type: none;
        margin: 0;
        padding: 0 15px;
      }

      button {
        margin-left: 15px;
        margin-top: 10px;
      }
    `,
  ],
})
export class CheckboxFilterComponent implements IFilterAngularComp {
  private params: IFilterParams;
  private valueGetter: (rowNode: RowNode) => any;
  public options: { label: string; selected: boolean }[] = [];

  agInit(params: IFilterParams): void {
    this.params = params;
    this.valueGetter = params.valueGetter;
    this.initOptions();
  }

  private initOptions() {
    const field = this.params.colDef.field;
    // @ts-ignore
    this.options = uniq(this.params.rowModel.rowsToDisplay.map((d) => d.data[field]))
      .sort(function (a: string, b: string) {
        return a.toLowerCase().localeCompare(b.toLowerCase());
      })
      .map((o) => ({
        label: o,
        selected: true,
      }));
  }

  isFilterActive(): boolean {
    if (!this.options) {
      return false;
    }
    return this.hasUnselectedOptions();
  }

  hasUnselectedOptions(): boolean {
    return this.options.map((o) => o.selected).includes(false);
  }

  areAllOptionsSelected(): boolean {
    return this.options.every((o) => !!o.selected);
  }

  doesFilterPass(params: IDoesFilterPassParams): boolean {
    return this.options
      .filter((o) => o.selected)
      .map((o) => o.label.toLowerCase())
      .includes(this.valueGetter(params.node).toString().toLowerCase());
  }

  getModel(): any {
    return this.options;
  }

  setModel(model: any): void {
    if (model) {
      model.forEach((val) => {
        const index = this.options.map((o) => o.label).indexOf(val.label);
        if (index > -1) {
          this.options[index] = val;
        }
      });
    } else {
      this.resetOptions();
    }
  }

  private resetOptions(): void {
    if (!this.options) {
      this.initOptions();
    }
    this.options.forEach((o) => (o.selected = true));
  }

  selectAll() {
    this.options.forEach((o) => (o.selected = true));
    this.onChange();
  }

  deselectAll() {
    this.options.forEach((o) => (o.selected = false));
    this.onChange();
  }

  onChange(): void {
    this.params.filterChangedCallback();
  }
}
