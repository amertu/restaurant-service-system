import {Component, Input, OnInit, OnChanges} from '@angular/core';
import {ApplicationUser} from '../../../../dtos/application-user';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {AlertService} from '../../../../services/alert.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit, OnChanges {
  @Input() user: any;
  submitted: boolean = false;
  editForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private applicationUserService: ApplicationUserService,
    private alertService: AlertService,
    public activeModal: NgbActiveModal
  ) {
  }

  ngOnInit(): void {
    this.initUserFormGroup();
  }

  ngOnChanges(): void {
    this.initUserFormGroup();
  }

  private initUserFormGroup(): void {
    if (!this.user) {
      return;
    }

    this.editForm = this.formBuilder.group({
      firstName: [this.user.firstName, [Validators.required]],
      lastName: [this.user.lastName, [Validators.required]],
      email: [this.user.email, [Validators.required, Validators.email]],
      ssnr: [this.user.ssnr],
      admin: [this.user.admin]
    });

  }

  onSubmitUpdate(): void {
    this.submitted = true;
    if (this.editForm.invalid) {
      return;
    }

    const updatedUser: ApplicationUser = {
      ...this.user,
      ...this.editForm.value
    };

    this.applicationUserService.updateUser(updatedUser).subscribe({
      next: () => {
        this.alertService.reportSuccessModal('Successfully updated user.');
        this.activeModal.close();
      },
      error: (error) => {
        this.alertService.reportErrorModal(error);
      }
    });

  }
}
