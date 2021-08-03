export class Dish {
  constructor(
    public id: number,
    public name: string,
    public price: number,
    public category?: string
  ) {
  }
}
