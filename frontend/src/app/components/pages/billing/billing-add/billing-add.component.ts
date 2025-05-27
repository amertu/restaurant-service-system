import {Component, OnInit, Input} from '@angular/core';
import {AlertService} from '../../../../services/alert.service';
import {Dish} from '../../../../dtos/dish';
import {BillService} from '../../../../services/bill.service';
import {DishService} from '../../../../services/dish.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Reservation} from 'src/app/dtos/reservation';
import {DatePipe, formatDate, NgForOf, NgIf} from '@angular/common';
import {Bill} from 'src/app/dtos/bill';
import {TimeUtilsService} from 'src/app/services/time-utils.service';
import {finalize, take} from 'rxjs';

@Component({
  selector: 'app-billing-add',
  templateUrl: './billing-add.component.html',
  standalone: true,
  imports: [
    DatePipe,
    NgIf,
    NgForOf
  ],
  styleUrls: ['./billing-add.component.scss']
})
export class BillingAddComponent implements OnInit {

  @Input() reservation: Reservation;
  reservedTablesAsString: string;

  dishes: Dish[] = [];
  selectedDishCounts: { [dishId: number]: number } = {};
  protected selectedNumber: number[] = [];

  error: boolean = false;
  errorMessage: string = '';
  errorSuccessful: boolean = false;

  constructor(private alertService: AlertService, private dishService: DishService, private billService: BillService, public activeModal: NgbActiveModal, private timeUtilService: TimeUtilsService) {
  }

  ngOnInit(): void {
    this.loadAllDishes();

    if (this.reservation) {
      const tableNumbers: string[] = [];
      for (const table of this.reservation.restaurantTables) {
        tableNumbers.push(table.tableNum.toString());
      }
      this.reservedTablesAsString = tableNumbers.join(', ');
    }
  }

  /**
   * Load available dishes
   */
  public loadAllDishes(): void {
    console.log('Loading all dishes...');

    this.dishService.getAllDishes().pipe(
      take(1),
      finalize(() => {
        console.log('Finished attempting to load dishes.');
      })
    ).subscribe({
      next: (dishes: Dish[]) => {
        this.dishes = dishes;
        this.selectedNumber = new Array(dishes.length).fill(0);
      },
      error: (err) => {
        console.error('Failed to load dishes:', err);
        this.alertService.error('Unable to load dishes. Please try again later.');
      }
    });
  }

  formatPrice(price: number): string {
    const euros = Math.floor(price / 100);
    const cents = (price % 100).toString().padStart(2, '0');
    return `${euros},${cents} €`;
  }

  /**
   * complete checkout all dishes
   */
  checkout() {
    const selectedDishesObjects = this.dishes.filter(d => this.selectedDishCounts[d.id]);
    if (selectedDishesObjects.length !== 0) {
      this.buyDishes(selectedDishesObjects);
    }
  }

  buyDishes(dishes: Dish[]) {
    const dateTime = new Date();
    const invoiceId: string = this.timeUtilService.getDateTimeStringForInvoiceId(dateTime);
    console.log(invoiceId);
    let bill: Bill = new Bill(undefined, Number(invoiceId), undefined, undefined, dateTime, undefined, dishes, undefined, null);
    if (this.reservation) {
      bill.reservationStartedAt = formatDate(Date.parse(this.reservation.startDateTime), 'medium', 'en-US', '+0200');
      bill.servedTables = this.reservedTablesAsString;
    }
    this.billService.buyDishes(bill).subscribe({
      next: () => {
        this.errorMessage = 'Checkout completed successfully';
        this.errorSuccessful = true;
        this.loadAllDishes();
        this.activeModal.close();
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }


  private defaultServiceErrorHandling(error: any): void {
    this.error = true;
    if (error.status === 0) {
      this.errorMessage = 'Backend not reachable';
    } else if (!error.error?.message) {
      this.errorMessage = error.error?.error || 'Unknown error occurred';
    } else {
      this.errorMessage = error.error.message;
    }
  }


   selectDish(dish: Dish) {
    const index = this.dishes.findIndex(d => d.id === dish.id);
    this.selectedNumber[index] += 1;

    if (!this.selectedDishCounts[dish.id]) {
      this.selectedDishCounts[dish.id] = 1;
    } else {
      this.selectedDishCounts[dish.id]++;
    }
  }

   unselectDish(dish: Dish) {
    const index = this.dishes.findIndex(d => d.id === dish.id);
    if (this.selectedNumber[index] > 0) {
      this.selectedNumber[index] -= 1;
      this.selectedDishCounts[dish.id]--;

      if (this.selectedDishCounts[dish.id] <= 0) {
        delete this.selectedDishCounts[dish.id];
      }
    }
  }

}
