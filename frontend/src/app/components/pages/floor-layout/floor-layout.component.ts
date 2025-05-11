import {Component, ElementRef, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {TableService} from '../../../services/table.service';
import {Table} from '../../../dtos/table';
import {AlertService} from '../../../services/alert.service';
import * as fabric from 'fabric';
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
import {TableComponent} from "./table/table.component";
import {NgIf} from "@angular/common";
import {Canvas} from "fabric";

let layoutRef;

@Component({
  selector: 'app-floor-layout',
  templateUrl: './floor-layout.component.html',
  styleUrls: ['./floor-layout.component.scss'],
  standalone: true,
  imports: [
    TableComponent,
    NgIf
  ],
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

  protected tableList: Table[];
  private nrOfTables: number;
  private canvas: Canvas;
  private locked = !this.authService.isAdmin();
  private layoutLoaded: boolean;
  private clickedTable: Table;
  protected layoutEdited: boolean;
  protected editingWalls = false;
  private selectedTable: Table;

  private deleteImg: HTMLImageElement;
  private editImg: HTMLImageElement;
  private statusImg: HTMLImageElement;
  private cloneImg: HTMLImageElement;
  private customControls: any;

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
    this.tableService.getAllTables().subscribe({
      next: (tables: Table[]) => {
        this.tableList = tables;
        this.nrOfTables = tables.length;
        this.loadLayout();
      }
      ,
      error: (error) => {
        console.log('Failed to load tables.');
        this.alertService.error(error);
      }
    });
  }

  onUpdateTables(tables: Table[]) {
    this.tableList = tables;
    this.nrOfTables = tables.length;
    this.loadLayout();
  }

  fabricCanvas() {
    // Create and configure canvas
    this.canvas = new fabric.Canvas('c', {
      width: 800,
      height: 800,
      selection: false
    });

    // Define icons
    const icons = {
      delete: 'https://image.flaticon.com/icons/png/512/61/61848.png',
      edit: 'https://image.flaticon.com/icons/png/512/84/84380.png',
      status: 'https://freesvg.org/img/power-icon.png',
      clone: 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Font_Awesome_5_regular_copy.svg/200px-Font_Awesome_5_regular_copy.svg.png'
    };

    const deleteImg = new Image();
    deleteImg.src = icons.delete;

    const editImg = new Image();
    editImg.src = icons.edit;

    const statusImg = new Image();
    statusImg.src = icons.status;

    const cloneImg = new Image();
    cloneImg.src = icons.clone;

    // Canvas global object defaults
    fabric.FabricObject.prototype.objectCaching = false;
    fabric.FabricObject.prototype.transparentCorners = false;
    fabric.FabricObject.prototype.cornerColor = 'blue';
    fabric.FabricObject.prototype.cornerStyle = 'circle';

    // Add custom controls to each object manually
    const addCustomControls = (obj: fabric.Object) => {
      obj.controls = {
        deleteControl: new fabric.Control({
          x: 0.5,
          y: -0.5,
          offsetY: -16,
          offsetX: 16,
          cursorStyle: 'pointer',
          mouseUpHandler: (eventData, target) => this.deleteObjGroup(eventData, target),
          render: this.renderIcon(deleteImg),
        }),
        editControl: new fabric.Control({
          x: -0.5,
          y: -0.5,
          offsetY: -16,
          offsetX: -16,
          cursorStyle: 'pointer',
          mouseUpHandler: (eventData, target) => this.editObjGroup(eventData, target),
          render: this.renderIcon(editImg),
        }),
        statusControl: new fabric.Control({
          x: 0.5,
          y: 0.5,
          offsetY: 16,
          offsetX: 16,
          cursorStyle: 'pointer',
          mouseUpHandler: (eventData, target) => this.changeObjGroupStatus(eventData, target),
          render: this.renderIcon(statusImg),
        }),
        cloneControl: new fabric.Control({
          x: -0.5,
          y: 0.5,
          offsetY: 16,
          offsetX: -16,
          cursorStyle: 'pointer',
          mouseUpHandler: (eventData, target) => this.cloneObjGroup(eventData, target),
          render: this.renderIcon(cloneImg),
        }),
      };
    };

    // Apply snapAngle on selection
    this.canvas.on('selection:created', (ev) => {
      const target = ev.selected?.[0];
      if (target) {
        target.snapAngle = 15;
        addCustomControls(target);
      }
    });

    // Prevent objects from moving outside canvas bounds
    this.canvas.on('object:moving', (e) => {
      const obj = e.target;
      if (!obj || !obj.canvas) return;

      const bounds = obj.getBoundingRect();
      const canvas = obj.canvas;

      // skip oversized objects
      if (bounds.width > canvas.width || bounds.height > canvas.height) return;

      obj.setCoords();

      // restrict movement
      if (bounds.left < 0) {
        obj.left = Math.max(obj.left, obj.left - bounds.left);
      }
      if (bounds.top < 0) {
        obj.top = Math.max(obj.top, obj.top - bounds.top);
      }
      if (bounds.left + bounds.width > canvas.width) {
        obj.left = Math.min(obj.left, canvas.width - bounds.width + obj.left - bounds.left);
      }
      if (bounds.top + bounds.height > canvas.height) {
        obj.top = Math.min(obj.top, canvas.height - bounds.height + obj.top - bounds.top);
      }
    });
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

          objGroup.set('id', table.id);
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

  private changeObjGroupStatus(eventData, target) {
    layoutRef.saveLayout();
    let table = layoutRef.getTableFromList(target.id);
    if (table.active) {
      this.deactivateTable(this.clickedTable)
    } else {
      this.tableService.setTableActive(table.id, true).subscribe({
        next: (data) => this.loadTables(),
        error: (error) => console.error(`could not set table ${table.tableNum} active`)
      })
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

      const text = new fabric.FabricText(table.tableNum.toString(), {
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
      objGroup.set('id', table.id);
      objGroup.selectable = false;
      objGroup.snapAngle = 15;
      this.canvas.add(objGroup);
      i++;
      this.updateTableObjGroup(objGroup);
    }
  }

// Retrieve the polygon used for walls
  getPolygon(): fabric.Polygon | null {
    return this.canvas.getObjects().find(
      (obj) => obj.get('meta')?.type === 'polygon'
    ) as fabric.Polygon | null;
  }

// Delete polygon object safely
  deletePolygon() {
    const polygon = this.getPolygon();
    if (polygon) {
      this.canvas.remove(polygon);
      this.canvas.renderAll();
    }
  }

  deletePointsAndPolygon() {
    this.deletePoints();
    this.deletePolygon();
  }

  enoughPoints(): boolean {
    const polygon = this.getPolygon();
    return polygon ? polygon.points.length > 32 : false;
  }

  splitPoints() {
    const polygon = this.getPolygon();
    if (!polygon) return;

    const points = polygon.points;
    const newPoints: { x: number; y: number }[] = [];
    for (let i = 0; i < points.length; i++) {
      newPoints.push(points[i]);
      newPoints.push({
        x: (points[i].x + points[(i + 1) % points.length].x) / 2,
        y: (points[i].y + points[(i + 1) % points.length].y) / 2,
      });
    }

    this.deletePointsAndPolygon();
    this.createWalls(newPoints);
    this.createPoints();
    this.canvas.renderAll();
  }

  deletePoints() {
    const objects = this.canvas.getObjects();
    objects.forEach((obj) => {
      const meta = obj.get('meta');
      if (meta?.type === 'circle' && meta?.id?.startsWith('p')) {
        this.canvas.remove(obj);
      }
    });
    this.canvas.renderAll();
  }

  createPoints() {
    const polygon = this.getPolygon();
    if (!polygon) return;

    polygon.points.forEach((point, index) => {
      const canvasPoint = fabric.util.transformPoint(
        { x: point.x - polygon.pathOffset.x, y: point.y - polygon.pathOffset.y },
        polygon.calcTransformMatrix()
      );

      const circle = new fabric.Circle({
        width: 20, // diameter (2 * radius)
        height: 20, // diameter (2 * radius)
        fill: 'green',
        left: canvasPoint.x - 10, // Adjust to center the circle
        top: canvasPoint.y - 10, // Adjust to center the circle
        hasBorders: false,
        hasControls: false,
        objectCaching: false,
        excludeFromExport: true,
      });

      circle.set('meta', { id: `p${index}`, type: 'circle' });
      this.canvas.add(circle);
    });

    this.canvas.on('object:moving', (event) => {
      const target = event.target;
      const meta = target.get('meta');
      const id = meta?.('id');
      const type = meta?.('type');
      if (type === 'circle' && id?.startsWith('p')) {
        const index = parseInt(id.slice(1), 10);
        const polygon = this.getPolygon();
        if (polygon) {
          const localPoint = fabric.util.transformPoint(
            {
              x: target.left + 10, // Adjust for center alignment
              y: target.top + 10, // Adjust for center alignment
            },
            fabric.util.invertTransform(polygon.calcTransformMatrix())
          );

          polygon.points[index] = {
            x: localPoint.x + polygon.pathOffset.x,
            y: localPoint.y + polygon.pathOffset.y,
          };

          if (polygon.setCoords) {
            polygon.setCoords(); // Ensure coordinates are updated
          }

          this.canvas.requestRenderAll();
        }
      }
    });
  }


  createDefaultWalls() {
    this.createWalls(this.getDefaultPoints());
  }

  createWalls(points: { x: number; y: number }[]) {
    const polygon = new fabric.Polygon(points, {
      selectable: false,
      objectCaching: false,
      fill: '#DDD',
      evented: false,
      hasBorders: false,
      hasControls: false,
    });

    polygon.set('meta', { type: 'polygon' });
    this.canvas.add(polygon);
    this.canvas.sendObjectToBack(polygon);
  }

  editWalls() {
    this.editingWalls = !this.editingWalls;
    this.editingWalls ? this.createPoints() : this.deletePoints();
    console.log(this.canvas.getObjects().length);
  }

  setWalls() {
    const polygon = this.getPolygon();
    if (!polygon) {
      console.error('No valid polygon found. Aborting setWalls.');
      return;
    }

    const points = polygon.points;
    if (!Array.isArray(points) || points.length < 2) {
      console.error('Invalid or insufficient points for walls.');
      return;
    }

    this.deletePolygon();
    this.createWalls(points);
    this.canvas.renderAll();
  }

  resetWalls() {
    //this.deletePointsAndPolygon();
    this.canvas.clear();
    this.createDefaultWalls();
    this.createPoints();
    this.canvas.renderAll();
  }

// Save the layout to the database
  saveLayout() {
    this.layoutEdited = false;
    const jsonString = JSON.stringify(this.canvas.toJSON());
    this.canvas.forEachObject((objGroup) => {
      const objGroupId = objGroup.get('id');
      if (objGroupId) {
        console.log(objGroupId);
        const prevOriginX = objGroup.originX;
        const prevOriginY = objGroup.originY;
        objGroup.originX = 'center';
        objGroup.originY = 'center';
        const coords = new CenterCoordinates(objGroup.left, objGroup.top);
        layoutRef.tableService.setTableCoordinates(objGroupId, coords).subscribe({
          next: () => console.log(`Coordinates saved for table with ID ${objGroupId}`),
          error: () => console.error(`Failed to save coordinates for table with ID ${objGroupId}`),
        });
        objGroup.originX = prevOriginX;
        objGroup.originY = prevOriginY;
      }
    });

    const layoutObservable = this.layoutLoaded
      ? this.layoutService.updateLayout(new FloorLayout(1, jsonString))
      : this.layoutService.createLayout(new FloorLayout(1, jsonString));

    layoutObservable.subscribe(
      () => console.log('Layout saved/updated successfully!'),
      (error) => {
        console.error('Layout save/update failed!');
        this.alertService.error(error);
      }
    );
  }

// Load the layout from the database
  loadLayout() {
    this.layoutService.getLayoutWithId(1).subscribe({
      next: (data) => {
        console.log('Layout loaded successfully!');
        this.layoutLoaded = true;
        console.log(data.serializedLayout);
        this.canvas.loadFromJSON(data.serializedLayout, () => {
          this.setWalls();
          this.checkForDeletedTables();
          this.checkForNewTables();
          if (this.authService.isAdmin()) {
            this.unlockLayout();
          } else {
            this.lockLayout();
          }
          this.canvas.forEachObject((objGroup) => {
            const objGroupId = objGroup.get('id');
            console.log(`objGroupId: ${objGroupId}`);
            if (objGroupId) {
              objGroup.on('mousedown', () => {
                this.setClickedTable(objGroup);
                this.layoutEdited = true;
              });
              this.updateTableObjGroup(objGroup);
            }
          });
          this.canvas.renderAll();
          this.saveLayout();
        });
      },
      error: () => {
        console.log('No layout found. Creating default layout.');
        this.createDefaultWalls();
        this.createTableObjGroups(this.tableList);
        if (this.authService.isAdmin()) {
          this.unlockLayout();
        } else {
          this.lockLayout();
        }
        this.canvas.renderAll();
        this.saveLayout();
      },
    });
  }

// Lock layout to prevent editing
  lockLayout() {
    this.locked = true;
    this.canvas.discardActiveObject();
    this.canvas.forEachObject((objGroup) => {
      objGroup.selectable = false;
    });
    this.canvas.renderAll();
  }

// Unlock layout for editing
  unlockLayout() {
    this.locked = false;
    this.canvas.forEachObject((objGroup) => {
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
      const objGroupId = objGroup.get('id');
      if (objGroupId) {
        if (!this.tableList.find(table => table.id == objGroupId)) {
          this.removeObjGroupFromLayoutById(objGroupId);
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
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe({
      next: (reservations) => {
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
      error: (error) => {
        this.alertService.error(error);
      }
    });
  }

  private removeObjGroupFromLayoutById(id: number): void {
    let canvasObjects = this.canvas._objects;
    let index;
    for (let i = 0; i < canvasObjects.length; i++) {
      if (canvasObjects[i].get('id') === id) {
        index = i;
      }
    }
    if (index > -1) {
      canvasObjects.splice(index, 1);
      this.saveLayout();
      this.loadLayout();
    } else console.error(`could not remove objGroup with ID ${id} from layout!`);
  }


  protected openAddTableForm() {
    const modalRef = this.modalService.open(TableAddComponent);
    modalRef.result.then(() => {
      this.loadTables();
    });
  }

  private deactivateTable(table: Table) {
    let startDate = this.timeUtilsService.getCurrentLocalTimeAsIsoString();
    this.reservationService.filterReservations(null, startDate, new Date(2099, 12, 31).toISOString(), table.tableNum.toString()).subscribe({
      next: (reservations) => {
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
      error: (error) => {
        this.alertService.error(error);
      }
    });
  }

  public setTableActive(table: Table, active: boolean) {
    this.alertService.vanishAll();
    this.tableService.setTableActive(table.id, active).subscribe({
      next: () => {
      },
      error: (error) => {
        if (error.status === 409) this.alertService.reportError(`Could not deactivate table ${table.tableNum}: there are reservations for it in the future!`);
        else this.alertService.error(error);
      }
    });
  }

  public selectTable(table: Table) {
    this.selectedTable = table;
  }

}
