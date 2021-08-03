import {ApplicationUser} from './application-user';
import { Dish } from './dish';

export class Bill {
  constructor(
    public id: number,
    public invoiceId: number,
    public pdf: string,
    public totalCost: number,
    public paidAt: Date,
    public user: ApplicationUser,
    public dishes: Dish[],
    public reservationStartedAt: string,
    public servedTables: string
  ) {
  }
}
