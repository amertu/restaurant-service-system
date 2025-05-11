import { Component, OnInit, Output, EventEmitter, Input, OnChanges } from '@angular/core';
import {FormControl, FormsModule} from '@angular/forms';
import { timer } from 'rxjs';
import {NgbTimepicker, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import { NgbTimeStructAdapter } from '@ng-bootstrap/ng-bootstrap/timepicker/ngb-time-adapter';

@Component({
  selector: 'app-time-selector',
  templateUrl: './time-selector.component.html',
  standalone: true,
  imports: [
    NgbTimepicker,
    FormsModule
  ],
  styleUrls: ['./time-selector.component.scss']
})
export class TimeSelectorComponent implements OnInit, OnChanges {

  @Output() submitNewTime = new EventEmitter<string>();
  @Input() initialHour: string;
  @Input() initialMinute: string;


  fullTime: string;

  time: NgbTimeStruct;
  hourStep = 1;
  minuteStep = 15;

  constructor() { }

  ngOnInit(): void {


    // A delay seems to be required, as the date  doesn't update correctly otherwise.
    timer(100).subscribe(x => {
      const initialHour = Number(this.initialHour);
      const initialMinute = Number(this.initialMinute);
      this.time = {hour: initialHour, minute: initialMinute, second: 0};
      // Make call for submitting intial values.
      this.onTimeChanged();
   });

  }

  ngOnChanges() {
    console.log('TimeSelectorComponent - ngOnChanges()');
    this.ngOnInit();
  }


  public onTimeChanged(): void {
    this.fullTime = this.time.hour.toString().padStart(2, '0') + ':' + this.time.minute.toString().padStart(2, '0');
    console.log('Time changed to: ' + this.fullTime);
    this.submitNewTime.emit(this.fullTime);
  }


}
