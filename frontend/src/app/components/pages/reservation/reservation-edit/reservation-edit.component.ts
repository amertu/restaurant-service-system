import {Component, Input, OnInit} from '@angular/core';
import {Reservation} from 'src/app/dtos/reservation';
import {ReservationService} from 'src/app/services/reservation.service';
import {TableService} from 'src/app/services/table.service';
import {AlertService} from 'src/app/services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-reservation-edit',
  templateUrl: './reservation-edit.component.html',
  styleUrls: ['./reservation-edit.component.scss']
})
export class ReservationEditComponent implements OnInit {
  @Input() reservation: Reservation;

  constructor(
              public activeModal: NgbActiveModal,
              private reservationService: ReservationService,
              private tableService: TableService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
  }

  public updateReservation(reservationToCreate: Reservation) {
    this.reservationService.updateReservation(reservationToCreate).subscribe(
      () => {
        this.alertService.reportSuccessModal('Successfully updated reservation.');
        this.activeModal.close();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }
}
