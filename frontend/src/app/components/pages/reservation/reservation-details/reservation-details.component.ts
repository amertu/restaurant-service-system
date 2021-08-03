import { Component, OnInit, Input } from '@angular/core';
import { Reservation } from 'src/app/dtos/reservation';

@Component({
  selector: 'app-reservation-details',
  templateUrl: './reservation-details.component.html',
  styleUrls: ['./reservation-details.component.scss']
})
export class ReservationDetailsComponent implements OnInit {
  @Input() reservation: Reservation;

  constructor() { }

  ngOnInit(): void {
  }

  public doNothing(reservationToCreate: Reservation) {
    // The details-page must not allow any changes.
  }

}
