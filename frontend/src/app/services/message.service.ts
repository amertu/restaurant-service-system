import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {catchError, tap} from 'rxjs/operators';
import {NotificationService} from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private readonly messageBaseUri = `${this.globals.backendUri}/messages`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private notificationService: NotificationService
  ) {}

  /**
   * Loads all messages from the backend
   */
  getMessage(): Observable<Message[]> {
    return this.httpClient.get<Message[]>(this.messageBaseUri);
  }

  /**
   * Loads specific message from the backend
   * @param id of message to load
   */
  getMessageById(id: number): Observable<Message> {
    return this.httpClient.get<Message>(`${this.messageBaseUri}/${id}`).pipe(
      catchError(error => {
        console.error('Error loading message', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Persists message to the backend
   * @param message to persist
   */
  createMessage(message: Message): Observable<Message> {
    console.log('Creating message with title:', message.title);
    return this.httpClient.post<Message>(`${this.messageBaseUri}`, message).pipe(
      tap((createdMessage) => {
        console.log('Message created successfully:', createdMessage);
        //this.notificationService.success('Message created successfully'); // optional
      }),
      catchError(error => {
        console.error('Error creating message:', error);
        const message = this.extractErrorMessage(error);
        //this.notificationService.error(message); // optional
        return throwError(() => error);
      })
    );
  }
  /**
   * Extracts a human-readable error message from backend or default.
   */
  private extractErrorMessage(error: any): string {
    if (error?.error?.message) {
      return error.error.message;
    }
    if (error?.message) {
      return error.message;
    }
    return 'An unknown error occurred while creating the message.';
  }
}
