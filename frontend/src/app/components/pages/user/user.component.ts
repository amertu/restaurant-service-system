import {Component, OnInit} from '@angular/core';
import {ApplicationUserService} from '../../../services/application-user.service';
import {ApplicationUser} from '../../../dtos/application-user';
import {Router} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {AlertService} from '../../../services/alert.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UserAddComponent} from './user-add/user-add.component';
import {UserEditComponent} from './user-edit/user-edit.component';
import {UserDeleteComponent} from './user-delete/user-delete.component';
import {PasswordResetComponent} from './password-reset/password-reset.component';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  users: ApplicationUser[];

  constructor(private router: Router,
              private applicationUserService: ApplicationUserService,
              public authService: AuthService,
              private alertService: AlertService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isAdmin()) {
      this.alertService.reportWarning('You have no permission to access this site!');
      this.router.navigateByUrl('/');
    }
    this.loadUsers();
  }

  loadUsers(): void {
    this.applicationUserService.getAllUsers().subscribe({
      next: (users: ApplicationUser[]) => {
        this.users = users;
      },
      error: (error) => {
        this.alertService.error(error);
      }
    });
  }

  /**
   * check if user is logged in as admin
   * @param user the user
   */
  isLoggedInAdmin(user: ApplicationUser): boolean {
    return this.authService.getUserEmail() === user.email;
  }

  onClickBlockButton(user: ApplicationUser, blocked: boolean): void {
    const blockedUser = new ApplicationUser(user.id, user.email, user.password, user.firstName,
      user.lastName, user.ssnr, user.admin, blocked);
    this.applicationUserService.changeUserBlockedValue(user.id, blockedUser).subscribe(
      data => {
        if (blocked) {
          this.alertService.reportSuccessModal('User ' + user.firstName + ' ' + user.lastName + ' has been blocked!');
        } else {
          this.alertService.reportSuccessModal('User ' + user.firstName + ' ' + user.lastName + ' has been unblocked!');
        }
        this.loadUsers();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }

  onClickResetPassword(user) {
    const modalRef = this.modalService.open(PasswordResetComponent);
    modalRef.componentInstance.user = user;
    //the result is a promise that gets resolved on close action and rejected on dismiss
    //modal is closed if the user confirms the action, otherwise it is dismissed
    modalRef.result.then(() => this.loadUsers());
  }

  onClickEditUser(user) {
    const modalRef = this.modalService.open(UserEditComponent);
    modalRef.componentInstance.user = user;
    modalRef.result.then(() => this.loadUsers());
  }

  onClickAddUser() {
    const modalRef = this.modalService.open(UserAddComponent);
    modalRef.result.then(() => this.loadUsers());
  }

  onClickDeleteUser(user) {
    const modalRef = this.modalService.open(UserDeleteComponent);
    modalRef.componentInstance.user = user;
    modalRef.result.then(() => this.loadUsers());
  }
}
