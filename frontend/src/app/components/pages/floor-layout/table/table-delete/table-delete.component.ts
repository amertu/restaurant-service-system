import { Component, Input, OnInit } from '@angular/core';
import { Table } from '../../../../../dtos/table';
import { Reservation } from '../../../../../dtos/reservation';
import { TableService } from '../../../../../services/table.service';
import { AlertService } from '../../../../../services/alert.service';
import { FloorLayout } from '../../../../../dtos/floor-layout';
import { FloorLayoutService } from '../../../../../services/floor-layout.service';
import { ReservationService } from '../../../../../services/reservation.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-table-delete',
  templateUrl: './table-delete.component.html',
  styleUrls: ['./table-delete.component.scss']
})
export class TableDeleteComponent implements OnInit {
  @Input() table: Table;

  constructor(public tableService: TableService, public alertService: AlertService, private layoutService: FloorLayoutService, 
    private reservationService: ReservationService, public activeModal: NgbActiveModal) { }

  reservations: Reservation[];

  ngOnInit() {
  }

  public deleteTable(id: number) {
    this.tableService.deleteTable(id).subscribe(
      () => {
        this.activeModal.close();
      },
      error => {
        this.alertService.error(error);
      }
    );
  };

  public formatDate(date: string): string {
    return date.replace('T', ' ').substring(0, date.length - 3);
  }
}
