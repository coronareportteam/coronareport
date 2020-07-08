import { FormControl } from '@angular/forms';
import { Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { filter, startWith, takeUntil, distinctUntilChanged, debounceTime, tap } from 'rxjs/operators';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { COMMA, ENTER } from '@angular/cdk/keycodes';

@Component({
  selector: 'qro-lazy-autocomplete',
  templateUrl: './lazy-autocomplete.component.html',
  styleUrls: ['./lazy-autocomplete.component.scss'],
})
export class LazyAutocompleteComponent implements OnInit, OnDestroy {
  @Input() control: FormControl;
  @Input() placeholder: string;
  private _selectableItems: any[] = [];
  @Input() get selectableItems(): any[] {
    return this._selectableItems;
  }

  set selectableItems(val: any[]) {
    this._selectableItems = val;
    if (typeof this.searchTerm === 'string') {
      this._filter(this.searchTerm);
    }
  }
  @Input() displayWith: ((value: any) => string) | null;
  @Input() keyProperty: string;
  @Output() removed = new EventEmitter<any>();
  @Output() added = new EventEmitter<any>();
  @Output() itemNotFound = new EventEmitter<string>();
  @Output() completeMethod: EventEmitter<string> = new EventEmitter();
  selectedItems: any[] = [];
  inputControl = new FormControl();
  separatorKeysCodes: number[] = [ENTER, COMMA];
  @ViewChild('input') input: ElementRef<HTMLInputElement>;
  @ViewChild('auto') autocomplete;
  destroy$: Subject<void> = new Subject<void>();
  filteredList$$: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  private searchTerm = '';

  ngOnInit() {
    this.filteredList$$.next(this.selectableItems);
    this.control.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        filter((data) => !!data)
      )
      .subscribe((data: any[]) => {
        this.selectableItems = data;
        this.control.markAsDirty();
        data.forEach((value) => this.added.emit(value));
      });

    this.selectableItems = this.control.value;

    this.inputControl.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(500),
        distinctUntilChanged(),
        startWith(null as string),
        tap((searchTerm) => (this.searchTerm = searchTerm))
      )
      .subscribe((searchTerm) => {
        if (typeof searchTerm === 'string') {
          this.completeMethod.emit(searchTerm);
          this._filter(searchTerm);
        }
      });
  }

  clearInput(): void {
    // fixme: best to do this only via formControl ! ..... :(
    this.inputControl.patchValue(null); // patchInputControl to null for later comparisons
    this.input.nativeElement.value = null; // set input value of native element to null, otherwise its shown in the GUI
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get prefilteredList(): any[] {
    return this.selectableItems.reduce((total, item) => {
      if (!this.selectedItems.find((i) => i[this.keyProperty] === item[this.keyProperty])) {
        total.push(item);
      }
      return total;
    }, []);
  }

  checkInputForData() {
    const input = this.inputControl.value;
    const inList = this.isInputInSelectedList(input);
    if (!inList && input) {
      this.itemNotFound.emit(input);
    }
  }

  private isInputInSelectedList(searchString: string): any {
    return this.selectableItems.find((item) => this.displayWith(item) === searchString);
  }

  private _filter(searchTerm: string) {
    let arrayToReturn = this.prefilteredList.filter(
      (item) => !this.selectedItems.find((i) => i[this.keyProperty] === item[this.keyProperty])
    );

    if (!searchTerm) {
      this.filteredList$$.next(this.prefilteredList);
    }
    const filterValue = searchTerm.toLowerCase();
    arrayToReturn = arrayToReturn.filter((item) => this.displayWith(item).toLowerCase().indexOf(filterValue) === 0);
    this.filteredList$$.next(arrayToReturn);
  }

  get disabled(): boolean {
    return this.control.disabled;
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const selectedValue = event.option.value;
    this.selectedItems.push(selectedValue);
    this.input.nativeElement.value = '';
    this.inputControl.setValue(null);
    this.setFormControlValue();
    this.added.emit(selectedValue);
    this.filteredList$$.next(this.prefilteredList);
  }

  remove(item: any): void {
    const index = this.selectedItems.indexOf(item);

    if (index >= 0) {
      this.selectedItems.splice(index, 1);
      this.setFormControlValue();
      this.removed.emit(item);
    }
    this.filteredList$$.next(this.prefilteredList);
  }

  setFormControlValue() {
    this.control.setValue(this.selectedItems);
    this.control.markAsDirty();
  }

  getNameById(id: string) {
    const item = this.selectableItems.find((i) => i[this.keyProperty] === id);
    if (item) {
      return this.displayWith(item);
    }
    return '';
  }
}
