import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-alert-content-for-ngb-modal',
  templateUrl: './alert-content-for-ngb-modal.component.html',
  standalone: true,
  styleUrls: ['./alert-content-for-ngb-modal.component.css']
})
export class AlertContentForNgbModalComponent implements OnInit {

  @Input() title: string;
  @Input() message: string;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
