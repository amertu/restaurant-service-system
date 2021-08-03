import { Injectable } from '@angular/core';
import { formatDate } from '@angular/common';
import { Reservation } from '../dtos/reservation';

@Injectable({
  providedIn: 'root'
})
export class TimeUtilsService {

  constructor() { }

  public getCurrentLocalTimeAsIsoString(): string {
    return formatDate(Date.now(), 'yyy-MM-ddTHH:mm', 'en-US', '+0200'  );
  }


  public getDateTimeStringForInvoiceId(dateTime: Date): string {
        
    
    // ASSUMPTION:
    // It is assumed that the backend uses a numerical ID of at least 64-bit. (e.g.: Java datatype Long)
    // This would mean that the maximum value is "2^(63)-1", which would be represented as 19-digits (decimal).
    // There are 17 digits for a DateTime from year to milliseconds (inclusive) when using zero-padding, 
    // therefore such an ID is suitable for the assumed backend.
    return '' +
      dateTime.getFullYear()  + '' +
      // JavaScript Month representation is 0-based: 
      // "The getMonth() method returns the month (from 0 to 11) for the specified date, according to local time."
      // See: https://www.w3schools.com/jsref/jsref_getmonth.asp
      // Therefore "+1" is added to get a date representation we are used to read.
      (dateTime.getMonth() + 1).toString().padStart(2, '0')  + '' +
      dateTime.getDay().toString().padStart(2, '0')  + '' +
      dateTime.getHours().toString().padStart(2, '0') + '' +
      dateTime.getMinutes().toString().padStart(2, '0') + '' +
      dateTime.getSeconds().toString().padStart(2, '0') + '' +
      dateTime.getMilliseconds().toString().padStart(3, '0');
  }


  public getMinutesRoundedUpToNextQuarterOfHour(currentHour: number, currentMinute: number): number {

    if (currentHour === 23 && currentMinute >= 45) {
      return currentMinute + 1; // +1  to avoid problem with running over to next minute
    }

    console.log('currentMinute: ' + currentMinute);
    const numberOfPastFullQuarters = Math.floor(currentMinute / 15);
    console.log('numberOfPastFullQuarters: ' + numberOfPastFullQuarters);
    const minutesRoundedUpToNextQuarterOfHour =  ((numberOfPastFullQuarters + 1) * 15) % 60;
    console.log('minutesRoundedUpToNextQuarterOfHour: ' + minutesRoundedUpToNextQuarterOfHour);

    return minutesRoundedUpToNextQuarterOfHour;

  }

  public getStartTime(startHour: number, minutesRoundedUpToNextQuarterOfHour: number): string {

    return this.getTimeString(startHour, minutesRoundedUpToNextQuarterOfHour);

  }

  public getEndTime(startHour: number, minutesRoundedUpToNextQuarterOfHour: number, duration: number): string {
      const endHour = startHour + duration;
      if ( endHour > 23) {
        return '23:59';
      } else {
        return this.getTimeString(endHour, minutesRoundedUpToNextQuarterOfHour);
      }
  }

  public getStartHour(currentHour: number, minutesRoundedUpToNextQuarterOfHour: number): number {
    if (currentHour === 23) {
      return currentHour;
    } else {
        if (minutesRoundedUpToNextQuarterOfHour === 0) {
          // if next quarter is :00, this means an overflow of hour
          const startHour = currentHour + 1;
          return startHour;
        } else {
          return currentHour;
        }
    }
  }

  public getTimeString(hour: number, minute: number): string {
    return hour.toString().padStart(2, '0') + ':' + minute.toString().padStart(2, '0');
  }

  public getIsoDateTimeAsString(dateAsString, timeAsString): string {
    return dateAsString + 'T' + timeAsString;
  }

  public reservationStartedInThePast(reservation: Reservation): boolean {
    const currentTime = Date.parse(this.getCurrentLocalTimeAsIsoString());
    const startTimeOfReservation =  Date.parse(reservation.startDateTime);
    return startTimeOfReservation <= currentTime;
  }

  public reservationStartsInTheFuture(reservation: Reservation): boolean {
    const currentTime = Date.parse(this.getCurrentLocalTimeAsIsoString());
    const startTimeOfReservation =  Date.parse(reservation.startDateTime);
    // As we don't allow the endTime to equal the startTime in the future, '>=' is used intentionally
    return startTimeOfReservation >= currentTime;
  }

  reservationFinishedInThePast(reservation: Reservation): boolean {
    const currentTime = Date.parse(this.getCurrentLocalTimeAsIsoString());
    const endTimeOfReservation =  Date.parse(reservation.endDateTime);
    // As we don't allow the endTime to equal the startTime in the future, '>=' is used intentionally
    return endTimeOfReservation <= currentTime;
  }

  /**
   * Tells if timeOne <= timeTwo
   * @param timeOne time like 15:30
   * @param timeTwo time like 16:30
   */
  public timeOneIsBeforeOrEqualToTimeTwo(timeOne: string, timeTwo: string ){
    const time1 = this.getTimeInMinutes(timeOne);
    const time2 = this.getTimeInMinutes(timeTwo);
    return time1 <= time2;
  }


  /**
   * Returns duration in minutes
   * @param startTime start time
   * @param endTime end time
   */
  public getDurationInMinutes(startTime: string, endTime: string): number {
      return this.getTimeInMinutes(endTime) - this.getTimeInMinutes(startTime);
  }

  /**
   * Returns time in number of minutes
   * @param timeAsString e.g.: 15:30
   */
  public getTimeInMinutes(timeAsString: string) {

    const numberOfHours = this.getNumberOfHours(timeAsString);
    const numberOfMinutes = this.getNumberOfMinutes(timeAsString);

    return (60 * numberOfHours) + numberOfMinutes;

  }

  public getNumberOfHours(timeAsString: string): number {
    const locationOfHourToMinuteSeparator = timeAsString.indexOf(':');
    return Number(timeAsString.substring(0, locationOfHourToMinuteSeparator));
  }

  public getNumberOfMinutes(timeAsString: string): number {
    const locationOfHourToMinuteSeparator = timeAsString.indexOf(':');
    return Number(timeAsString.substring(locationOfHourToMinuteSeparator + 1));
  }


  /**
   * Returns ISO formatted time e.g.: 16:30
   * @param startTime e.g.: 15:30
   * @param offsetInMinutes e.g.: 60
   */
  public getEndTimeByStartTimeAndOffsetInMinutes(startTime: string, offsetInMinutes: number): string {
      const FULL_DAY_IN_MINUTES = 24 * 60;
      const startTimeInMinutes = this.getTimeInMinutes(startTime);
      const endTime = startTimeInMinutes + offsetInMinutes;

      if (endTime > FULL_DAY_IN_MINUTES){
        return '23:59';
      } else {
        const endHour = Math.floor(endTime / 60);
        const endMinute = endTime % 60;
        return this.getTimeString(endHour, endMinute);
      }
  }


}
