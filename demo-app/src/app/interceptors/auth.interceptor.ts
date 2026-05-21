import { inject } from '@angular/core';

import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';

import { Router } from '@angular/router';

import {
  catchError,
  switchMap,
  throwError
} from 'rxjs';

import { LoginService }
from '../services/login.service';

export const authInterceptor:
HttpInterceptorFn = (req, next) => {

  const isAuthRequest =
  req.url.includes('/login') ||
  req.url.includes('/refresh');

if (isAuthRequest) {
  return next(req);
}

  const token =
    sessionStorage.getItem(
      'accessToken'
    );

  const loginService =
    inject(LoginService);

  const router =
    inject(Router);

  let authReq = req;

  if (token) {

    authReq = req.clone({
      setHeaders: {
        Authorization:
          `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(

    catchError(
      (error: HttpErrorResponse) => {

        if (error.status === 401 || error.status === 403) {

          return loginService
            .refreshToken()
            .pipe(

              switchMap((response) => {

                sessionStorage.setItem(
                  'accessToken',
                  response.accessToken
                );

                const retryReq =
                  req.clone({
                    setHeaders: {
                      Authorization:
                        `Bearer ${response.accessToken}`
                    }
                  });

                return next(retryReq);
              }),

              catchError(() => {

                sessionStorage.clear();

                router.navigate([
                  '/login'
                ]);

                return throwError(
                  () => error
                );
              })
            );
        }

        return throwError(
          () => error
        );
      }
    )
  );
};