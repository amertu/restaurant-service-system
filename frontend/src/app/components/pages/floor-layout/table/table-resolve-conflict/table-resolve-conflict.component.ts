import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { Router } from '@angular/router';
import { Table } from 'src/app/dtos/table';
import { Reservation } from 'src/app/dtos/reservation';
import { TableService } from '../../../../../services/table.service';
import { ReservationService } from '../../../../../services/reservation.service';
import {AlertService} from '../../../../../services/alert.service';
import { FloorLayout } from '../../../../../dtos/floor-layout';
import { FloorLayoutService } from '../../../../../services/floor-layout.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ReservationEditComponent } from '../../../reservation/reservation-edit/reservation-edit.component';
import { ReservationDeleteComponent } from '../../../reservation/reservation-delete/reservation-delete.component';

enum reservationFormType {
  add = 'Add',
  edit = 'Edit'
}

@Component({
  selector: 'app-table-resolve-conflict',
  templateUrl: './table-resolve-conflict.component.html',
  styleUrls: ['./table-resolve-conflict.component.scss']
})
export class TableResolveConflictComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router:Router, private modalService: NgbModal, private tableService: TableService,
    private reservationService: ReservationService, private alertService: AlertService, private layoutService: FloorLayoutService) { }

  table: Table;
  reservations: Reservation[];
  delete: boolean;//if true -> table can be deleted after resolving conflicts, else deactivated

  selectedReservation: Reservation;
  dataLoaded: boolean;
  desiredActionComplete: boolean = false;

  ngOnInit(): void {
    const tableId = Number(this.route.snapshot.paramMap.get("id"));
    this.delete = (this.router.url.includes('delete'));
    this.loadData(tableId);
  }

  loadData(tableId: number) {
    this.tableService.getTableWithId(tableId).subscribe(
      table => {
        this.table = table;
        let startDate = new Date().toISOString();
        let endDate = new Date('2099-12-31T23:59:00').toISOString();
        this.reservationService.filterReservations(null, startDate, endDate, this.table.tableNum.toString()).subscribe(
          reservations => {
            this.reservations = reservations;
            console.log(`found ${reservations.length} reservations for table ${table.tableNum}`);
            this.dataLoaded = true;
          },
          error => {
            this.alertService.reportError(error.message)
          }
        )
      },
      error => {
        if(error.status == 404) this.router.navigate(['/table']);
        else this.alertService.reportError(error.message)
      }
    )
  }

  public formatDate(date: string): string {
    return date.replace('T', ' ').substring(0, date.length - 3);
  }

  completeDesiredAction() {
    if (this.delete) {//delete
      this.tableService.deleteTable(this.table.id).subscribe(
        () => {
          this.deleteFromLayout(this.table.id);
          this.desiredActionComplete = true;
        },
        error => {
          this.alertService.error(error);
        }
      );
    } else {//deactivate
      this.tableService.setTableActive(this.table.id, false).subscribe(
        () => {
          this.desiredActionComplete = true;
        },
        error => {
          this.alertService.error(error);
        }
      );
    }
  }

  private deleteFromLayout(id: number): void {
    let currLayout;
    this.layoutService.getLayoutWithId(1).subscribe(
      data => {
        currLayout = JSON.parse(data.serializedLayout);
        console.log("Layout successfully loaded!");
        let layoutObjects = currLayout.objects;
        let index;
        for (let i = 0; i < layoutObjects.length; i++) {
          if (layoutObjects[i].id === id) {
            index = i;
          }
        }
        if (index > -1) {
          currLayout.objects.splice(index, 1);
          this.updateLayout(JSON.stringify(currLayout));
        }
      },
      error => {
        console.log("Layout could not be loaded!");
      }
    );
  }

  private updateLayout(newLayout: string): void {
    this.layoutService.updateLayout(new FloorLayout(1, newLayout)).subscribe(
      () => {
        console.log("Layout has been updated!");
      },
      error => {
        console.log("Layout could not be updated!");
      }
    );
  }

  openEditReservation(reservation: Reservation) {
    const modalRef = this.modalService.open(ReservationEditComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then(() => this.loadData(this.table.id));
  }

  openDeleteReservation(reservation: Reservation) {
    const modalRef = this.modalService.open(ReservationDeleteComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then(() => this.loadData(this.table.id));
  }
}
