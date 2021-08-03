import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { Reservation } from 'src/app/dtos/reservation';
import { TableService } from 'src/app/services/table.service';
import { AlertService } from 'src/app/services/alert.service';
import { Table } from 'src/app/dtos/table';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ReservationService } from 'src/app/services/reservation.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { fabric } from 'fabric';
import { FloorLayoutService } from '../../../../services/floor-layout.service';
import { TimeUtilsService } from 'src/app/services/time-utils.service';
import { timer } from 'rxjs/internal/observable/timer';



enum ReservationFormType {
  add = 'Add',
  edit = 'Edit',
  details = 'Details'
}

//reference to component, required as for some canvas stuff this refers to something else
let component;

@Component({
  selector: 'app-reservation-form',
  templateUrl: './reservation-form.component.html',
  styleUrls: ['./reservation-form.component.scss']
})
export class ReservationFormComponent implements OnInit, OnChanges {
  @Input() type: ReservationFormType;
  @Input() initialReservation: Reservation;

  @Output() submitReservation = new EventEmitter<Reservation>();


  submitted: boolean = false;
  reservationForm: FormGroup;
  tables: Table[] = [];
  tableColors: string[];
  checkedTables: boolean[];
  tableSelection: Table[];

  //fabric.js stuff
  canvas;
  layoutLoaded: boolean = false;
  tablesInReservations: Table[];
  editFormInitialized: boolean = false;//this is part of an ugly workaround

  lastValidEndTime: string;
  dirtiedEndTime = false;

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private reservationService: ReservationService,
    private tableService: TableService,
    private alertService: AlertService,
    private floorLayoutService: FloorLayoutService,
    private timeUtilsService: TimeUtilsService) {
    component = this;
  }

  ngOnInit(): void {
    //this.initReservationFormGroup();
    //this.loadAllTables();
  }

  ngOnChanges(): void {
    this.initReservationFormGroup();
    this.loadAllTables();
    this.setupLayout();
  }

  public loadAllTables() {
    this.tableService.getAllTables().subscribe(
      (tables: Table[]) => {
        this.tables = tables;
        this.checkedTables = new Array<boolean>(tables.length);
        this.tableColors = new Array<string>(tables.length);
        this.setAllTableColors();
        this.setPreSelectedTables();
      },
      error => {
        this.alertService.error(error);
      }
    );
  }

  public setAllTableColors() {
    for (let i = 0; i < this.tables.length; i++) {
      if (!this.tables[i].active) {
        this.tableColors[i] = '#d9534f';
      } else {
        this.setColor(i);
      }
    }
  }

  public setColor(index: number) {
    this.reservationService.getReservationsByStartAndEndDateTime(
      this.reservationForm.controls.dateAsString.value + 'T' +
      this.reservationForm.controls.startTime.value,
      this.reservationForm.controls.dateAsString.value + 'T' +
      this.reservationForm.controls.endTime.value).subscribe(
        (reservations: Reservation[]) => {
          this.setColorDependentOnCollision(index, reservations);
        },
        error => {
          this.alertService.error(error);
        }
      );
  }

  private setColorDependentOnCollision(index: number, reservations: Reservation[]) {
    let collision = false;
    for (let i = 0; i < reservations.length; i++) {
      if (reservations[i].id !== this.initialReservation.id) {
        for (let j = 0; j < reservations[i].restaurantTables.length; j++) {
          if (reservations[i].restaurantTables[j].id === this.tables[index].id) {
            this.tableColors[index] = '#f0ad4e';
            collision = true;
          }
        }
      }
    }
    if (!collision) {
      this.tableColors[index] = '#292b2c';
    }
  }

  setPreSelectedTables() {
    for (let i = 0; i < this.initialReservation.restaurantTables.length; i++) {
      for (let j = 0; j < this.tables.length; j++) {
        if (this.initialReservation.restaurantTables[i].id === this.tables[j].id) {
          this.checkedTables[j] = true;
        }
      }
    }
  }

  isDateInPast() {
    const now = new Date();
    now.setTime(now.getTime() - new Date().getTimezoneOffset() * 60 * 1000);
    return this.reservationForm.controls.dateAsString.value + 'T' +
      this.reservationForm.controls.startTime.value < now.toISOString();
  }

  /*nothingChecked(): boolean {
    let nothingChecked = true;
    for (let i = 0; i < this.checkedTables.length; i++) {
      if (this.checkedTables[i]) {
        nothingChecked = false;
      }
    }
    return nothingChecked;
  }*/

  /*invalidChecked(): boolean {
     let invalidChecked = false;
     for (let i = 0; i < this.checkedTables.length; i++) {
       if (this.checkedTables[i] && (!this.tables[i].active || this.tableColors[i] === '#f0ad4e')) {
         invalidChecked = true;
       }
     }
     return invalidChecked;
   }*/

  public onDateChanged(dateAsString: string): void {
    const dateInForm = this.reservationForm.controls.dateAsString.value;
    console.log(`date in reservation form: ${dateInForm}, new date:${dateAsString}`);
    if (!(dateInForm == dateAsString)) {
      this.tableSelection = [];//reset tableSelection as it might be invalid for new time frame
      this.updateLayout();
      //this.setAllTableColors();
    }
    this.reservationForm.controls.dateAsString.setValue(dateAsString);
  }

  public onStartTimeChanged(startTime: string) {
    const startTimeInForm = this.reservationForm.controls.startTime.value;
    const endTimeInForm = this.reservationForm.controls.endTime.value;
    console.log('onStartTimeChanged: ' + startTime);
    const currentDurationInMinutes = this.timeUtilsService.getDurationInMinutes(this.reservationForm.controls.startTime.value,
      this.lastValidEndTime);

    const endTime = this.timeUtilsService.getEndTimeByStartTimeAndOffsetInMinutes(startTime, currentDurationInMinutes);
    this.lastValidEndTime = endTime;

    if (!(startTimeInForm == startTime) || !(endTimeInForm == this.lastValidEndTime)) {
      this.tableSelection = [];//reset tableSelection as it might be invalid for new time frame
      this.updateLayout();
      //this.setAllTableColors();
    }

    this.reservationForm.controls.startTime.setValue(startTime);
    this.reservationForm.controls.endTime.setValue(this.lastValidEndTime);
  }

  public onEndTimeChanged(endTime: string) {
    console.log('onEndTimeChanged( ' + endTime + ' )');
    const oldEndTimeInForm = this.reservationForm.controls.endTime.value;
    if (this.timeUtilsService.timeOneIsBeforeOrEqualToTimeTwo(endTime, this.reservationForm.controls.startTime.value)) {
      console.log('lastValidEndTime should remain: ' + this.lastValidEndTime);
      this.alertService.reportErrorMessageModal('End time must be after start time.');
      this.reservationForm.controls.endTime.setValue(this.lastValidEndTime);
      this.dirtiedEndTime = true;
      timer(100).subscribe(x => {
        this.dirtiedEndTime = false;
      });
    } else {
      this.reservationForm.controls.endTime.setValue(endTime);
      this.lastValidEndTime = endTime;
      console.log('lastValidEndTime was set to new value: ' + this.lastValidEndTime);
    }

    const newEndTimeInForm = this.reservationForm.controls.endTime.value;
    if (!(oldEndTimeInForm == newEndTimeInForm)) {
      this.tableSelection = [];//reset tableSelection as it might be invalid for new time frame
      this.updateLayout();
      //this.setAllTableColors();
    }
  }

  /*setCheck(index: number) {
    this.checkedTables[index] = !this.checkedTables[index];
  }*/

  onSubmit() {
    this.submitted = true;
    if (this.reservationForm.valid && !this.isDateInPast()) {
      if (this.tableSelection.length > 0) {
        const reservationToSubmit = new Reservation(
          this.initialReservation.id,
          this.reservationForm.controls.guestName.value,
          this.reservationForm.controls.numberOfGuests.value,
          this.reservationForm.controls.contactInformation.value,
          this.reservationForm.controls.comment.value,
          this.reservationForm.controls.dateAsString.value + 'T' +
          this.reservationForm.controls.startTime.value,
          this.reservationForm.controls.dateAsString.value + 'T' +
          this.reservationForm.controls.endTime.value,
          this.tableSelection);
        this.submitReservation.emit(reservationToSubmit);
      }
    }
  }
  /*if (this.reservationForm.valid && !this.invalidChecked() && !this.isDateInPast()) {
    const tablesToBook: Table[] = [];
    let somethingChecked = false;
    for (let i = 0; i < this.checkedTables.length; i++) {
      if (this.checkedTables[i]) {
        somethingChecked = true;
        tablesToBook.push(new Table(this.tables[i].id, null, null, null, null, null));
      }
    }
    if (somethingChecked) {
      const reservationToSubmit = new Reservation(
        this.initialReservation.id,
        this.reservationForm.controls.guestName.value,
        this.reservationForm.controls.numberOfGuests.value,
        this.reservationForm.controls.contactInformation.value,
        this.reservationForm.controls.comment.value,
        this.reservationForm.controls.dateAsString.value + 'T' +
        this.reservationForm.controls.startTime.value,
        this.reservationForm.controls.dateAsString.value + 'T' +
        this.reservationForm.controls.endTime.value,
        tablesToBook);
      this.submitReservation.emit(reservationToSubmit);
    }
  }
}*/


  private initReservationFormGroup() {
    this.reservationForm = this.formBuilder.group({
      guestName: [this.initialReservation.guestName, Validators.required],
      numberOfGuests: [this.initialReservation.numberOfGuests, Validators.required],
      contactInformation: [this.initialReservation.contactInformation],
      comment: [this.initialReservation.comment],
      dateAsString: [this.initialReservation.startDateTime.substring(0, 10), Validators.required],
      startTime: [this.initialReservation.startDateTime.substring(11, 16), Validators.required],
      endTime: [this.initialReservation.endDateTime.substring(11, 16), Validators.required]
    });

    this.lastValidEndTime = this.initialReservation.endDateTime.substring(11, 16);

    if (this.type === ReservationFormType.details) {
      this.reservationForm.disable();
    }

  }

  private initTableSelectionForEditForm() {
    if (this.editFormInitialized) return;//this is part of an ugly workaround
    let temp = [];
    this.initialReservation.restaurantTables.forEach(table => {
      if (!temp.find(element => element.id === table.id)) {
        temp.push(table);
      }
    });
    this.tableSelection = temp;
    console.log(`initialized table selection with ${this.tableSelection.length} table${this.tableSelection.length == 1 ? '' : 's'}`);
    console.log(this.tableSelection);
  }

  fillFormForWalkInCustomer() {
    const today = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    const locationOfDateSeparator = today.indexOf('T');
    const currentTime = today.substring(locationOfDateSeparator + 1);
    const guestName = 'Walk-in customer at ' + currentTime;
    this.reservationForm.controls.guestName.setValue(guestName);
    this.reservationForm.controls.numberOfGuests.setValue(1);
  }

  getTableSuggestion() {
    const numberOfGuests = this.reservationForm.controls.numberOfGuests.value;
    const startDateTime = this.reservationForm.controls.dateAsString.value + 'T' + this.reservationForm.controls.startTime.value;

    const endDateTime = this.reservationForm.controls.dateAsString.value + 'T' + this.reservationForm.controls.endTime.value;

    let idOfReservationToIgnore : string = '';

    if (this.initialReservation === undefined
      || this.initialReservation.id === undefined
      || this.initialReservation.id === null ) {
        idOfReservationToIgnore = '';
    } else {
        idOfReservationToIgnore = this.initialReservation.id.toString();
    }

    this.tableService.getTableSuggestion(numberOfGuests, idOfReservationToIgnore, startDateTime, endDateTime).subscribe(
      (tables: Table[]) => {
        console.log(tables);
        this.tableSelection = tables;
        this.updateLayout();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }

  public setupLayout() {
    this.canvas = new fabric.Canvas('canvas-reservation');
    this.canvas.setWidth(800);
    this.canvas.setHeight(847);
    this.loadLayout();
  }

  private loadLayout() {
    this.floorLayoutService.getLayoutWithId(1).subscribe(
      data => {
        console.log('Layout loaded successfully!');
        this.layoutLoaded = true;
        this.canvas.loadFromJSON(data.serializedLayout, () => {
          this.canvas.hoverCursor = 'pointer';
          this.canvas.forEachObject(function (objGroup) {
            objGroup.selectable = false;
            if (objGroup.id) {
              objGroup.on('mousedown', function () {
                component.toggleTableSelected(objGroup);
              });
            }
            if (objGroup.type === 'polygon') {
              objGroup.hoverCursor = 'default';
            }
          });
          //scale canvas so it fits into form
          const tableContainer = document.getElementById('table-container');
          const containerWidth = tableContainer.clientWidth;
          const containerHeight = tableContainer.clientHeight;
          let scaleRatio = Math.min(containerWidth / this.canvas.getWidth(), containerHeight / this.canvas.getHeight());
          this.canvas.setDimensions({ width: this.canvas.getWidth() * scaleRatio, height: this.canvas.getHeight() * scaleRatio });
          this.canvas.setZoom(scaleRatio);

          if (this.type === ReservationFormType.edit) this.initTableSelectionForEditForm();//this is part of an ugly workaround

          this.markSelectedTables();
          this.markReservedTables();
          this.canvas.renderAll();
        });
      },
      error => {
        console.log('There is no layout saved in the database!');
      }
    );
  }

  private toggleTableSelected(objGroup) {
    //objGroup is a table and reservations defined
    if (objGroup.id && this.tablesInReservations) {
      //find corresponding table
      const table = this.tables.filter(table => table.id === objGroup.id)[0];
      //table not reserved and not deactivated
      if (this.tablesInReservations.filter(element => element.id === objGroup.id).length == 0 && table.active) {
        let rectangle = objGroup.item(0);
        //table not selected
        if (this.tableSelection.filter(element => element.id === table.id).length == 0) {
          this.tableSelection = this.tableSelection.concat([table]);//concatenating as array has to be reassigned for ngOnChanges() to recognize change
          rectangle.set('stroke', 'green');
        }
        else {//table selected
          this.tableSelection = this.tableSelection.filter(table => table.id != objGroup.id);//remove table from selected tables
          rectangle.set('stroke', 'black');
        }
        this.canvas.renderAll();
      }
    }
    console.log(`selected tables:${this.tableSelection.reduce((a, b) => a + " " + b.tableNum, "")}`);
  }

  private markSelectedTables() {
    this.canvas.forEachObject(objGroup => {
      //check if objGroup is a table (has ID field) and selectedTables is set/defined
      if (objGroup.id && component.tableSelection) {
        const selectedTable = component.tableSelection.filter(table => table.id == objGroup.id)[0];
        if (selectedTable) {
          console.log(selectedTable);
          let rectangle = objGroup.item(0);
          rectangle.set('stroke', 'green');
        }
      };
    });
    this.canvas.renderAll();
  }

  private markReservedTables() {
    this.reservationService.filterReservations(
      null,
      this.reservationForm.controls.dateAsString.value + 'T' + this.reservationForm.controls.startTime.value,
      this.reservationForm.controls.dateAsString.value + 'T' + this.reservationForm.controls.endTime.value,
      null).subscribe(
        reservations => {
          let tables = [];
          //if edit-form was opened: tables from current reservation shouldn't be marked as reserved, we want to be able to change them!
          if (this.type === ReservationFormType.edit) {
            reservations = reservations.filter(reservation => reservation.id != this.initialReservation.id);
          }
          for (let reservation of reservations) {
            for (let table of reservation.restaurantTables) {
              if (!tables.includes(table)) tables.push(table);
            }
          }
          this.tablesInReservations = tables;
          this.canvas.forEachObject(function (objGroup) {
            if (objGroup.id) {
              let rectangle = objGroup.item(0);
              let tableOfLayout = component.tablesInReservations.find(element =>
                element.id === objGroup.id);
              if (tableOfLayout) {
                rectangle.set('stroke', 'red');
              }
            }
          });
          this.canvas.renderAll();
        },
        error => {
          console.log('Something went wrong when filtering and marking reserved tables!');
        }
      );
  }

  private updateLayout() {
    this.canvas.forEachObject(function (objGroup) {
      if (objGroup.id) {
        if (!component.tables) return console.error("tables not defined!");
        const table = component.tables.filter(table => table.id === objGroup.id)[0];
        if (!table) return console.error("Not table with same ID as element from floorplan found!");

        let rectangle = objGroup.item(0);
        let text = objGroup.item(1)
        //all active tables have to be "repainted" as they could be free as soon as the
        if (table.active) {
          rectangle.set('fill', '#eccbaf');
          rectangle.set('stroke', 'black');
        }
      }
    });
    this.markSelectedTables();
    this.markReservedTables();
    this.canvas.renderAll();
  }

  private getStringForSelectedTables() {
    if (!this.tableSelection || this.tableSelection.length < 1) return 'none';
    return `${this.tableSelection.reduce((a, b) => a + ", " + b.tableNum, "").slice(2)}`;
  }


  onNumberOfGuestsInput() {
    this.tableSelection = []; //reset tableSelection as it might be invalid for new number of guests.
    this.updateLayout();
  }
}
