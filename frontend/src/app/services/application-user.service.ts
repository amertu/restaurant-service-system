import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {ApplicationUser} from '../dtos/application-user';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApplicationUserService {

  private readonly messageBaseUri: string;
  private errorMessage: string = ' ';

  constructor(private httpClient: HttpClient,
              private globals: Globals) {
    this.messageBaseUri = this.globals.backendUri + '/users';
  }

  /**
   * Loads all users from the backend
   */
  getAllUsers(): Observable<ApplicationUser[]> {
    console.log('Get all users');
    return this.httpClient.get<ApplicationUser[]>(this.messageBaseUri);
  }

  /**
   * Resets a users password to the specified value
   * @param id   the ID of the user
   * @param user the user with the new password
   */
  resetUserPassword(id: number, user: ApplicationUser): Observable<number> {
    console.log('Reset password of user #' + id);
    return this.httpClient.put<number>(this.messageBaseUri + '/' + id + '/passwordReset', user).pipe(
      catchError((error) => {
        console.error('changeUserPassword: error caught in service');
        this.defaultServiceErrorHandling(error);
        return throwError(error);
      })
    );
  }

  /**
   * Changes the blocked value of a user
   * @param id marks the user where the blocked value should be changed
   * @param blocked the new value of blocked
   */
  changeUserBlockedValue(id: number, blockedUser: ApplicationUser): Observable<number> {
    console.log('Changes the blocked value of the user with the id ' + id + ' to ' + blockedUser.blocked);
    return this.httpClient.put<number>(this.messageBaseUri + '/changeUserBlockedValue/' + id, blockedUser);
  }

  /**
   * Save given user
   * @param userRegister to be made
   */
  saveAsAdmin(userRegister: ApplicationUser): Observable<ApplicationUser> {
    return this.httpClient.post<ApplicationUser>(this.messageBaseUri, userRegister);
  }

  /**
   * Delete a user as admin
   * @param userToDeleteEmail email to be deleted
   * @param adminPerformingAction a user who is deleting
   */
  deleteAsAdmin(userToDeleteEmail: string, adminPerformingAction: string): Observable<ApplicationUser> {
    let param = new HttpParams();
    param = param.set('userPerformingAction', adminPerformingAction);
    return this.httpClient.delete<ApplicationUser>(this.messageBaseUri + '/' + userToDeleteEmail, {params: param});
  }

  /**
   * Update an existing user
   * @param user user to be updated
   */
  updateUser(user: ApplicationUser): Observable<ApplicationUser> {
    return this.httpClient.put<ApplicationUser>(this.messageBaseUri, user);
  }

  /**
   * Load a user data given the email
   * @param id of the user
   */
  getUserById(id: number): Observable<ApplicationUser> {
    return this.httpClient.get<ApplicationUser>(this.messageBaseUri + '/' + id);
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    if (error.status === 0) {
      this.errorMessage = 'Backend seems not to be down';
    } else if (!error.error || error.error.message === 'No message available') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error.message;
    }
  }
}
