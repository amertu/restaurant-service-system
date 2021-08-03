import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../../services/auth.service';
import {AlertService} from '../../../../services/alert.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {ApplicationUser} from '../../../../dtos/application-user';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit, OnChanges {
  @Input() user: ApplicationUser;
  submitted: boolean = false;
  resetForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private route: ActivatedRoute,
              private router: Router,
              private alertService: AlertService,
              private applicationUserService: ApplicationUserService,
              public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
    this.initResetForm();
  }

  ngOnChanges(): void {
    this.initResetForm();
  }

  checkPasswords(p1: string, p2: string) {
    return (formGroup: FormGroup) => {
      const password = formGroup.controls[p1];
      const repeated = formGroup.controls[p2];

      if (repeated.errors && !repeated.errors.notSame) {
        return;
      }
      return password.value !== repeated.value ? repeated.setErrors({notSame: true}) : repeated.setErrors(null);
    };
  }

  onSubmit() {
    this.submitted = true;
    if (this.resetForm.valid) {
      this.user.password = this.resetForm.controls.newPassword.value;
      this.applicationUserService.resetUserPassword(this.user.id, this.user).subscribe(
        () => {
          window.location.reload();
          this.alertService.reportSuccessModal('Password reset successful!');
        },
        error => {
          this.alertService.reportErrorModal(error);
        }
      );
    }
  }

  initResetForm(): void {
    this.resetForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      repeatPassword: ['', [Validators.required]]
    }, {validator: [this.checkPasswords('newPassword', 'repeatPassword')]});
  }
}
