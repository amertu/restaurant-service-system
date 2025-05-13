import {Component, Input, OnInit} from '@angular/core';
import {Validators, FormBuilder, ReactiveFormsModule, FormGroup, FormControl} from '@angular/forms';
import {TableService} from '../../../../../services/table.service';
import {AlertService} from '../../../../../services/alert.service';
import {Table} from '../../../../../dtos/table';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-table-add',
  templateUrl: './table-add.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./table-add.component.scss']
})
export class TableAddComponent implements OnInit {
  submitted: boolean = false;
  tables: Table[];
  protected tableForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private tableService: TableService,
              private alertService: AlertService,
              protected activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
    this.initTableFormGroup();
    this.loadAllTables();
  }

  private initTableFormGroup(): void {
    this.tableForm = this.formBuilder.group<{
      id: FormControl<number | null>;
      tableNum: FormControl<number | null>;
      seatCount: FormControl<number | null>;
      posDescription: FormControl<string | null>;
      active: FormControl<boolean>;
    }>({
      id: this.formBuilder.control<number | null>(null),
      tableNum: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(1)]),
      seatCount: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(1)]),
      posDescription: this.formBuilder.control<string | null>(''),
      active: this.formBuilder.control<boolean>(true, Validators.required)
    });
  }


  public loadAllTables() {
    console.log('loadAllTables()');
    this.tableService.getAllTables().subscribe({
      next: (tables: Table[]) => {
        this.tables = tables;
      },
      error: (error) => {
        console.log('Failed to load tables.');
        this.alertService.error(error);
      }
    });
  }

  tableNumOverlapping(): boolean {
    for (let i = 0; i < this.tables.length; i++) {
      // Ensure both values are compared as numbers
      if (+this.tableForm.controls.tableNum.value === +this.tables[i].tableNum) {
        return true;
      }
    }
    return false;
  }


  onSubmitAdd() {
    this.submitted = true;
    if (this.tableForm.valid && !this.tableNumOverlapping()) {
      const tableAdd: Table = {
        id: null,  // For new table, the ID will be null
        tableNum: +this.tableForm.controls.tableNum.value,  // Ensure it's treated as a number
        seatCount: +this.tableForm.controls.seatCount.value,  // Make sure seatCount is also treated as a number
        posDescription: this.tableForm.controls.posDescription.value,
        active: this.tableForm.controls.active.value,
        centerCoordinates: null // Assuming centerCoordinates is null for new tables, adjust as necessary
      };
      this.save(tableAdd);
    }
  }


  save(table: Table) {
    this.tableService.createTable(table).subscribe({
      next: () => {
        this.activeModal.close();
      },
      error: (error) => {
        this.alertService.error(error);
      }
    });
  }
}
