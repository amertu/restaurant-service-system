import {Component, Input, OnInit} from '@angular/core';
import {Table} from '../../../../../dtos/table';
import {Reservation} from '../../../../../dtos/reservation';
import {TableService} from '../../../../../services/table.service';
import {AlertService} from '../../../../../services/alert.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-table-deactivate',
  templateUrl: './table-deactivate.component.html',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./table-deactivate.component.scss']
})
export class TableDeactivateComponent implements OnInit {
  @Input() table: Table;
  @Input() reservations: Reservation[];

  constructor(public tableService: TableService, public alertService: AlertService, protected activeModal: NgbActiveModal) {
  }

  ngOnInit() {
  }

  public deactivateTable(id: number) {
    this.tableService.setTableActive(id, false).subscribe({
      next: () => {
        this.activeModal.close();
      },
      error: (error) => {
        this.alertService.error(error);
      }
    });
  };

  public formatDate(date: string): string {
    return date.replace('T', ' ').substring(0, date.length - 3);
  }
}
