import {Component, ElementRef, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {TableService} from '../../../services/table.service';
import {Table} from '../../../dtos/table';
import {AlertService} from '../../../services/alert.service';
import {fabric} from 'fabric';
import {FloorLayoutService} from '../../../services/floor-layout.service';
import {FloorLayout} from '../../../dtos/floor-layout';
import {AuthService} from '../../../services/auth.service';
import {CenterCoordinates} from '../../../dtos/center-coordinates';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TableAddComponent} from './table/table-add/table-add.component';
import {TableEditComponent} from './table/table-edit/table-edit.component';
import {TableDeleteComponent} from './table/table-delete/table-delete.component';
import {TableDeactivateComponent} from './table/table-deactivate/table-deactivate.component';
import {ReservationService} from '../../../services/reservation.service';
import {TimeUtilsService} from '../../../services/time-utils.service';
import {concat} from 'rxjs';

let layoutRef;

@Component({
  selector: 'app-floor-layout',
  templateUrl: './floor-layout.component.html',
  styleUrls: ['./floor-layout.component.scss'],
  encapsulation: ViewEncapsulation.None
})

export class FloorLayoutComponent implements OnInit {
  @ViewChild('editTableModal') editModal: ElementRef;

  constructor(private tableService: TableService,
              private alertService: AlertService,
              private layoutService: FloorLayoutService,
              public authService: AuthService,
              public modalService: NgbModal,
              private reservationService: ReservationService,
              private timeUtilsService: TimeUtilsService) {
    layoutRef = this;
  }

  tableList: Table[];
  nrOfTables: number;
  canvas;
  locked = !this.authService.isAdmin();
  layoutLoaded: boolean;
  clickedTable: Table;
  layoutEdited: boolean;
  editingWalls = false;
  selectedTable: Table;

  // svg-path strings for seatCount pictogram (see also: assets/user-solid.svg)
  pathHeadString = 'm210.351562 246.632812c33.882813 0 63.222657-12.152343 87.195313-36.128906 23.972656-23.972656 ' +
    '36.125-53.304687 36.125-87.191406 0-33.875-12.152344-63.210938-36.128906-87.191406-23.976563-23.96875-53.3125-36.121094-87.191407-36.121094-33.886718 ' +
    '0-63.21875 12.152344-87.191406 36.125s-36.128906 53.308594-36.128906 87.1875c0 33.886719 12.15625 63.222656 36.132812 87.195312 ' +
    '23.976563 23.96875 53.3125 36.125 87.1875 36.125zm0 0';
  pathBodyString = 'm426.128906 393.703125c-.691406-9.976563-2.089844-20.859375-4.148437-32.351563-2.078125-11.578124-' +
    '4.753907-22.523437-7.957031-32.527343-3.308594-10.339844-7.808594-20.550781-13.371094-30.335938-5.773438-10.15625-12.554688-19-' +
    '20.164063-26.277343-7.957031-7.613282-17.699219-13.734376-28.964843-18.199219-11.226563-4.441407-23.667969-6.691407-' +
    '36.976563-6.691407-5.226563 0-10.28125 2.144532-20.042969 8.5-6.007812 3.917969-13.035156 8.449219-20.878906 13.460938-' +
    '6.707031 4.273438-15.792969 8.277344-27.015625 11.902344-10.949219 3.542968-22.066406 5.339844-33.039063 5.339844-10.972656 ' +
    '0-22.085937-1.796876-33.046874-5.339844-11.210938-3.621094-20.296876-7.625-26.996094-11.898438-7.769532-4.964844-14.800782-9.496094-' +
    '20.898438-13.46875-9.75-6.355468-14.808594-8.5-20.035156-8.5-13.3125 0-25.75 2.253906-36.972656 6.699219-11.257813 ' +
    '4.457031-21.003906 10.578125-28.96875 18.199219-7.605469 7.28125-14.390625 16.121094-20.15625 26.273437-5.558594 9.785157-10.058594 ' +
    '19.992188-13.371094 30.339844-3.199219 10.003906-5.875 20.945313-7.953125 32.523437-2.058594 11.476563-3.457031 22.363282-4.148437 ' +
    '32.363282-.679688 9.796875-1.023438 19.964844-1.023438 30.234375 0 26.726562 8.496094 48.363281 25.25 64.320312 16.546875 15.746094 ' +
    '38.441406 23.734375 65.066406 23.734375h246.53125c26.625 0 48.511719-7.984375 65.0625-23.734375 16.757813-15.945312 ' +
    '25.253906-37.585937 25.253906-64.324219-.003906-10.316406-.351562-20.492187-1.035156-30.242187zm0 0';

  ngOnInit() {
    this.fabricCanvas();
    this.loadTables();
  }

  loadTables() {
    console.log('loadAllTables()');
    this.tableService.getAllTables().subscribe(
      (tables: Table[]) => {
        this.tableList = tables;
        this.nrOfTables = tables.length;
        this.loadLayout();
      },
      error => {
        console.log('Failed to load tables.');
        this.alertService.error(error);
      }
    );
  }

  onUpdateTables(tables: Table[]) {
    this.tableList = tables;
    this.nrOfTables = tables.length;
    this.loadLayout();
  }

  fabricCanvas() {
    this.canvas = new fabric.Canvas('c');
    this.canvas.setWidth(800);
    this.canvas.setHeight(800);
    const deleteIcon = 'https://image.flaticon.com/icons/png/512/61/61848.png';
    const deleteImg = document.createElement('img');
    deleteImg.src = deleteIcon;
    const editIcon = 'https://image.flaticon.com/icons/png/512/84/84380.png';
    const editImg = document.createElement('img');
    editImg.src = editIcon;
    let statusIcon = 'https://freesvg.org/img/power-icon.png';
    let statusImg = document.createElement('img');
    statusImg.src = statusIcon;
    let cloneIcon = 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Font_Awesome_5_regular_copy.svg/200px-Font_Awesome_5_regular_copy.svg.png';
    let cloneImg = document.createElement('img');
    cloneImg.src = cloneIcon;
    fabric.Object.prototype.objectCaching = false;
    fabric.Object.prototype.transparentCorners = false;
    fabric.Object.prototype.cornerColor = 'blue';
    fabric.Object.prototype.cornerStyle = 'circle';
    this.canvas.on('selection:created', function (ev) {
      ev.target.snapAngle = 15;
    });

    fabric.Object.prototype.controls.deleteControl = new fabric.Control({
      position: {x: 0.5, y: -0.5},
      offsetY: -16,
      offsetX: 16,
      cursorStyle: 'pointer',
      mouseUpHandler: (eventData, target) => this.deleteObjGroup(eventData, target),
      render: this.renderIcon(deleteImg),
      cornerSize: 24
    });

    fabric.Object.prototype.controls.editControl = new fabric.Control({
      position: {x: -0.5, y: -0.5},
      offsetY: -16,
      offsetX: -16,
      cursorStyle: 'pointer',
      mouseUpHandler: (eventData, target) => this.editObjGroup(eventData, target),
      render: this.renderIcon(editImg),
      cornerSize: 24
    });

    fabric.Object.prototype.controls.statusControl = new fabric.Control({
      position: {x: 0.5, y: 0.5},
      offsetY: 16,
      offsetX: 16,
      cursorStyle: 'pointer',
      mouseUpHandler: (eventData, target) => this.changeObjGroupStatus(eventData, target),
      render: this.renderIcon(statusImg),
      cornerSize: 24
    });

    fabric.Object.prototype.controls.cloneControl = new fabric.Control({
      position: {x: -0.5, y: 0.5},
      offsetY: 16,
      offsetX: -16,
      cursorStyle: 'pointer',
      mouseUpHandler: (eventData, target) => this.cloneObjGroup(eventData, target),
      render: this.renderIcon(cloneImg),
      cornerSize: 24
    });

    this.canvas.on('object:moving', function (e) {
      var obj = e.target;
      // if object is too big ignore
      if (obj.currentHeight > obj.canvas.height || obj.currentWidth > obj.canvas.width) {
        return;
      }
      obj.setCoords();
      // top-left  corner
      if (obj.getBoundingRect().top < 0 || obj.getBoundingRect().left < 0) {
        obj.top = Math.max(obj.top, obj.top - obj.getBoundingRect().top);
        obj.left = Math.max(obj.left, obj.left - obj.getBoundingRect().left);
      }
      // bot-right corner
      if (obj.getBoundingRect().top + obj.getBoundingRect().height > obj.canvas.height || obj.getBoundingRect().left + obj.getBoundingRect().width > obj.canvas.width) {
        obj.top = Math.min(obj.top, obj.canvas.height - obj.getBoundingRect().height + obj.top - obj.getBoundingRect().top);
        obj.left = Math.min(obj.left, obj.canvas.width - obj.getBoundingRect().width + obj.left - obj.getBoundingRect().left);
      }
    });
    this.canvas.selection = false;
  }

  renderIcon(icon) {
    return function renderIcon(ctx, left, top, styleOverride, fabricObject) {
      const size = this.cornerSize;
      ctx.save();
      ctx.translate(left, top);
      ctx.drawImage(icon, -size / 2, -size / 2, size, size);
      ctx.restore();
    };
  }

  private cloneObjGroup(eventData, target) {
    const pathHeadString = this.pathHeadString;
    const pathBodyString = this.pathBodyString;
    layoutRef.tableService.cloneTable(layoutRef.clickedTable.id).subscribe(
      (table: Table) => {
        layoutRef.tableList.push(table);
        let canvas = target.canvas;
        target.clone(function (cloned) {
          let rect = new fabric.Rect({
            fill: '#eccbaf',
            width: 40,
            height: 60,
            stroke: 'black',
            strokeWidth: 3,
            originX: 'center',
            originY: 'center'
          });
          let text = new fabric.Text(table.tableNum.toString(), {
            fontFamily: 'Arial',
            fontSize: 15,
            textAlign: 'center',
            originX: 'center',
            originY: 'center',
            angle: 270
          });

          const pathHead = new fabric.Path(pathHeadString);
          const pathBody = new fabric.Path(pathBodyString);
          const iconGroup = new fabric.Group([pathHead, pathBody], {
            angle: 270,
            scaleX: .03,
            scaleY: .03,
            left: 1,
            top: 21
          });

          let objGroup = new fabric.Group([rect, text, iconGroup], {
            lockScalingX: false,
            lockScalingY: false,
            left: cloned.left,
            top: cloned.top + 50,
            angle: cloned.angle
          });

          objGroup.id = table.id;
          objGroup.selectable = true;
          objGroup.snapAngle = 15;
          objGroup.on('mousedown', function () {
            layoutRef.setClickedTable(objGroup);
            layoutRef.layoutEdited = true;
          });
          layoutRef.updateTableObjGroup(objGroup);
          canvas.add(objGroup);
        });
        layoutRef.saveLayout();
      },
      error => {
        console.log('Failed to clone table (and objGroup).');
        layoutRef.alertService.error(error);
      }
    );
  }

  private deleteObjGroup(eventData, target) {
    layoutRef.saveLayout();
    layoutRef.deleteTable(layoutRef.clickedTable);
  }

  private editObjGroup(eventData, target) {
    layoutRef.saveLayout();
    layoutRef.openEditTableForm(layoutRef.clickedTable);
  }

  getPolygon(): number {
    const objects = this.canvas.getObjects();
    for (let i = 0; i < objects.length; i++) {
      if (objects[i].name === 'poly') {
        return i;
      }
    }
    return 0;
  }

  getDefaultPoints() {
    return [{x: 50, y: 50},
      {x: 400, y: 50},
      {x: 750, y: 50},
      {x: 750, y: 400},
      {x: 750, y: 750},
      {x: 400, y: 750},
      {x: 50, y: 750},
      {x: 50, y: 400}];
  }

  createDefaultWalls() {
    this.createWalls(this.getDefaultPoints());
  }

  createWalls(points: { x, y }[]) {
    const options = {selectable: false, objectCaching: false};
    const polygon = new fabric.Polygon(points, options);
    polygon.name = 'poly';
    polygon.fill = '#DDD';
    polygon.evented = false;
    polygon.hasBorders = false;
    polygon.hasControls = false;
    polygon.toObject = (function (toObject) {
      return function () {
        return fabric.util.object.extend(toObject.call(this), {
          name: this.name
        });
      };
    })(polygon.toObject);
    this.canvas.add(polygon);
    this.canvas.sendToBack(polygon);
  }

  createPoints() {
    const polygon = this.canvas.getObjects()[this.getPolygon()];
    const points = polygon.points;
    const options = {objectCaching: false};
    for (let i = 0; i < points.length; i++) {
      const circle = new fabric.Circle({
        radius: 10,
        fill: 'green',
        left: points[i].x,
        top: points[i].y,
        originX: 'center',
        originY: 'center',
        hasBorders: false,
        hasControls: false,
        name: 'p' + i
      }, {options});
      circle.excludeFromExport = true;
      this.canvas.add(circle);
    }
    this.canvas.on('object:moving', function (obj) {
      if (obj.target.name && obj.target.name.substring(0, 1) === 'p') {
        polygon.points[obj.target.name.substring(1)] = {
          x: obj.target.getCenterPoint().x,
          y: obj.target.getCenterPoint().y
        };
      }
    });
  }

  private changeObjGroupStatus(eventData, target) {
    layoutRef.saveLayout();
    let table = layoutRef.getTableFromList(target.id);
    if (table.active) {
      this.deactivateTable(this.clickedTable)
    } else {
      this.tableService.setTableActive(table.id, true).subscribe(
        data => {
          this.loadTables();
        },
        error => console.error(`could not set table ${table.tableNum} active`)
      )
    }
  }

  createTableObjGroups(tableList) {
    console.log(`creating objGroup for each table`);
    let i = 0;
    for (const table of tableList) {
      const rect = new fabric.Rect({
        fill: '#eccbaf',
        width: 40,
        height: 60,
        stroke: 'black',
        strokeWidth: 3,
        originX: 'center',
        originY: 'center'

      });

      const text = new fabric.Text(table.tableNum.toString(), {
        fontFamily: 'Arial',
        fontSize: 15,
        textAlign: 'center',
        originX: 'center',
        originY: 'center',
        angle: 270
      });

      const pathHead = new fabric.Path(this.pathHeadString);
      const pathBody = new fabric.Path(this.pathBodyString);
      const iconGroup = new fabric.Group([pathHead, pathBody], {
        angle: 270,
        scaleX: .03,
        scaleY: .03,
        left: 1,
        top: 21
      });

      const objGroup = new fabric.Group([rect, text, iconGroup], {
        lockScalingX: false,
        lockScalingY: false,
        left: 100,
        top: 250 + (i * 70)
      });
      objGroup.id = table.id;
      objGroup.selectable = false;
      objGroup.snapAngle = 15;
      this.canvas.add(objGroup);
      i++;
      this.updateTableObjGroup(objGroup);
    }
  }

  enoughPoints(): boolean {
    const poly = this.canvas.getObjects()[this.getPolygon()];
    return poly && poly.points.length > 32;
  }

  splitPoints() {
    const poly = this.canvas.getObjects()[this.getPolygon()];
    const points = poly.points;
    const newPoints = [];
    for (let i = 0; i < points.length; i++) {
      newPoints.push(points[i]);
      newPoints.push({
        x: (points[i].x + points[(i + 1) % points.length].x) / 2,
        y: (points[i].y + points[(i + 1) % points.length].y) / 2
      });
    }
    this.deletePointsAndPolygon();
    this.createWalls(newPoints);
    this.createPoints();
    this.canvas.renderAll();
  }

  deletePointsAndPolygon() {
    this.deletePoints();
    this.deletePolygon();
  }

  deletePoints() {
    const objects = this.canvas.getObjects();
    for (let i = objects.length - 1; i >= 0; i--) {
      if (objects[i].name && objects[i].name.substring(0, 1) === 'p' && objects[i].name !== 'poly') {
        this.canvas.remove(objects[i]);
      }
    }
    this.canvas.renderAll();
  }

  deletePolygon() {
    this.canvas.remove(this.canvas.getObjects()[this.getPolygon()]);
  }

  editWalls() {
    this.editingWalls = !this.editingWalls;
    if (this.editingWalls) {
      this.createPoints();
    } else {
      this.deletePoints();
    }
    console.log(this.canvas.getObjects().length);
  }

  setWalls() {
    const objects = this.canvas.getObjects();
    const points = objects[this.getPolygon()].points;
    this.deletePolygon();
    this.createWalls(points);
    this.canvas.renderAll();
  }

  resetWalls() {
    this.deletePointsAndPolygon();
    this.createDefaultWalls();
    this.createPoints();
    this.canvas.renderAll();
  }

  saveLayout() {
    this.layoutEdited = false;
    const jsonString = JSON.stringify(this.canvas.toJSON(['id', 'snapAngle']));
    this.canvas.forEachObject(function (objGroup) {
      //only if the id property exists, we are dealing with a table
      if (objGroup.id) {
        console.log(objGroup.id);
        //store values originX and originY were stored to
        const prevOriginX = objGroup.originX;
        const prevOriginY = objGroup.originY;
        //set originX and originY to center so that left and top properties refer to center of objGroup
        objGroup.originX = 'center';
        objGroup.originY = 'center';
        const coords = new CenterCoordinates(objGroup.left, objGroup.top);
        layoutRef.tableService.setTableCoordinates(objGroup.id, coords).subscribe(
          () => console.log(`successfully saved coordinates for objGroup w/ ID ${objGroup.id}`),
          () => console.error(`could not save coordinates for objGroup w/ ID ${objGroup.id}`)
        );
        objGroup.originX = prevOriginX;
        objGroup.originY = prevOriginY;
      }
    });
    if (!this.layoutLoaded) {
      this.layoutService.createLayout(new FloorLayout(1, jsonString)).subscribe(
        () => {
          console.log('Layout saved successfully!');
          console.log(JSON.parse(jsonString));
        },
        error => {
          console.error('Layout could not be saved!');
          this.alertService.error(error);
        }
      );
    } else {
      this.layoutService.updateLayout(new FloorLayout(1, jsonString)).subscribe(
        () => {
          console.log('Layout updated successfully!');
        },
        error => {
          console.error('Layout could not be updated!');
        }
      );
    }
  }

  loadLayout() {
    this.layoutService.getLayoutWithId(1).subscribe(
      data => {
        console.log('Layout loaded successfully!');
        this.layoutLoaded = true;
        this.canvas.loadFromJSON(data.serializedLayout, () => {
          this.setWalls();
          this.checkForDeletedTables();
          this.checkForNewTables();
          let ref = this;
          if (this.authService.isAdmin()) {
            this.unlockLayout();
          } else {
            this.lockLayout();
          }
          if (!this.locked) {
            console.log(this.canvas.toJSON(['id', 'snapAngle']));
          }
          this.canvas.forEachObject(function (objGroup) {
            if (objGroup.id) {
              if (!ref.locked) {
                objGroup.on('mousedown', function () {
                  ref.setClickedTable(objGroup);
                  ref.layoutEdited = true;
                });
              }
              ref.updateTableObjGroup(objGroup);
            }
          });
          this.canvas.renderAll();
          this.saveLayout();
        });
      },
      error => {
        console.log('There is no layout saved in the database!');
        this.createDefaultWalls();
        this.createTableObjGroups(this.tableList);
        if (this.authService.isAdmin()) {
          this.unlockLayout();
        } else {
          this.lockLayout();
        }
        this.canvas.forEachObject(function (objGroup) {
          if (objGroup.id) {
            objGroup.on('mousedown', function () {
              layoutRef.setClickedTable(objGroup);
              layoutRef.layoutEdited = true;
            });
            layoutRef.updateTableObjGroup(objGroup);
          }
        });
        this.canvas.renderAll();
        this.saveLayout();
      }
    );
  }

  lockLayout() {
    this.locked = true;
    this.canvas.discardActiveObject();
    this.canvas.forEachObject(function (objGroup) {
      objGroup.selectable = false;
    });
    this.canvas.renderAll();
  }

  unlockLayout() {
    this.locked = false;
    this.canvas.forEachObject(function (objGroup) {
      objGroup.selectable = true;
    });
    this.canvas.renderAll();
  }

  checkForNewTables(): void {
    this.tableList.length;
    let objGroupCount = this.canvas.getObjects().length - 1;
    let tableCount = this.tableList.length;
    console.log(`objGroupCount: ${objGroupCount}, tableCount: ${tableCount}`);
    if (objGroupCount < tableCount) {
      this.createTableObjGroups(this.tableList.slice(objGroupCount, tableCount));
    }
    this.canvas.renderAll();
  }

  checkForDeletedTables(): void {
    this.canvas.forEachObject((objGroup) => {
      //only if the id property exists, we are dealing with a table
      if (objGroup.id) {
        if (!this.tableList.find(table => table.id == objGroup.id)) {
          this.removeObjGroupFromLayoutById(objGroup.id);
        }
      }
    });
  }

  setClickedTable(objGroup): void {
    this.clickedTable = this.tableList.find(table =>
      table.id === objGroup.id
    );
  }

  getTableFromList(id: number): Table {
    let foundTable = this.tableList.find(element =>
      element.id === id);
    return foundTable;
  }

  updateTableObjGroup(objGroup): void {
    let currTable = this.getTableFromList(objGroup.id);
    let rectangle = objGroup.item(0);
    let text = objGroup.item(1);
    let userIconHead = objGroup.item(2).item(0);
    let userIconBody = objGroup.item(2).item(1);
    text.set('text', currTable.tableNum.toString() + '\n' + currTable.seatCount);
    if (currTable.active) {
      rectangle.set('fill', '#eccbaf');
      rectangle.set('stroke', 'black');
      text.set('fill', 'black');
      userIconHead.set('fill', 'black');
      userIconBody.set('fill', 'black');
    } else {
      console.log(`table ${currTable.tableNum} is ${currTable.active ? 'active' : 'inactive'} and will be painted white`);
      rectangle.set('fill', 'white');
      rectangle.set('stroke', 'grey');
      text.set('fill', 'grey');
      userIconHead.set('fill', 'grey');
      userIconBody.set('fill', 'grey');
    }
  }

  public openEditTableForm(table: Table) {
    const modalRef = this.modalService.open(TableEditComponent);
    modalRef.componentInstance.table = table;
    modalRef.result.then(() => {
      this.loadTables();
      this.loadLayout();
    });
  }

  public deleteTable(table: Table) {
    let startDate = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe(
      reservations => {
        //open delete table form
        const modalRef = this.modalService.open(TableDeleteComponent);
        modalRef.componentInstance.table = table;
        modalRef.componentInstance.reservations = reservations;
        //if close (delete button click) and not dismiss(any other button or left modal), reload tables and layout
        modalRef.result.then(() => {
          this.loadTables();
          this.removeObjGroupFromLayoutById(table.id);
          this.loadLayout();
        });
      },
      error => {
        this.alertService.error(error);
      }
    );
  }

  private removeObjGroupFromLayoutById(id: number): void {
    let canvasObjects = this.canvas._objects;
    let index;
    for (let i = 0; i < canvasObjects.length; i++) {
      if (canvasObjects[i].id === id) {
        index = i;
      }
    }
    if (index > -1) {
      canvasObjects.splice(index, 1);
      this.saveLayout();
      this.loadLayout();
    } else console.error(`could not remove objGroup with ID ${id} from layout!`);
  }


  private openAddTableForm() {
    const modalRef = this.modalService.open(TableAddComponent);
    modalRef.result.then(() => {
      this.loadTables();
    });
  }

  private deactivateTable(table: Table) {
    let startDate = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe(
      reservations => {
        if (reservations.length > 0) {
          const modalRef = this.modalService.open(TableDeactivateComponent);
          modalRef.componentInstance.table = table;
          modalRef.componentInstance.reservations = reservations;
          modalRef.result.then(() => {
            this.loadTables();
            this.loadLayout();
          });
        } else {
          console.log(`no reservations in future for table ${table.tableNum}`);
          this.tableService.setTableActive(table.id, false).subscribe(
            table => {
              this.loadTables();
              this.loadLayout();
            }
          );
        }
      },
      error => {
        this.alertService.error(error);
      }
    );
  }

  public setTableActive(table: Table, active: boolean) {
    this.alertService.vanishAll();
    this.tableService.setTableActive(table.id, active).subscribe(
      () => {

      },
      error => {
        if (error.status === 409) this.alertService.reportError(`Could not deactivate table ${table.tableNum}: there are reservations for it in the future!`);
        else this.alertService.error(error);
      }
    );
  }

  public selectTable(table: Table) {
    this.selectedTable = table;
  }

}
