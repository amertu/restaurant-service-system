import {Component, ElementRef, AfterViewInit, ViewChild, ViewEncapsulation} from '@angular/core';
import * as fabric from 'fabric';
import {ICON_PATHS} from '../../../constants/icons';
import {Table} from '../../../dtos/table';
import {TimeUtilsService} from '../../../services/time-utils.service';
import {ReservationService} from '../../../services/reservation.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../../services/auth.service';
import {FloorLayoutService} from '../../../services/floor-layout.service';
import {AlertService} from '../../../services/alert.service';
import {TableService} from '../../../services/table.service';
import {TableComponent} from './table/table.component';
import {CanvasControlsService} from '../../../services/canvas-controls.service';
import {IconLoaderService} from '../../../services/icon-loader.service';

interface CanvasTable extends fabric.Group {
  tableId?: number;
  tableData?: any;
}

@Component({
  selector: 'app-floor-layout',
  templateUrl: './floor-layout.component.html',
  styleUrls: ['./floor-layout.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [
    TableComponent
  ]
})
export class FloorLayoutComponent implements AfterViewInit {
  @ViewChild('canvasEl', {static: false}) canvasRef!: ElementRef<HTMLCanvasElement>;
  canvas!: fabric.Canvas;
  tables: Table[];
  deleteImg: HTMLImageElement;

  constructor(private tableService: TableService,
              private alertService: AlertService,
              private layoutService: FloorLayoutService,
              public authService: AuthService,
              public modalService: NgbModal,
              private reservationService: ReservationService,
              private timeUtilsService: TimeUtilsService,
              private iconLoader: IconLoaderService,
              private controlsService: CanvasControlsService) {
  }

  async ngAfterViewInit(): Promise<void> {
    this.initializeBaseCanvas();
    //this.setupMovementConstraints();
    await this.controlsService.initializeControls();
    this.loadTables();
  }

  private initializeBaseCanvas(): void {
    this.canvas = new fabric.Canvas(this.canvasRef.nativeElement, {
      width: 800,
      height: 800,
      selection: true,         // Allow selection of objects
      renderOnAddRemove: true, // Redraw the canvas when objects are added or removed
      interactive: true,       // Enable interaction with the canvas
    });
  }


  private setupMovementConstraints(): void {
    this.canvas.on('object:moving', (e) => {
      const obj = e.target;
      if (!obj || !this.canvas) {
        return;
      }

      obj.setCoords();
      const canvasWidth = this.canvas.getWidth();
      const canvasHeight = this.canvas.getHeight();
      const objWidth = obj.getScaledWidth();
      const objHeight = obj.getScaledHeight();

      obj.set({
        left: Math.max(0, Math.min(obj.left ?? 0, canvasWidth - objWidth)),
        top: Math.max(0, Math.min(obj.top ?? 0, canvasHeight - objHeight))
      });
    });

    console.log('Movement constraints enabled');
  }

  private loadTables(): void {
    this.tableService.getAllTables().subscribe({
      next: (tables) => {
        this.tables = tables;
        this.renderTables(tables);
      },
      error: (err) => console.error('Table load failed:', err)
    });
  }

  private renderTables(tables: Table[]): void {
    const tableCount = this.tables.length;
    for (let i = 0; i < tableCount; i++) {
      const table = this.tables[i];
      this.createTable(table, i);
    }
    this.canvas.renderAll();
    console.log('Tables rendered:', this.tables.length);
  }

  private createTable(currTable: Table, index: number): void {
    const tableNumber = currTable.tableNum;
    const seatCount = currTable.seatCount;
    const tableText = tableNumber.toString() + '\n' + seatCount;
    const rect = new fabric.Rect({
      fill: '#eccbaf',
      width: 40,
      height: 60,
      stroke: 'black',
      strokeWidth: 3,
      cornerSize: 10,
      originX: 'center',
      originY: 'center',
    });

    const text = new fabric.FabricText(tableText, {
      fontFamily: 'Arial',
      fontSize: 15,
      textAlign: 'center',
      originX: 'center',
      originY: 'center',
      angle: 270
    });

    const head = new fabric.Path(ICON_PATHS.USER_HEAD);
    const body = new fabric.Path(ICON_PATHS.USER_BODY);
    const iconGroup = new fabric.Group([head, body], {
      angle: 270,
      scaleX: .03,
      scaleY: .03,
      left: 1,
      top: 21
    });
    const table = new fabric.Group([rect, text, iconGroup], {
      lockScalingX: false,
      lockScalingY: false,
      left: 100,
      top: 250 + (index * 70),
      snapAngle: 15,
      selectable: true,
      evented: true,
    }) as CanvasTable;
    table.tableId = tableNumber;

    this.canvas.add(table);
    this.canvas.renderAll();

    console.log('Table created:', table);
  }

  protected onUpdateTables(tables: Table[]) {
    this.tables = tables;
    this.renderTables(tables);
  }

}
