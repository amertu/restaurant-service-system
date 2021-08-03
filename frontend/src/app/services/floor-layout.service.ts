import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {FloorLayout} from '../dtos/floor-layout';

@Injectable({
  providedIn: 'root'
})
export class FloorLayoutService {
  private layoutBaseUri: string = this.globals.backendUri + '/layout';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads the layout with the id from the backend
   * @param id of the layout to load
   */
  public getLayoutWithId(id: number): Observable<FloorLayout> {
    console.log("Get layout with id " + id);
    return this.httpClient.get<FloorLayout>(this.layoutBaseUri + '/' + id);
  }

  /**
   * Creates a new layout
   * @param layout to be created
   */
  public createLayout(layout: FloorLayout): Observable<FloorLayout> {
    console.log('FloorLayoutService: Creating layout.');
    return this.httpClient.post<FloorLayout>(this.layoutBaseUri, layout);
  }

  /**
   * Updates an existing layout
   * @param layout to be updated
   */
  public updateLayout (layout: FloorLayout): Observable<FloorLayout> {
    console.log('FloorLayoutService: Updating layout.');
    return this.httpClient.patch<FloorLayout>(this.layoutBaseUri, layout);
  }
}
