import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AlertService } from '../../../../services/alert.service';
import { Table } from '../../../../dtos/table';
import { TableService } from '../../../../services/table.service';
import { AuthService } from '../../../../services/auth.service';
import { TableAddComponent } from './table-add/table-add.component';
import { TableEditComponent } from './table-edit/table-edit.component';
import { TableDeleteComponent } from './table-delete/table-delete.component';
import { TableDeactivateComponent } from './table-deactivate/table-deactivate.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ReservationService } from '../../../../services/reservation.service';
import { TimeUtilsService } from '../../../../services/time-utils.service';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  @Input() tables: Table[];
  @Output() updateTables: EventEmitter<Table[]> = new EventEmitter<Table[]>();

  constructor(private tableService: TableService, private alertService: AlertService, private timeUtilsService: TimeUtilsService,
    private authService: AuthService, private modalService: NgbModal, private reservationService: ReservationService) {
  }

  ngOnInit(): void {
  }

  public loadAllTables() {
    console.log('loadAllTables()');
    this.tableService.getAllTables().subscribe(
      (tables: Table[]) => {
        this.tables = tables;
        this.updateTables.emit(this.tables);
      },
      error => {
        console.log('Failed to load tables.');
        this.alertService.error(error);
      }
    );
  }

  public setTableActive(table: Table, active: boolean) {
    this.alertService.vanishAll();
    this.tableService.setTableActive(table.id, active).subscribe(
      () => {
        this.loadAllTables();
      },
      error => {
        if (error.status === 409) this.alertService.reportError(`Could not deactivate table ${table.tableNum}: there are reservations for it in the future!`);
        else this.alertService.error(error);
      }
    );
  }

  public openEditTableForm(table: Table) {
    const modalRef = this.modalService.open(TableEditComponent);
    modalRef.componentInstance.table = table;
    modalRef.result.then(() => this.loadAllTables());
  }

  public deleteTable(table: Table) {
    let startDate = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe(
      reservations => {
        const modalRef = this.modalService.open(TableDeleteComponent);
        modalRef.componentInstance.table = table;
        modalRef.componentInstance.reservations = reservations;
        modalRef.result.then(() => this.loadAllTables());
      },
      error => {
        this.alertService.error(error);
      }
    );
  }

  private openAddTableForm() {
    const modalRef = this.modalService.open(TableAddComponent);
    modalRef.result.then(() => this.loadAllTables());
  }

  private changeTableActive(table: Table, active: boolean) {
    if(active) {
      this.tableService.setTableActive(table.id, true).subscribe(table => this.loadAllTables(), error => this.alertService.error(error));
      return;
    }

    let startDate = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe(
      reservations => {
        if (reservations.length > 0) {
          const modalRef = this.modalService.open(TableDeactivateComponent);
          modalRef.componentInstance.table = table;
          modalRef.componentInstance.reservations = reservations;
          modalRef.result.then(() => this.loadAllTables());
        } else {
          console.log(`no reservations in future for table ${table.tableNum}`);
          this.tableService.setTableActive(table.id, false).subscribe(table => this.loadAllTables());
        }
      },
      error => {
        this.alertService.error(error);
      }
    );
  }
}
