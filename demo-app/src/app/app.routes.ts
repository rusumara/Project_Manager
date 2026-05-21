import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'people',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/person-list/person-list-page.component').then(
        (m) => m.PersonListPageComponent,
      ),
  },
  {
    path: 'projects',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/project-list/project-list-page.component').then(
        (m) => m.ProjectListPageComponent,
      ),
  },
  {
    path: 'skills',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/skill-list/skill-list-page.component').then(
        (m) => m.SkillListPageComponent,
      ),
  },

{
  path: 'admin/people',
  loadComponent: () =>
    import('./features/person-list/person-list-page.component')
      .then(m => m.PersonListPageComponent)
},

{
  path: 'admin/projects',
  loadComponent: () =>
    import('./features/project-list/project-list-page.component')
      .then(m => m.ProjectListPageComponent)
},

{
  path: 'admin/skills',
  loadComponent: () =>
    import('./features/skill-list/skill-list-page.component')
      .then(m => m.SkillListPageComponent)
},

{
  path: 'user/dashboard',
  loadComponent: () =>
    import('./features/person-list/person-list-page.component')
      .then(m => m.PersonListPageComponent)
},

{
  path: 'user/people',
  loadComponent: () =>
    import('./features/person-list/person-list-page.component')
      .then(m => m.PersonListPageComponent)
},

{
  path: 'forgot-password',
  loadComponent: () =>
    import('./pages/forgot-password/forgot-password')
      .then(m => m.ForgotPassword)
},

  {
    path: 'error',
    loadComponent: () =>
      import('./features/not-found/not-found-page.component').then(
        (m) => m.NotFoundPageComponent,
      ),
  },

  {
  path: 'reset-password',
  loadComponent: () =>
    import('./pages/reset-password/reset-password')
      .then(m => m.ResetPassword)
},
  {
    path: '**',
    redirectTo: 'error',
  },
];