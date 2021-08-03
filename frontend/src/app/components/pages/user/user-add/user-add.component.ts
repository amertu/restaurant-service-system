import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {AlertService} from '../../../../services/alert.service';
import {ApplicationUser} from '../../../../dtos/application-user';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.scss']
})
export class UserAddComponent implements OnInit {
  submitted: boolean = false;

  constructor(private formBuilder: FormBuilder,
              private applicationUserService: ApplicationUserService,
              private alertService: AlertService,
              public activeModal: NgbActiveModal) {
  }

  addForm = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    ssnr: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
    admin: [false, [Validators.required]],
    email: ['', [Validators.required, Validators.email, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    repeatPassword: ['', [Validators.required]]
  }, {
    validator: [this.checkPasswords('password', 'repeatPassword'), this.checkSSNR('ssnr')]
  });

  ngOnInit(): void {
  }

  /**
   * check if the two password entries match
   * @param password the first password entry
   * @param repeatPassword the second password entry
   */
  checkPasswords(password: string, repeatPassword: string) {
    return (formGroup: FormGroup) => {
      const pass = formGroup.controls[password];
      const repeat = formGroup.controls[repeatPassword];

      if (repeat.errors && !repeat.errors.notSame) {
        return;
      }
      return pass.value !== repeat.value ? repeat.setErrors({notSame: true}) : repeat.setErrors(null);
    };
  }

  checkSSNR(ssnr: string) {
    return (formGroup: FormGroup) => {
      const nr = formGroup.controls[ssnr];
      if (nr.errors && !nr.errors.invalidSSNR) {
        return;
      }
      return this.isInvalidSSNR(nr.value) ? nr.setErrors({invalidSSNR: true}) : nr.setErrors(null);
    };
  }

  isInvalidSSNR(ssnr: string): boolean {
    if (ssnr.charAt(0) === '0') {
      return true;
    }
    const checkNumber = ((+ssnr.charAt(0)) * 3
      + +(+ssnr.charAt(1)) * 7
      + +(+ssnr.charAt(2)) * 9
      + +(+ssnr.charAt(4)) * 5
      + +(+ssnr.charAt(5)) * 8
      + +(+ssnr.charAt(6)) * 4
      + +(+ssnr.charAt(7)) * 2
      + +(+ssnr.charAt(8)) * 1
      + +(+ssnr.charAt(9)) * 6) % 11;
    return checkNumber === 10 || checkNumber !== (+ssnr.charAt(3));
  }

  onSubmitAdd() {
    this.submitted = true;
    if (this.addForm.valid) {
      const userRegister: ApplicationUser = new ApplicationUser(null,
        this.addForm.controls.email.value,
        this.addForm.controls.password.value,
        this.addForm.controls.firstName.value,
        this.addForm.controls.lastName.value,
        this.addForm.controls.ssnr.value,
        this.addForm.controls.admin.value,
        false);
      this.save(userRegister);
    }
  }

  save(userRegister: ApplicationUser) {
    this.applicationUserService.saveAsAdmin(userRegister).subscribe(() => {
        this.alertService.reportSuccessModal('Successfully added user.')
        this.activeModal.close();
      },
      error => {
        this.alertService.reportErrorModal(error);
      }
    );
  }
}
