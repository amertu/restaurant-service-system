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
  private selectedDishes: number[] = [];
  private selectedDishesObjects: Dish[] = [];
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

  /**
   * select a dish
   * @param selectedDish the selected
   */
  selectDishes(selectedDish: Dish) {
    this.selectedDishes.push(selectedDish.id);
    this.selectedDishesObjects.push(selectedDish);
    this.selectedNumber[this.dishes.indexOf(selectedDish)] += 1;
  }

  /**
   * unselect a dish
   * @param unselectedDish the unselected
   */
  unselectDishes(unselectedDish: Dish) {
    const index = this.selectedDishes.indexOf(unselectedDish.id);
    if (index > -1) {
      this.selectedDishes.splice(index, 1);
      this.selectedDishesObjects.splice(index, 1);
      this.selectedNumber[this.dishes.indexOf(unselectedDish)] -= 1;
    }
  }

  formatPrice(price: number) {
    return '' + Math.floor(price / 100) + ',' + (price % 100 + ' €').padStart(4, '0');
  }

  /**
   * complete checkout all dishes
   */
  checkout() {
    const dateTime = new Date();
    const invoiceId: string = this.timeUtilService.getDateTimeStringForInvoiceId(dateTime);
    console.log(invoiceId);
    if (this.selectedDishesObjects.length !== 0) {
      this.buyDishes(invoiceId);
    }
  }

  /**
   * buy dishes
   * @param invoiceId of the bought dishes
   */
  buyDishes(invoiceId: String) {
    const bill: Bill = new Bill(undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, null);
    bill.invoiceId = Number(invoiceId);
    bill.dishes = this.selectedDishesObjects;

    if (this.reservation) {
      bill.reservationStartedAt = formatDate(Date.parse(this.reservation.startDateTime), 'medium', 'en-US', '+0200');
      bill.servedTables = this.reservedTablesAsString;
    }

    this.billService.buyDishes(bill).subscribe({
      next: () => {
        this.errorMessage = 'Checkout completed successfully';
        this.errorSuccessful = true;
        this.selectedDishesObjects = [];
        this.loadAllDishes();
        this.activeModal.close();
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  /**
   * error handling
   * @param error the error message
   */
  private defaultServiceErrorHandling(error: any) {
    this.error = true;
    if (error.status === 0) {
      this.errorMessage = 'The backend seems not to be reachable';
    } else if (error.error.message === 'No message available' || error.error.message === '' || error.error.message === null) {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error.message;
    }
  }
}
