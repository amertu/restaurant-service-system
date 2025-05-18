import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Dish} from '../dtos/dish';

@Injectable({
  providedIn: 'root'
})
export class DishService {

  private readonly dishBaseUri: string;

  constructor(private httpClient: HttpClient, private globals: Globals) {
    this.dishBaseUri = this.globals.backendUri + '/dishes';
  }

  /**
   * Loads all dishes from the backend
   */
  public getAllDishes(): Observable<Dish[]> {
    console.log('Get all dishes');
    return this.httpClient.get<Dish[]>(this.dishBaseUri + '/');
  }

  /**
   * Creates a new dish
   * @param dish dish to create
   */
  public createDish(dish: Dish): Observable<Dish> {
    console.log('DishService: Creating dish.');
    return this.httpClient.post<Dish>(this.dishBaseUri + '/', dish);
  }

  /**
   * Updates an existing  dish
   * @param dish dish to update
   */
  public updateDish(dish: Dish): Observable<Dish> {
    console.log('DishService: Updating dish.');
    return this.httpClient.put<Dish>(this.dishBaseUri, dish);
  }

  /**
   * Deletes an existing dish
   * @param id id of the dish to delete
   */
  public deleteDish(id: number): Observable<any> {
    console.log('DishService: Deleting dish.');
    return this.httpClient.delete(this.dishBaseUri + '/' + id);
  }

}
