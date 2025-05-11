import { Component, OnInit, Output, Input, EventEmitter, OnChanges } from '@angular/core';
import { timer } from 'rxjs';
import {NgbRating} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-duration-selector',
  templateUrl: './duration-selector.component.html',
  standalone: true,
  imports: [
    NgbRating
  ],
  styleUrls: ['./duration-selector.component.scss']
})
export class DurationSelectorComponent implements OnInit, OnChanges {

  @Output() submitNewTime = new EventEmitter<string>();
  @Input() initialHour: string;
  @Input() initialMinute: string;
  @Input() initialEndHour: string;

  selectedDurationInHours = 2;

  fullTime: string;

  constructor() { }

  ngOnInit(): void {
    // A delay seems to be required, as the date  doesn't update correctly otherwise.

    console.log('Duration-Selector: ngOnInit().');

    if (this.initialEndHour) {
      this.selectedDurationInHours = Number(this.initialEndHour) - Number(this.initialHour);
    } else {
      this.selectedDurationInHours = 2;
      timer(100).subscribe(x => {

        // Make call for submitting intial values.
        this.onTimeChanged();
     });
    }



  }


  ngOnChanges(): void {
    this.onTimeChanged();
  }

  public onTimeChanged(): void {

    const resultingHour = Number(this.initialHour) + Number(this.selectedDurationInHours);
    if ( resultingHour > 23) {
      this.fullTime = '23:59';
    } else {
      this.fullTime = resultingHour.toString().padStart(2, '0') + ':' + this.initialMinute.toString().padStart(2, '0');
    }
    console.log('Time changed to: ' + this.fullTime);
    this.submitNewTime.emit(this.fullTime);
  }

}
