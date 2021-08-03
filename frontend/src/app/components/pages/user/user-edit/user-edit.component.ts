import {Component, Input, OnInit, OnChanges} from '@angular/core';
import {ApplicationUser} from '../../../../dtos/application-user';
import {FormBuilder, Validators} from '@angular/forms';
import {ApplicationUserService} from '../../../../services/application-user.service';
import {AlertService} from '../../../../services/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit, OnChanges {
  @Input() user: ApplicationUser;
  submitted: boolean = false;

  constructor(private applicationUserService: ApplicationUserService,
              private formBuilder: FormBuilder,
              private alertService: AlertService,
              public activeModal: NgbActiveModal) {
  }

  editForm = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    admin: [false, [Validators.required]],
    email: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.initUserFormGroup();
  }

  ngOnChanges(): void {
    this.initUserFormGroup();
  }

  onSubmitUpdate() {
    this.submitted = true;
    if (this.editForm.valid) {
      this.applicationUserService.updateUser(this.editForm.value).subscribe(() => {
          this.alertService.reportSuccessModal('Successfully updated user.');
          this.activeModal.close();
        },
        error => {
          this.alertService.reportErrorModal(error);
        }
      );
    }
  }

  private initUserFormGroup() {
    this.editForm = this.formBuilder.group({
      id: [this.user.id],
      firstName: [this.user.firstName, [Validators.required]],
      lastName: [this.user.lastName, [Validators.required]],
      ssnr: [this.user.ssnr, [Validators.required]],
      email: [this.user.email],
      admin: [this.user.admin, [Validators.required]]
    });
  }
}
