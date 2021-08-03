import {
  Component,
  OnInit,
  OnChanges,
  SimpleChanges,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { timer } from 'rxjs/internal/observable/timer';

import {NgbDateStruct, NgbDate, NgbCalendar} from '@ng-bootstrap/ng-bootstrap';
import { getLocaleMonthNames } from '@angular/common';

@Component({
  selector: 'app-date-selector',
  templateUrl: './date-selector.component.html',
  styleUrls: ['./date-selector.component.scss']
})
export class DateSelectorComponent implements OnInit {
  @Output() submitNewDate = new EventEmitter<string>();

  @Input() initialDateAsString: string;

  today: NgbDate;

  initialDate: NgbDate;

  model: NgbDateStruct;

  constructor(private calendar: NgbCalendar) {}

  ngOnInit(): void {

    // A delay seems to be required, as the date  doesn't update correctly otherwise.
    timer(100).subscribe(x => {
      this.today = this.calendar.getToday();
      this.selectInitialDate(this.initialDateAsString);
    });
  }

  private selectInitialDate(initialDateAsString: string): void {
    // for the used time format see: https://de.wikipedia.org/wiki/ISO_8601
    // JJJJ-MM-TT
    const year = Number(initialDateAsString.slice(0, 4));
    const month = Number(initialDateAsString.slice(5, 7));
    const day = Number(initialDateAsString.slice(8, 10));
    this.initialDate = new NgbDate(year, month, day);
    this.model = this.initialDate;
    this.onDateChanged(this.initialDate);
  }


  public onDateChanged(selectedDate: NgbDate): void {
    const fullDateAsString =
     selectedDate.year +
      '-' +
      selectedDate.month.toString().padStart(2, '0') +
      '-' +
      selectedDate.day.toString().padStart(2, '0');
    console.log('Date changed to: ' + fullDateAsString);
    this.submitNewDate.emit(fullDateAsString);
  }

}
