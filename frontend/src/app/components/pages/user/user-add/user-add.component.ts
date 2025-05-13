import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {AlertService} from '../../../../services/alert.service';
import {ApplicationUser} from '../../../../dtos/application-user';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./user-add.component.scss']
})
export class UserAddComponent implements OnInit {
  submitted: boolean = false;
  protected addUserForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private applicationUserService: ApplicationUserService,
              private alertService: AlertService,
              public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
    this.initAddUserForm();
  }

  private initAddUserForm() {
    this.addUserForm = this.formBuilder.group<{
      firstName: FormControl<string>;
      lastName: FormControl<string>;
      ssnr: FormControl<string>;
      admin: FormControl<boolean>;
      email: FormControl<string>;
      password: FormControl<string>;
      repeatPassword: FormControl<string>;
    }>({
      firstName: this.formBuilder.control<string>('', [Validators.required]),
      lastName: this.formBuilder.control<string>('', [Validators.required]),
      ssnr: this.formBuilder.control<string>('', [
        Validators.required,
        Validators.pattern('^[0-9]{10}$')
      ]),
      admin: this.formBuilder.control<boolean>(false, [Validators.required]),
      email: this.formBuilder.control<string>('', [
        Validators.required,
        Validators.email,
        Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')
      ]),
      password: this.formBuilder.control<string>('', [
        Validators.required,
        Validators.minLength(8)
      ]),
      repeatPassword: this.formBuilder.control<string>('', [Validators.required])
    }, {
      validators: [this.checkPasswords('password', 'repeatPassword'), this.checkSSNR('ssnr')]
    });
  }


  /**
   * check if the two password entries match
   * @param passwordKey
   * @param repeatPasswordKey
   */
  checkPasswords(passwordKey: string, repeatPasswordKey: string): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get(passwordKey)?.value;
      const repeatPassword = formGroup.get(repeatPasswordKey)?.value;

      if (password !== repeatPassword) {
        return {passwordMismatch: true};
      }
      return null;
    };
  }

  checkSSNR(controlName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const formGroup = control as FormGroup;
      const ssnrControl = formGroup.controls[controlName];

      if (!ssnrControl) {
        return null;
      }

      const value = ssnrControl.value;
      const isInvalid: boolean = this.isInvalidSSNR(value);

      // Preserve other errors, don't overwrite them
      const currentErrors = ssnrControl.errors || {};

      if (isInvalid) {
        if (!currentErrors['invalidSSNR']) {
          ssnrControl.setErrors({...currentErrors, invalidSSNR: true});
        }
      } else {
        if (currentErrors['invalidSSNR']) {
          delete currentErrors['invalidSSNR'];
          ssnrControl.setErrors(Object.keys(currentErrors).length ? currentErrors : null);
        }
      }

      return null;
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
      + +(+ssnr.charAt(8))
      + +(+ssnr.charAt(9)) * 6) % 11;
    return checkNumber === 10 || checkNumber !== (+ssnr.charAt(3));
  }

  onSubmitAdd() {
    this.submitted = true;
    if (this.addUserForm.valid) {
      const userRegister: ApplicationUser = new ApplicationUser(null,
        this.addUserForm.controls.email.value,
        this.addUserForm.controls.password.value,
        this.addUserForm.controls.firstName.value,
        this.addUserForm.controls.lastName.value,
        this.addUserForm.controls.ssnr.value,
        this.addUserForm.controls.admin.value,
        false);
      this.save(userRegister);
    }
  }

  save(userRegister: ApplicationUser) {
    this.applicationUserService.saveAsAdmin(userRegister).subscribe({
      next: () => {
        this.alertService.reportSuccessModal('Successfully added user.');
        this.activeModal.close();
      },
      error: error => {
        this.alertService.reportErrorModal(error);
      }
    });
  }
}
