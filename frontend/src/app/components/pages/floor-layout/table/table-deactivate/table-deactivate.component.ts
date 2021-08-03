import { Component, Input, OnInit } from '@angular/core';
import { Table } from '../../../../../dtos/table';
import { Reservation } from '../../../../../dtos/reservation';
import { TableService } from '../../../../../services/table.service';
import { AlertService } from '../../../../../services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-table-deactivate',
  templateUrl: './table-deactivate.component.html',
  styleUrls: ['./table-deactivate.component.scss']
})
export class TableDeactivateComponent implements OnInit {
  @Input() table: Table;
  @Input() reservations: Reservation[];

  constructor(public tableService: TableService, public alertService: AlertService, private activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  public deactivateTable(id: number) {
    this.tableService.setTableActive(id, false).subscribe(
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
