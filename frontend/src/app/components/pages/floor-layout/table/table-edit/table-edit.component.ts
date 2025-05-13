import {Component, Input, OnInit, OnChanges} from '@angular/core';
import {Validators, FormBuilder, ReactiveFormsModule, FormGroup, FormControl} from '@angular/forms';
import {TableService} from '../../../../../services/table.service';
import {AlertService} from '../../../../../services/alert.service';
import {Table} from 'src/app/dtos/table';
import {ActivatedRoute} from '@angular/router';
import {FloorLayoutService} from '../../../../../services/floor-layout.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-table-edit',
  templateUrl: './table-edit.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./table-edit.component.scss']
})
export class TableEditComponent implements OnInit, OnChanges {
  @Input() table: Table;
  submitted: boolean = false;
  tables: Table[];
  protected tableForm!: FormGroup;

  constructor(private tableService: TableService,
              private alertService: AlertService, private route: ActivatedRoute, private formBuilder: FormBuilder,
              private layoutService: FloorLayoutService, public activeModal: NgbActiveModal) {
  }


  ngOnInit(): void {
    this.initTableFormGroup();
    this.loadAllTables();
  }

  ngOnChanges(): void {
    this.initTableFormGroup();
    this.loadAllTables();
  }

  private initTableFormGroup() {
    this.tableForm = this.formBuilder.group<{
      id: FormControl<number | null>;
      tableNum: FormControl<number>;
      seatCount: FormControl<number>;
      posDescription: FormControl<string>;
      active: FormControl<boolean>;
    }>({
      id: this.formBuilder.control<number | null>(null),
      tableNum: this.formBuilder.control<number>(1, [Validators.required, Validators.min(1)]),
      seatCount: this.formBuilder.control<number>(1, [Validators.required, Validators.min(1)]),
      posDescription: this.formBuilder.control<string>('', []),
      active: this.formBuilder.control<boolean>(true, Validators.required),
    });

    this.tableForm.setValue({
      id: this.table.id,
      tableNum: this.table.tableNum,
      seatCount: this.table.seatCount,
      posDescription: this.table.posDescription || '',
      active: this.table.active
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
      if (this.table.id !== this.tables[i].id && this.tableForm.controls.tableNum.value === this.tables[i].tableNum) {
        return true;
      }
    }
    return false;
  }

  onSubmitUpdate(): void {
    this.submitted = true;
    if (this.tableForm.invalid || this.tableNumOverlapping()) {
      return;
    }
    const updatedTable: Table = {
      ...this.table,
      ...this.tableForm.value
    };

    this.tableService.updateTable(updatedTable).subscribe({
      next: (response) => {
        console.log('Table updated successfully:', response);
        this.alertService.reportSuccessModal('Successfully updated table.');
        this.activeModal.close();
      },
      error: (error) => {
        console.error('Error updating table:', error);
        this.alertService.reportErrorModal(error);
      }
    });
  }
}
