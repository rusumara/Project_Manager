import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ForgotPasswordRequest,
  LoginService
} from '../../services/login.service';


import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPassword {

  private loginService = inject(LoginService);
  private router = inject(Router);

  email = '';

  sendCode(): void {

    const request: ForgotPasswordRequest = {
      email: this.email
    };

    this.loginService
      .forgotPassword(request)
      .subscribe({

  next: (response) => {

    alert(response);

    this.router.navigate(['/reset-password']);

  },

  error: (err) => {

    console.error(err);
    alert('Failed to send code');

  }

});

  }

}