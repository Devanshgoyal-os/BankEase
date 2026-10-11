import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { CanActivateFn, Router } from '@angular/router';
const B = `http://${location.hostname}:8080/api`;
@Injectable({ providedIn: 'root' })
export class Api {
  http = inject(HttpClient);
  get token() { return sessionStorage.getItem('t'); }
  get role() { return sessionStorage.getItem('r'); }
  login(b: any) { return this.http.post<any>(B + '/auth/login', b); }
  register(b: any) { return this.http.post<any>(B + '/auth/register', b); }
  get(p: string) { return this.http.get<any[]>(B + p); }
  post(p: string, b: any = {}) { return this.http.post<any>(B + p, b); }
  logout() { sessionStorage.clear(); }
}
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const t = sessionStorage.getItem('t');
  return next(t ? req.clone({ setHeaders: { Authorization: 'Bearer ' + t } }) : req);
};
export const authGuard: CanActivateFn = () => inject(Api).token ? true : inject(Router).createUrlTree(['/login']);
