import { Component } from '@angular/core';
import {HeaderComponent} from './components/header/header.component';
import {AlertComponent} from './components/alert/alert.component';
import {RouterModule} from '@angular/router';
import {FooterComponent} from './components/footer/footer.component';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {MessageComponent} from './components/pages/message/message.component';
import {NgbDatepickerModule} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [
    HeaderComponent,
    AlertComponent,
    RouterModule,
    FooterComponent,
    FormsModule,
    CommonModule,
    MessageComponent,
  ],
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Spring Kitchen';
}
