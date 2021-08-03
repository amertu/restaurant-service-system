import {Component, Input, OnInit} from '@angular/core';
import {ApplicationUser} from '../../../../dtos/application-user';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {AlertService} from '../../../../services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-user-delete',
  templateUrl: './user-delete.component.html',
  styleUrls: ['./user-delete.component.scss']
})
export class UserDeleteComponent implements OnInit {
  @Input() user: ApplicationUser;

  constructor(public applicationUserService: ApplicationUserService, public alertService: AlertService, public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  private deleteUser(user: ApplicationUser) {
    this.applicationUserService.deleteAsAdmin(user.email, localStorage.getItem('username')).subscribe(
      () => {
        this.alertService.reportSuccessModal('Successfully deleted user.');
        this.activeModal.close();
      },
      error => {
        console.log(error);
        this.alertService.reportErrorModal(error);
      }
    );
  }
}
