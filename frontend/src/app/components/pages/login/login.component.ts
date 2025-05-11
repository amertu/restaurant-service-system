import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {AuthRequest} from '../../../dtos/auth-request';
import {AlertService} from '../../../services/alert.service';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted: boolean = false;

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private alertService: AlertService) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.username.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);

    this.authService.loginUser(authRequest).subscribe({
      next: async () => {
        console.log('Successfully logged in user: ' + authRequest.email);
        this.authService.email = authRequest.email;

        try {
          await this.router.navigate(['/message']);
          console.log('Navigation complete');
        } catch (navError) {
          console.error('Navigation failed:', navError);
        }
      },
      error: (error) => {
        console.log('Could not log in due to:');
        this.alertService.error(error);
      }
    });
  }


  ngOnInit() {
  }

}
