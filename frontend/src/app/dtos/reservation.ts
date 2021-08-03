import { Table } from './table';

export class Reservation {

  constructor(
    public id: number,
    public guestName: string,
    public numberOfGuests: number,
    public contactInformation: string,
    public comment: string,
    public startDateTime: string,
    public endDateTime: string,
    public restaurantTables: Table[]
  ) {
  }
}
