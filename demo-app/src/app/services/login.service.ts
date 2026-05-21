import { inject, Injectable } from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import { Observable } from 'rxjs';

const API_URL =
  'http://localhost:8080/login';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  accessToken: string;
  refreshToken: string;
  role: string | null;
  errorMessage: string | null;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  code: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private readonly http =
    inject(HttpClient);

  login(
    request: LoginRequest
  ): Observable<LoginResponse> {

    return this.http.post<LoginResponse>(
      API_URL,
      request
    );
  }

  forgotPassword(
    request: ForgotPasswordRequest
  ): Observable<string> {

    return this.http.post(
      'http://localhost:8080/password/forgot',
      request,
      {
        responseType: 'text'
      }
    );
  }

  resetPassword(
    request: ResetPasswordRequest
  ): Observable<string> {

    return this.http.post(
      'http://localhost:8080/password/reset',
      request,
      {
        responseType: 'text'
      }
    );
  }

  refreshToken() {

  const refreshToken =
    sessionStorage.getItem(
      'refreshToken'
    );
   // if(!refreshToken) {
    //  return throwError(() => new Error('No refresh token available'));

  return this.http.post<any>(
    'http://localhost:8080/refresh',
    {
      refreshToken
    }
  );
}
}