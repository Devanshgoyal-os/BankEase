import { Component, inject } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Api } from './api.service';
@Component({
  selector: 'app-root', standalone: true, imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `<nav><b>🏦 BankEase</b>
    @if (api.token) {
      <a routerLink="/dashboard" routerLinkActive="active">Accounts</a><a routerLink="/transfer" routerLinkActive="active">Transfer</a>
      <a routerLink="/bills" routerLinkActive="active">Bill Pay</a><a routerLink="/loans" routerLinkActive="active">Loans</a><a routerLink="/invest" routerLinkActive="active">Investments</a>
      @if (api.role === 'ADMIN') { <a routerLink="/admin" routerLinkActive="active">Admin & Audit</a> }
      <a (click)="out()">Logout</a>
    }</nav><div class="wrap"><router-outlet/></div>`
})
export class AppComponent { api = inject(Api); router = inject(Router); out() { this.api.logout(); this.router.navigate(['/login']); } }
