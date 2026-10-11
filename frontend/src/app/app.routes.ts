import { Routes } from '@angular/router';
import { authGuard } from './api.service';
import { Login, Dashboard, Transfer, Bills, Loans, Invest, Admin } from './pages';
export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'transfer', component: Transfer, canActivate: [authGuard] },
  { path: 'bills', component: Bills, canActivate: [authGuard] },
  { path: 'loans', component: Loans, canActivate: [authGuard] },
  { path: 'invest', component: Invest, canActivate: [authGuard] },
  { path: 'admin', component: Admin, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
