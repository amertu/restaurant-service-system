import {Component, Input, OnInit} from '@angular/core';
import {Validators, FormBuilder} from '@angular/forms';
import {TableService} from '../../../../../services/table.service';
import {AlertService} from '../../../../../services/alert.service';
import {Table} from '../../../../../dtos/table';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-table-add',
  templateUrl: './table-add.component.html',
  styleUrls: ['./table-add.component.scss']
})
export class TableAddComponent implements OnInit {
  submitted: boolean = false;
  tables: Table[];

  constructor(private formBuilder: FormBuilder,
              private tableService: TableService,
              private alertService: AlertService,
              private activeModal: NgbActiveModal) {
  }

  tableForm = this.formBuilder.group({
    id: null,
    tableNum: ['', [Validators.required, Validators.min(1)]],
    seatCount: ['', [Validators.required, Validators.min(1)]],
    posDescription: [''],
    active: [true, Validators.required]
  });

  ngOnInit(): void {
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
      if (this.tableForm.controls.tableNum.value === this.tables[i].tableNum) {
        return true;
      }
    }
    return false;
  }

  onSubmitAdd() {
    this.submitted = true;
    if (this.tableForm.valid && !this.tableNumOverlapping()) {
      const tableAdd: Table = new Table(null,
        this.tableForm.controls.tableNum.value,
        this.tableForm.controls.seatCount.value,
        this.tableForm.controls.posDescription.value,
        this.tableForm.controls.active.value,
        null);
      this.save(tableAdd);
    }
  }

  save(table: Table) {
    this.tableService.createTable(table).subscribe(
      () => {
        this.activeModal.close();
      },
      error => {
        this.alertService.error(error);
      }
    );
  }
}
