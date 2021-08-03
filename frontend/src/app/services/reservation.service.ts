import { Injectable } from '@angular/core';

import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Reservation } from '../dtos/reservation';
import {HttpClient, HttpParams} from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class ReservationService {


  private reservationBaseUri: string = this.globals.backendUri + '/reservations';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads all reservations from the backend
   */
  public getAllReservations(): Observable<Reservation[]> {
    console.log('Get all reservations');
    return this.httpClient.get<Reservation[]>(this.reservationBaseUri + '/');
  }

  /**
   * Loads all reservations within the given time range from the backend
   * @param startDateTime start of the time range
   * @param endDateTime end of the time range
   */
  public getReservationsByStartAndEndDateTime(startDateTime: string, endDateTime: string): Observable<Reservation[]> {
    console.log('Get all reservations within the given time range from the backend');
    return this.httpClient.get<Reservation[]>(this.reservationBaseUri + '?startDateTime=' + startDateTime + '&endDateTime=' + endDateTime);
  }

    /**
   * Loads all reservations, except the reservation with the specified ID, within the given time range from the backend
   * @param startDateTime start of the time range
   * @param endDateTime end of the time range
   * @param idOfReservationToBeIgnored ID of the reservation to be ignored.
   */
  public findByStartAndEndDateTimeIgnoreReservationWithId(startDateTime: string, endDateTime: string, idOfReservationToBeIgnored: number): Observable<Reservation[]> {
    console.log('Get all reservations, except the reservation with the specified ID, within the given time range from the backend');
    return this.httpClient.get<Reservation[]>(this.reservationBaseUri + '?startDateTime=' + startDateTime + '&endDateTime=' + endDateTime + '&idOfReservationToBeIgnored=' + idOfReservationToBeIgnored );
  }

  /**
   * Creates a new reservation
   * @param reservation reservation to create
   */
  public createReservation(reservation: Reservation): Observable<Reservation> {
    console.log('ReservationService: Creating reservation.');
    return this.httpClient.post<Reservation>(this.reservationBaseUri + '/', reservation);
  }

  /**
   * Deletes the given reservation.
   * @param reservation the reservation to be deleted
   */

  public deleteReservation(reservation: Reservation): Observable<void> {
    console.log('ReservationService: Deleting reservation with id: ' + reservation.id);
    return this.httpClient.delete<void>(this.reservationBaseUri + '/' + reservation.id);
  }

/**
 * Loads the reservation with the specified id
 * @param id ID of the reservation to load
 */
  public getReservationWithId(id: number): Observable<Reservation> {
    console.log('getReservationWithId:' + id);
    return this.httpClient.get<Reservation>(this.reservationBaseUri + '/' + id);
  }


  /**
   * Updates the given reservation
   * @param reservation reservation with updated data
   */
  public updateReservation(reservation: Reservation): Observable<Reservation>  {
    console.log('updateReservation:' + JSON.stringify(reservation));
    return this.httpClient.put<Reservation>(this.reservationBaseUri, reservation);
  }

  /**
   * Filters saved reservations by the given parameters.
   * @param guestName     the name of the guest that made the reservation(s) to find.
   * @param startDateTime the earliest Date and Time of the reservation(s) to find. (exclusive)
   * @param endDateTime   the latest Date and Time of the reservations(s) to find. (exclusive)
   * @param tableNum      the table number of a table shall be part of the reservation(s) to find.
   */
  public filterReservations(guestName: string, startDateTime: string, endDateTime: string, tableNum: string): Observable<Reservation[]> {
    console.log('searching');
    guestName = !guestName ? '' : guestName;
    startDateTime = !startDateTime ? '' : startDateTime;
    endDateTime = !endDateTime ? '' : endDateTime;
    tableNum = !tableNum ? '' : tableNum;
    const httpParams = new HttpParams()
      .set('guestName', guestName)
      .set('startDateTime', startDateTime)
      .set('endDateTime', endDateTime)
      .set('tableNum', tableNum);
    const options = {params: httpParams};
    return this.httpClient.get<Reservation[]>(this.reservationBaseUri + '/filter', options);
  }

}



