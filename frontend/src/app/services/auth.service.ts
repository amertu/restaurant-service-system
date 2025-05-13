import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {Globals} from '../global/globals';
import {jwtDecode, JwtPayload} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string;
  email: string;

  constructor(private httpClient: HttpClient, private globals: Globals) {
    this.authBaseUri = this.globals.backendUri + '/authentication';
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }

  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  logoutUser() {
    console.log('Logout');
    localStorage.removeItem('authToken');
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  isAdmin() {
    return this.getUserRole() === 'ADMIN';
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole(): string {
    const token = this.getToken();

    if (token) {
      try {
        const decoded = jwtDecode<CustomJwtPayload>(token);
        const roles = decoded.rol;

        if (roles?.includes('ROLE_ADMIN')) {
          return 'ADMIN';
        } else if (roles?.includes('ROLE_USER')) {
          return 'USER';
        }
      } catch (err) {
        console.error('Invalid JWT:', err);
      }
    }

    return 'UNDEFINED';
  }

  /**
   * Return the email of the user
   */
  getUserEmail() {
    if (this.isLoggedIn()) {
      return this.email;
    }
  }

  private getTokenExpirationDate(token: string): Date | null {
    try {
      const decoded: JwtPayload = jwtDecode<JwtPayload>(token);

      if (decoded.exp === undefined) {
        return null;
      }

      const date = new Date(0);
      date.setUTCSeconds(decoded.exp);
      return date;
    } catch (error) {
      console.error('Invalid JWT token:', error);
      return null;
    }
  }

}

interface CustomJwtPayload extends JwtPayload {
  rol: string[];
}
