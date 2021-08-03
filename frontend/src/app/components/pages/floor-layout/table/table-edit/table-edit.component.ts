import {Component, Input, OnInit, OnChanges} from '@angular/core';
import {Validators, FormBuilder} from '@angular/forms';
import {TableService} from '../../../../../services/table.service';
import {AlertService} from '../../../../../services/alert.service';
import {Table} from 'src/app/dtos/table';
import { ActivatedRoute } from '@angular/router';
import {AuthService} from '../../../../../services/auth.service';
import {FloorLayout} from '../../../../../dtos/floor-layout';
import {FloorLayoutService} from '../../../../../services/floor-layout.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-table-edit',
  templateUrl: './table-edit.component.html',
  styleUrls: ['./table-edit.component.scss']
})
export class TableEditComponent implements OnInit, OnChanges {
  @Input() table: Table;
  submitted: boolean = false;
  tables: Table[];

  constructor(private tableService: TableService,
              private alertService: AlertService, private route: ActivatedRoute, private formBuilder: FormBuilder,
              private layoutService: FloorLayoutService, public activeModal: NgbActiveModal) { }

  tableForm = this.formBuilder.group({
    id: null,
    tableNum: [Validators.required, Validators.min(1)],
    seatCount: [Validators.required, Validators.min(1)],
    posDescription: [''],
    active: [true, Validators.required]
  });

  ngOnInit(): void {
    this.initTableFormGroup();
    this.loadAllTables();
  }

  ngOnChanges(): void {
    this.initTableFormGroup();
    this.loadAllTables();
  }

  public loadAllTables() {
    console.log('loadAllTables()');
    this.tableService.getAllTables().subscribe(
      (tables: Table[]) => {
        this.tables = tables;
      },
      error => {
        console.log('Failed to load tables.');
        this.alertService.error(error);
      }
    );
  }

  tableNumOverlapping(): boolean {
    for (let i = 0; i < this.tables.length; i++) {
      if (this.table.id !== this.tables[i].id && this.tableForm.controls.tableNum.value === this.tables[i].tableNum) {
        return true;
      }
    }
    return false;
  }

  onSubmitUpdate() {
    this.submitted = true;
    console.log(this.tableForm.value);
    if (this.tableForm.valid && !this.tableNumOverlapping()) {
      this.tableService.updateTable(this.tableForm.value).subscribe(
        () => {
            this.activeModal.close();
        },
        error => {
          this.alertService.error(error);
        });
    };
  }

  private initTableFormGroup() {
    this.tableForm = this.formBuilder.group({
      id: [this.table.id],
      tableNum: [this.table.tableNum, [Validators.required, Validators.min(1)]],
      seatCount: [this.table.seatCount, [Validators.required, Validators.min(1)]],
      posDescription: [this.table.posDescription],
      active: [this.table.active, Validators.required]
    });
  }
}
