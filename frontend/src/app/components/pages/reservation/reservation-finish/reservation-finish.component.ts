import { Component, OnInit, Input } from '@angular/core';
import { Reservation } from 'src/app/dtos/reservation';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ReservationService } from 'src/app/services/reservation.service';
import { AlertService } from 'src/app/services/alert.service';
import { TimeUtilsService } from 'src/app/services/time-utils.service';


@Component({
  selector: 'app-reservation-finish',
  templateUrl: './reservation-finish.component.html',
  standalone: true,
  styleUrls: ['./reservation-finish.component.css']
})
export class ReservationFinishComponent implements OnInit {
  @Input() reservation: Reservation;

  constructor(public activeModal: NgbActiveModal, public timeUtilsService: TimeUtilsService,
    public reservationService: ReservationService, public alertService: AlertService){ }

  ngOnInit(): void {
  }

  public finishReservation() {
    this.reservation.endDateTime = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.updateReservation(this.reservation).subscribe(
      () => {
        this.alertService.reportSuccessModal('Successfully finished the reservation.');
        this.activeModal.close();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }

}
