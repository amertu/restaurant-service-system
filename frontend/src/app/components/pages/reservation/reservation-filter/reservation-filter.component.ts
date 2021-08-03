import { Component, OnInit } from '@angular/core';
import {Observable, Subject, timer} from 'rxjs';
import {Reservation} from '../../../../dtos/reservation';
import {ReservationService} from '../../../../services/reservation.service';
import {AlertService} from '../../../../services/alert.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { ReservationAddComponent } from '../reservation-add/reservation-add.component';
import { ReservationEditComponent } from '../reservation-edit/reservation-edit.component';
import { ReservationDeleteComponent } from '../reservation-delete/reservation-delete.component';
import { formatDate } from '@angular/common';
import {FloorLayoutService} from '../../../../services/floor-layout.service';
import { TableService } from '../../../../services/table.service';
import {fabric} from 'fabric';
import {Table} from '../../../../dtos/table';
import { TimeUtilsService } from 'src/app/services/time-utils.service';
import { BillingAddComponent } from '../../billing/billing-add/billing-add.component';
import { ReservationFinishComponent } from '../reservation-finish/reservation-finish.component';

// [guestName, startDateTime, endDateTime, tableNum]
type filterParamsTuple = [string, string, string, string];

@Component({
  selector: 'app-reservation-filter',
  templateUrl: './reservation-filter.component.html',
  styleUrls: ['./reservation-filter.component.scss']
})
export class ReservationFilterComponent implements OnInit {
  filteredReservations: Observable<Reservation[]>;
  selectedReservation: Reservation;
  initialDate = new Date((Date.now() - new Date().getTimezoneOffset() * 60000)).toISOString();//https://stackoverflow.com/questions/10830357/javascript-toisostring-ignores-timezone-offset
  startDate: string;
  startTime: string;
  endDate: string;
  endTime: string;

  private filterParams = new Subject<filterParamsTuple>();

  // variables for floorplan
  canvas;
  layoutLoaded = false;
  currentTime = new Date();
  currReservations: Reservation[];
  tablesInReservations: Table[];
  allTables: Table[];
  displayMSG = '';
  guestName = '';
  tableNr = '';
  noLayout = false;
  defaultStartTime = '00:00';
  defaultEndTime = '23:45';


  constructor(private reservationService: ReservationService,
              private alertService: AlertService, private floorLayoutService: FloorLayoutService, private tableService: TableService,
              private modalService: NgbModal, private timeUtilsService: TimeUtilsService) {
    setInterval(() => {
      this.currentTime = new Date();
    }, 1);
  }

  ngOnInit(): void {
    this.startTime = this.defaultStartTime;
    this.endTime = this.defaultEndTime;
    this.initialDateToString();
    this.showToday();
    console.log(this.filteredReservations);
    this.loadFilterResults();
    this.tableService.getAllTables().subscribe(
      data => {
        this.allTables = data;
        this.setupLayout();
      }
    );

  }

  public loadFilterResults() {
    this.filteredReservations = this.filterParams.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((filterParams: filterParamsTuple) =>
        this.reservationService.filterReservations(filterParams[0], filterParams[1], filterParams[2], filterParams[3]))
    );
  }

  filter(filterParams: filterParamsTuple) {
    this.filterParams[0] = filterParams[0];
    this.filterParams[1] = filterParams[1];
    this.filterParams[2] = filterParams[2];
    this.filterParams[3] = filterParams[3];
    console.log(this.filterParams);
    this.filterParams.next(filterParams);
    if(filterParams[0] === '') {
      this.guestName = '';
    } else if (filterParams[0] !== undefined) {
      this.guestName = 'Guest: ' + filterParams[0];
    }
    if (filterParams[3] === '') {
      this.tableNr = '';
    } else if (filterParams[3] !== undefined) {
      this.tableNr = 'Reservations with Table ' + filterParams[3];
    }
  }

  public onStartDateChanged(startDate: string) {
    console.log(startDate);
    this.startDate = startDate;
    this.setDisplayMSG();
    const startDateTime = this.constructDateTimeString(this.startDate, this.startTime, this.defaultStartTime);
    this.filterParams[1] = startDateTime;
    this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
  }

  public onStartTimeChanged(startTime: string) {
    console.log(startTime);
    this.startTime = startTime;
    const startDateTime = this.constructDateTimeString(this.startDate, this.startTime, this.defaultStartTime);
    this.filterParams[1] = startDateTime;
    this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
    this.setDisplayMSG();
  }

  public onEndDateChanged(endDate: string) {
    console.log(endDate);
    this.endDate = endDate;
    this.setDisplayMSG();
    const endDateTime = this.constructDateTimeString(this.endDate, this.endTime, this.defaultEndTime);
    this.filterParams[2] = endDateTime;
    this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
  }

  public onEndTimeChanged(endTime: string) {
    console.log(endTime);
    this.endTime = endTime;
    const endDateTime = this.constructDateTimeString(this.endDate, this.endTime, this.defaultEndTime);
    this.filterParams[2] = endDateTime;
    this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
    this.setDisplayMSG();
  }

  public constructDateTimeString(date: string, time: string, defaultTime: string): string {
    date = !date ? this.initialDate : date;
    time = !time ? defaultTime : time;
    const dateTime = date + 'T' + time;
    console.log(dateTime);
    return dateTime;
  }

  public initialDateToString() {
    this.initialDate = this.initialDate.substring(0, this.initialDate.indexOf('T'));
  }


  public showAll() {
    this.displayMSG = 'All reservations';
    this.startTime = this.defaultStartTime;
    this.endTime = this.defaultEndTime;
    this.filter([undefined, undefined, undefined, undefined]);
  }

  public showToday() {
    this.startDate = this.initialDate;
    this.endDate = this.initialDate;
    console.log(this.startTime);
    this.displayMSG = 'Today\'s reservations';
    this.startTime = this.defaultStartTime;
    this.endTime = this.defaultEndTime;
    this.filter([undefined, this.initialDate + 'T' + this.startTime, this.initialDate + 'T' + this.endTime, undefined]);
  }

  public formatDate(date: string): string {
    return date.replace('T', ' ').substring(0, date.length - 3);
  }

  selectReservation(reservation: Reservation) {
    this.selectedReservation = reservation;
  }

  public setupLayout() {
    this.canvas = new fabric.Canvas('canvas');
    this.canvas.setWidth(800);
    this.canvas.setHeight(847);
    this.loadLayout();
  }

  private setDisplayMSG() {
    if (this.startDate === this.initialDate && this.endDate === this.initialDate) {
      this.displayMSG = 'Today\'s reservations';
    } else if (this.startDate === this.endDate) {
      this.displayMSG = 'Reservations on the ' + this.startDate;
    } else {
      this.displayMSG = 'Reservations from ' + this.startDate + ' to ' + this.endDate;
    }
  }

  public showNextTwoHours() {
    this.startDate = this.initialDate;
    this.endDate = this.initialDate;
    const currHour = this.currentTime.getHours();
    const currMin = this.currentTime.getMinutes();
    const currQuarterHour = (Math.trunc(currMin / 15 + 1) * 15) % 60;
    const next2Hour = currQuarterHour === 0 ? 3 : 2;
    this.startTime =  currHour < 10 ? '0' + currHour.toString() : currHour.toString() ;
    this.startTime += currMin < 10 ? ':0' + currMin.toString() : ":" + currMin.toString();
    this.endTime = (currHour + 2) % 24 < 10 ? '0' + ((currHour + next2Hour) % 24).toString() : ((currHour + next2Hour) % 24).toString() ;
    this.endTime += currQuarterHour < 10 ? ':0' + currQuarterHour.toString() : ":" + currQuarterHour.toString();
    this.filter([undefined, this.initialDate + 'T' + this.startTime, this.initialDate + 'T' + this.endTime, undefined]);
    this.setDisplayMSG();
  }

  private loadLayout() {
    this.floorLayoutService.getLayoutWithId(1).subscribe(
      data => {
        const ref = this;
        console.log('Layout loaded successfully!');
        this.layoutLoaded = true;
        this.canvas.hoverCursor = 'pointer';
        this.canvas.loadFromJSON(data.serializedLayout, () => {
          this.canvas.forEachObject(function (objGroup) {
            objGroup.selectable = false;
          });
        });
        this.filteredReservations.subscribe(
          data => {
            this.canvas.hoverCursor = 'pointer';
            this.currReservations = data;
            this.updateTableList();
            console.log(this.currReservations);
            console.log('Reservations updated!');
            this.canvas.forEachObject(function (objGroup) {
              if (objGroup.type !== 'polygon') {
                ref.markUnavailableTables(objGroup);
              }
              if (objGroup.type === 'polygon') {
                objGroup.hoverCursor = 'default';
              }
            });
            this.canvas.renderAll();
          }
        );
      },
      error => {
        console.log('There is no layout saved in the database!');
        this.noLayout = true;
      }
    );

  }

  private markUnavailableTables(objGroup) {
    const rectangle = objGroup.item(0);

    if (!this.tablesInReservations) return console.error('cannot mark unavailable tables: list of reserved tables missing!');
    let tableReserved = this.tablesInReservations.find(element =>
      element.id === objGroup.id);
    if (tableReserved) {
      rectangle.set('stroke', 'red');
      return;
    }

    let table = this.allTables.find(element => element.id === objGroup.id);
    if (table.active) {
      rectangle.set('stroke', 'black');
    }
  }

  private updateTableList() {
    const tables = [];
    for (const reservation of this.currReservations) {
      for (const table of reservation.restaurantTables) {
        tables.push(table);
      }
    }
    this.tablesInReservations = tables;
  }

  reservationStartedInThePast(reservation: Reservation): boolean {
    return this.timeUtilsService.reservationStartedInThePast(reservation);
  }

  reservationStartsInTheFuture(reservation: Reservation): boolean {
    return this.timeUtilsService.reservationStartsInTheFuture(reservation);
  }

  reservationFinishedInThePast(reservation: Reservation): boolean {
    return this.timeUtilsService.reservationFinishedInThePast(reservation);
  }

  openEditReservation(reservation: Reservation) {
    const modalRef = this.modalService.open(ReservationEditComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then(() => this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]));
  }

  openDeleteReservation(reservation: Reservation) {
    console.log('openReservationDelete');

    const modalRef = this.modalService.open(ReservationDeleteComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then((table) => {
      console.log('filter component: the following table was deleted:');
      console.log(table);
      this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
    }, (reason) => {
      // on dismiss
    });
  }

  openReservationAddForm() {

    console.log('openReservationAddForm');

    const modalRef = this.modalService.open(ReservationAddComponent);
    modalRef.result.then(() => this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]));
  }

  openInvoiceReservation(reservation: Reservation) {
    const modalRef = this.modalService.open(BillingAddComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then(() => {
      this.filter([this.filterParams[0], this.filterParams[1], this.filterParams[2], this.filterParams[3]]);
    }, (reason) => {
      // on dismiss
    });
  }

  finishReservation(reservation: Reservation) {
    const modalRef = this.modalService.open(ReservationFinishComponent);
    modalRef.componentInstance.reservation = reservation;
    modalRef.result.then(() => {
      this.showNextTwoHours();
    }, (reason) => {
      // on dismiss
    });
  }


}
