import { CenterCoordinates } from './center-coordinates';

export class Table {
    constructor(
      public id: number,
      public tableNum: number,
      public seatCount: number,
      public posDescription: string,
      public active: boolean,
      public centerCoordinates: CenterCoordinates
    ) {
    }
  }
