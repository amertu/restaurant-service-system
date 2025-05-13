import {Injectable} from '@angular/core';

import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Table} from '../dtos/table';
import {HttpClient} from '@angular/common/http';
import {CenterCoordinates} from '../dtos/center-coordinates';


@Injectable({
  providedIn: 'root'
})
export class TableService {


  private tableBaseUri: string;

  constructor(private httpClient: HttpClient, private globals: Globals) {
    this.tableBaseUri = this.globals.backendUri + '/tables';
  }

  /**
   * Loads the table with the id from the backend
   * @param id of table to load
   */
  public getTableWithId(id: number): Observable<Table> {
    console.log('Get all tables');
    return this.httpClient.get<Table>(this.tableBaseUri + '/' + id);
  }

  /**
   * Loads all tables from the backend
   */
  public getAllTables(): Observable<Table[]> {
    console.log('Get all tables');
    return this.httpClient.get<Table[]>(this.tableBaseUri + '/');
  }

  /**
   * Creates a new table
   * @param table table to create
   */
  public createTable(table: Table): Observable<Table> {
    console.log('TableService: Creating table.');
    return this.httpClient.post<Table>(this.tableBaseUri + '/', table);
  }

  /**
   * Clones a table which is identified by its id.
   * All properties are simply copied, with two exceptions:
   * 1. database-generated id
   * 2. tableNum (which is simply next available tableNum which is greater than tableNum of supplied table)
   *
   * @param id the id of the table to clone
   * @return a table clone (all properties same as those of table to be cloned - exceptions: id and tableNum)
   */
  public cloneTable(id: number): Observable<Table> {
    console.log('TableService: Creating table.');
    return this.httpClient.post<Table>(this.tableBaseUri + '/clone/' + id, null);
  }

  /**
   * Updates an existing table
   * @param table table to update
   */
  public updateTable(table: Table): Observable<Table> {
    console.log('TableService: Updating table.');
    return this.httpClient.put<Table>(this.tableBaseUri, table);
  }

  /**
   * Deletes a new table
   * @param id id of the table to delete
   */
  public deleteTable(id: number): Observable<any> {
    console.log('TableService: Creating table.');
    return this.httpClient.delete(this.tableBaseUri + '/' + id);
  }

  /**
   * Sets a given table as (in)active
   * @param id id of the table to update
   * @param active boolean indicating whether table is active
   */
  public setTableActive(id: number, active: boolean): Observable<any> {
    console.log(`TableService: Make table with ID ${id} ${active ? 'active' : 'inactive'}`);
    let table = new Table(id, null, null, null, active, null);
    return this.httpClient.patch(this.tableBaseUri, table);
  }

  /**
   * Sets a given tables' coordinates
   * @param id id of the table to update
   * @param centerCoordinates (updated) center coordinates of the table
   */
  public setTableCoordinates(id: number, centerCoordinates: CenterCoordinates): Observable<any> {
    console.log(`TableService: Update table coordinates to (${centerCoordinates.x}, ${centerCoordinates.y})`);
    let table = new Table(id, null, null, null, null, centerCoordinates);
    return this.httpClient.patch(this.tableBaseUri + '/coordinates', table);
  }

  /**
   * Returns a suggestion of table considering the given number of guests.
   * @param numberOfGuests number of guests.
   * @param idOfReservationToIgnore the id of the a reservation to ignore is used in case of the update of an existing reservation, to consider the reserved tables as free.
   * @param startDateTime the start time of the reservation.
   * @param endDateTime the end time of the reservation.
   */
  public getTableSuggestion(numberOfGuests: number, idOfReservationToIgnore: string, startDateTime: string, endDateTime: string): Observable<Table[]> {
    console.log('Get table suggestion.');
    return this.httpClient.get<Table[]>(this.tableBaseUri + '?numberOfGuests=' + numberOfGuests
      + '&idOfReservationToIgnore=' + idOfReservationToIgnore
      + '&startDateTime=' + startDateTime + '&endDateTime=' + endDateTime);
  }

}
