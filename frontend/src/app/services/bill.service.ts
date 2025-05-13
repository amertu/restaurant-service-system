import { Injectable } from '@angular/core';
import {Dish} from '../dtos/dish';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Bill} from '../dtos/bill';

@Injectable({
  providedIn: 'root'
})
export class BillService {

  private readonly billsBaseUri: string;

  constructor(private httpClient: HttpClient, private globals: Globals) {
    this.billsBaseUri = this.globals.backendUri + '/bills';
  }

  /**
   * A method to buy dishes
   * @param dishes to be bought
   * @param invoiceId id of the invoice
   */
  buyDishes(bill: Bill): Observable<Bill> {
    return this.httpClient.post<Bill>(this.billsBaseUri + '/' + bill.invoiceId, bill);
  }

  /**
   * Loads all bills from the backend
   */
  getBills(): Observable<Bill[]> {
    console.log('Get all bills');
    return this.httpClient.get<Bill[]>(this.billsBaseUri);
  }

  /**
   * Load invoice as pdf
   * @param invoiceId id of the invoice
   */
  getInvoicePdf(invoiceId: number): Observable<Bill> {
    return this.httpClient.get<Bill>(this.billsBaseUri + '/' + invoiceId);
  }
}
