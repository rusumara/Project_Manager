import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
})
export class ResetPassword {

  private loginService = inject(LoginService);

  email = '';
  code = '';
  newPassword = '';

  resetPassword() {

    this.loginService.resetPassword({
      email: this.email,
      code: this.code,
      newPassword: this.newPassword
    }).subscribe({

      next: (response) => {

        alert(response);

      },

      error: () => {

        alert('Reset failed');

      }

    });

  }

}