import {Component, OnInit} from '@angular/core';
import {Reservation} from 'src/app/dtos/reservation';
import {ReservationService} from 'src/app/services/reservation.service';
import {TableService} from 'src/app/services/table.service';
import {AlertService} from 'src/app/services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { formatDate } from '@angular/common';
import { TimeUtilsService } from 'src/app/services/time-utils.service';
import {ReservationFormComponent, ReservationFormType} from '../reservation-form/reservation-form.component';


@Component({
  selector: 'app-reservation-add',
  templateUrl: './reservation-add.component.html',
  standalone: true,
  imports: [
    ReservationFormComponent
  ],
  styleUrls: ['./reservation-add.component.scss']
})
export class ReservationAddComponent implements OnInit {
  initialReservation: Reservation;
  readonly formTypeAdd = ReservationFormType.add;

  constructor(
              public activeModal: NgbActiveModal,
              private reservationService: ReservationService,
              private tableService: TableService,
              private alertService: AlertService,
              private router: Router,
              private timeUtilsService: TimeUtilsService
              ) {
  }

  ngOnInit(): void {
    const today = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    console.log('todayWithFormatDate:' + today );

    const locationOfDateSeparator = today.indexOf('T');
    const locationOfHourToMinuteSeparator = today.indexOf(':');

    const initialDateAsString = today.substring(0, locationOfDateSeparator);
    const currentHour = Number(today.substring(locationOfDateSeparator + 1, locationOfHourToMinuteSeparator));
    const currentMinute = Number(today.substring(locationOfHourToMinuteSeparator + 1));
    const minutesRoundedUpToNextQuarterOfHour = this.timeUtilsService.getMinutesRoundedUpToNextQuarterOfHour(currentHour, currentMinute);

    const defaultDurationInHours = 2;
    const startHour = this.timeUtilsService.getStartHour(currentHour, minutesRoundedUpToNextQuarterOfHour);
    const startTime = this.timeUtilsService.getStartTime(startHour, minutesRoundedUpToNextQuarterOfHour);
    const endTime = this.timeUtilsService.getEndTime(startHour, minutesRoundedUpToNextQuarterOfHour, defaultDurationInHours);

    this.initialReservation = new Reservation(null,
      '',
      null,
      '',
      '',
      this.timeUtilsService.getIsoDateTimeAsString(initialDateAsString, startTime),
      this.timeUtilsService.getIsoDateTimeAsString(initialDateAsString, endTime),
      []);
  }

  public createReservation(reservationToCreate: Reservation) {
    this.reservationService.createReservation(reservationToCreate).subscribe(
      () => {
        this.alertService.reportSuccessModal('Successfully created reservation.');
        this.activeModal.close();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }


}
