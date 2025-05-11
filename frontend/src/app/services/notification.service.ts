import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  success(message: string): void {
    this.show('✅ Success: ' + message);
  }

  error(message: string): void {
    this.show('❌ Error: ' + message);
  }

  info(message: string): void {
    this.show('ℹ️ Info: ' + message);
  }

  private show(message: string): void {
    alert(message);
  }
}
