import { Component, OnInit } from '@angular/core';
import { AlertService } from 'src/app/services/alert.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  standalone: true,
  imports: [
    NgIf
  ],
  styleUrls: ['./alert.component.scss']
})
export class AlertComponent implements OnInit {

  constructor(public alertService: AlertService) { }

  ngOnInit(): void {
  }

}
