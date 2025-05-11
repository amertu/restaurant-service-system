import { Component, OnInit, Input } from '@angular/core';
import { Reservation } from 'src/app/dtos/reservation';
import {ReservationFormComponent, ReservationFormType} from '../reservation-form/reservation-form.component';

@Component({
  selector: 'app-reservation-details',
  templateUrl: './reservation-details.component.html',
  standalone: true,
  imports: [
    ReservationFormComponent,
  ],
  styleUrls: ['./reservation-details.component.scss']
})
export class ReservationDetailsComponent implements OnInit {
  @Input() reservation: Reservation;
  readonly formTypeDetails = ReservationFormType.details;


  constructor() { }

  ngOnInit(): void {
  }

  public doNothing(reservationToCreate: Reservation) {
    // The details-page must not allow any changes.
  }

}
