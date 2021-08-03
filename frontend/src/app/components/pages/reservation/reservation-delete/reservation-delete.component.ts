import {Component, Input, OnInit} from '@angular/core';
import {Reservation} from '../../../../dtos/reservation';
import {ReservationService} from '../../../../services/reservation.service';
import {AlertService} from '../../../../services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-reservation-delete',
  templateUrl: './reservation-delete.component.html',
  styleUrls: ['./reservation-delete.component.scss']
})
export class ReservationDeleteComponent implements OnInit {
  @Input() reservation: Reservation;

  constructor(public activeModal: NgbActiveModal, public reservationService: ReservationService, public alertService: AlertService) { }

  ngOnInit() {
  }

  public deleteReservation() {
    this.reservationService.deleteReservation(this.reservation).subscribe(
      () => {
        this.alertService.reportSuccessModal('Successfully deleted reservation.');
        this.activeModal.close(this.reservation);
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }
}
