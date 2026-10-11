import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Api } from './api.service';
const M = [CommonModule, FormsModule];
class Base {
  api = inject(Api); msg = ''; err = ''; accounts: any[] = [];
  done(o: any, ok: string, cb?: () => void) {
    o.subscribe({ next: (r: any) => { this.msg = r?.message || ok; this.err = ''; cb && cb(); }, error: (e: any) => { this.err = e.error?.message || ('Request failed (HTTP ' + e.status + ')'); this.msg = ''; } });
  }
  loadAccounts() { this.api.get('/accounts').subscribe(a => { this.accounts = a; this.f.accountNo = this.f.fromAcc = a[0]?.accountNo; }); }
  f: any = {};
  status() { return `<p class="err">{{err}}</p><p class="ok">{{msg}}</p>`; }
}
// Module 1: Authentication & Security
@Component({ standalone: true, imports: M, selector: 'p-login', template: `<div class="split"><div class="side"><h1>🏦 BankEase</h1><p>Secure NextGen Net Banking</p><ul><li>🔒 JWT-secured sessions</li><li>⚡ Instant IMPS / NEFT / RTGS</li><li>🧾 Bill payments in one click</li><li>📈 Loans &amp; Fixed Deposits</li></ul></div><div class="card login"><h1>Welcome</h1><p class="tag">Sign in to continue</p><h3 style="text-align:left">{{reg ? 'Create account' : 'Sign in'}}</h3>
  @if (reg) { <input placeholder="Full name" [(ngModel)]="f.fullName"><input placeholder="Email" [(ngModel)]="f.email"> }
  @if (!otpStep) { <input placeholder="Username" [(ngModel)]="f.username"><input type="password" placeholder="Password" [(ngModel)]="f.password"> } @else { <p class="ok">📱 Demo SMS: your OTP is <b>{{demoOtp}}</b></p><input placeholder="Enter 6-digit OTP" [(ngModel)]="f.otp"> }
  <button (click)="go()">{{reg ? 'Register' : (otpStep ? 'Verify OTP' : 'Login')}}</button> <a style="cursor:pointer;margin-left:10px" (click)="reg = !reg">{{reg ? 'Have an account?' : 'New user?'}}</a>
  <p class="err">{{err}}</p><p class="ok">{{msg}}</p><small>Demo admin: admin / admin123</small></div></div>` })
export class Login extends Base {
  reg = false; otpStep = false; demoOtp = ''; router = inject(Router);
  go() {
    if (this.reg) return this.done(this.api.register(this.f), 'Registered', () => this.reg = false);
    const fail = (e: any) => this.err = e.error?.message || 'Login failed';
    if (this.otpStep) return this.api.post('/auth/verify-otp', { username: this.f.username, otp: this.f.otp }).subscribe({ next: (r: any) => { sessionStorage.setItem('t', r.token); sessionStorage.setItem('r', r.role); sessionStorage.setItem('n', r.name); this.router.navigate(['/dashboard']); }, error: fail });
    this.api.login(this.f).subscribe({ next: (r: any) => { if (r.otpRequired) { this.otpStep = true; this.demoOtp = r.demoOtp; this.err = ''; } }, error: fail });
  }
}
// Module 2: Account Management
@Component({ standalone: true, imports: [...M, RouterLink], selector: 'p-dash', template: `
  <div class="hero"><div><small>Welcome back</small><h1>{{name}} 👋</h1><p>Manage your money securely with BankEase.</p></div></div>
  <div class="stats">
    <div class="stat"><span>Total Balance</span><b>₹ {{total | number:'1.2-2'}}</b></div>
    <div class="stat"><span>Accounts</span><b>{{accounts.length}}</b></div>
    <div class="stat"><span>Transactions</span><b>{{stmt.length}}</b></div>
    <div class="stat"><span>Money Sent</span><b>₹ {{sent | number:'1.0-0'}}</b></div>
  </div>
  <div class="quick"><a routerLink="/transfer">💸 Transfer</a><a routerLink="/bills">🧾 Pay Bills</a><a routerLink="/loans">🏠 Loans</a><a routerLink="/invest">📈 Invest</a></div>
  <div class="card"><h3>My Accounts</h3><div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px"><select style="width:auto;margin:0" [(ngModel)]="f.type"><option>SAVINGS</option><option>CURRENT</option></select><button style="margin:0" (click)="openAcc()">+ Open New Account</button></div><p class="err">{{err}}</p><p class="ok">{{msg}}</p>
  @for (a of accounts; track a.id) { <div class="acc"><div><small>{{a.type}} ACCOUNT</small><p>{{a.accountNo}} • IFSC {{a.ifsc}}</p></div><div class="bal">₹ {{a.balance | number:'1.2-2'}}</div></div> }</div>
  <div class="card"><h3>🔔 Recent Alerts</h3>@for (n of notes; track n.id) { <p class="note">{{n.sentAt | date:'short'}} — {{n.message}}</p> }@if (!notes.length) { <small>No alerts yet</small> }</div>
  <div class="card"><h3>Recent Transactions</h3><button style="margin:0 0 8px" (click)="csv()">⬇ Download CSV</button><table><tr><th>Date</th><th>Ref</th><th>To / From</th><th>Mode</th><th>Amount</th><th>Status</th></tr>
  @for (t of stmt; track t.id) { <tr><td>{{t.createdAt | date:'short'}}</td><td>{{t.refNo}}</td><td>{{isDr(t) ? t.toAcc : t.fromAcc}}</td><td>{{t.mode}}</td>
   <td [class]="isDr(t) ? 'dr' : 'cr'">{{isDr(t) ? '−' : '+'}} ₹{{t.amount}}</td><td><span class="badge" [class.warn]="t.status !== 'SUCCESS'">{{t.status}}</span></td></tr> }</table></div>` })
export class Dashboard extends Base { stmt: any[] = []; name = sessionStorage.getItem('n') || 'Customer';
  notes: any[] = [];
  constructor() { super(); this.f.type = 'SAVINGS'; this.loadAccounts(); this.api.get('/statement').subscribe(s => this.stmt = s); this.api.get('/notifications').subscribe(n => this.notes = n); }
  openAcc() { this.done(this.api.post('/accounts/open', { type: this.f.type }), 'Account opened', () => this.loadAccounts()); }
  csv() { const rows = [['Date', 'Ref', 'From', 'To', 'Mode', 'Amount', 'Status'], ...this.stmt.map(t => [t.createdAt, t.refNo, t.fromAcc, t.toAcc, t.mode, t.amount, t.status])];
    const a = document.createElement('a'); a.href = URL.createObjectURL(new Blob([rows.map(r => r.join(',')).join('\n')], { type: 'text/csv' })); a.download = 'statement.csv'; a.click(); }
  get total() { return this.accounts.reduce((s, a) => s + +a.balance, 0); }
  isDr(t: any) { return this.accounts.some(a => a.accountNo === t.fromAcc); }
  get sent() { return this.stmt.filter(t => this.isDr(t)).reduce((s, t) => s + +t.amount, 0); } }
// Module 3: Fund Transfer
@Component({ standalone: true, imports: M, selector: 'p-transfer', template: `<div class="hero slim "><div><small>Fund Transfer</small><h1>💸 Send Money</h1><p>IMPS, NEFT and RTGS — fast and secure.</p></div></div><div class="stats"><div class="stat"><span>Saved Beneficiaries</span><b>{{bens.length}}</b></div><div class="stat"><span>Per-transfer Limit</span><b>₹ 2,00,000</b></div><div class="stat"><span>Alert Above</span><b>₹ 1,00,000</b></div></div><div class="card"><h3>Add Beneficiary</h3>
  <input placeholder="Name" [(ngModel)]="b.name"><input placeholder="Account No" [(ngModel)]="b.accountNo"><input placeholder="IFSC (e.g. BEAS0000001)" [(ngModel)]="b.ifsc">
  <button (click)="addBen()">Add</button></div>
  <div class="card"><h3>Transfer Funds</h3><select [(ngModel)]="f.fromAcc">@for (a of accounts; track a.id) { <option [value]="a.accountNo">{{a.accountNo}} (₹{{a.balance}})</option> }</select>
  <select [(ngModel)]="f.toAcc"><option value="">-- Select beneficiary --</option>@for (x of bens; track x.id) { <option [value]="x.accountNo">{{x.name}} - {{x.accountNo}}</option> }</select>
  <select [(ngModel)]="f.mode"><option>IMPS</option><option>NEFT</option><option>RTGS</option></select><input type="number" placeholder="Amount" [(ngModel)]="f.amount">
  <button (click)="send()">Send Money</button><p class="err">{{err}}</p><p class="ok">{{msg}}</p></div>` })
export class Transfer extends Base {
  b: any = {}; bens: any[] = [];
  constructor() { super(); this.f.mode = 'IMPS'; this.loadAccounts(); this.lb(); }
  lb() { this.api.get('/beneficiaries').subscribe(x => this.bens = x); }
  addBen() { this.done(this.api.post('/beneficiaries', this.b), 'Beneficiary added', () => { this.lb(); this.b = {}; }); }
  send() { this.done(this.api.post('/transfers', this.f), 'Transfer successful', () => this.loadAccounts()); }
}
// Module 4: Bill Payment
@Component({ standalone: true, imports: M, selector: 'p-bills', template: `<div class="hero slim g2"><div><small>Bill Payment</small><h1>🧾 Pay Your Bills</h1><p>Electricity, mobile, DTH and cards in one place.</p></div></div><div class="stats"><div class="stat"><span>Available Billers</span><b>{{billers.length}}</b></div><div class="stat"><span>Accounts</span><b>{{accounts.length}}</b></div></div><div class="card"><h3>Pay a Bill</h3>
  <select [(ngModel)]="f.accountNo">@for (a of accounts; track a.id) { <option [value]="a.accountNo">{{a.accountNo}} (₹{{a.balance}})</option> }</select>
  <select [(ngModel)]="f.billerId">@for (x of billers; track x.id) { <option [value]="x.id">{{x.name}} ({{x.category}})</option> }</select>
  <input placeholder="Consumer / Mobile No" [(ngModel)]="f.consumerNo"><input type="number" placeholder="Amount" [(ngModel)]="f.amount">
  <button (click)="pay()">Pay Now</button><p class="err">{{err}}</p><p class="ok">{{msg}}</p></div>
  <div class="card"><h3>Payment History &amp; Receipts</h3><table><tr><th>Date</th><th>Biller</th><th>Consumer No</th><th>Amount</th><th>Receipt</th></tr>
  @for (x of hist; track x.id) { <tr><td>{{x.paidOn | date:'short'}}</td><td>{{bname(x.billerId)}}</td><td>{{x.consumerNo}}</td><td>₹{{x.amount}}</td><td>#RCP{{x.id}}</td></tr> }</table></div>` })
export class Bills extends Base { billers: any[] = [];
  constructor() { super(); this.loadAccounts(); this.api.get('/billers').subscribe(b => { this.billers = b; this.f.billerId = b[0]?.id; }); this.lh(); }
  hist: any[] = [];
  lh() { this.api.get('/bills').subscribe(h => this.hist = h); } bname(id: any) { return this.billers.find(b => b.id == id)?.name || id; }
  pay() { this.done(this.api.post('/bills', this.f), 'Bill paid successfully', () => { this.loadAccounts(); this.lh(); }); } }
// Module 5: Loan Services
@Component({ standalone: true, imports: M, selector: 'p-loans', template: `<div class="hero slim g3"><div><small>Loan Services</small><h1>🏠 Loans Made Easy</h1><p>Home 8.5% • Car 9.5% • Personal 12% — instant EMI.</p></div></div><div class="stats"><div class="stat"><span>My Loans</span><b>{{list.length}}</b></div><div class="stat"><span>Total Borrowed</span><b>₹ {{borrowed | number:'1.0-0'}}</b></div><div class="stat"><span>Monthly EMI</span><b>₹ {{emiTotal | number:'1.0-0'}}</b></div></div><div class="card"><h3>Apply for Loan</h3>
  <select [(ngModel)]="f.type"><option>HOME</option><option>CAR</option><option>PERSONAL</option></select>
  <input type="number" placeholder="Amount" [(ngModel)]="f.principal"><input type="number" placeholder="Tenure (months)" [(ngModel)]="f.months">
  <button (click)="apply()">Apply & Calculate EMI</button><p class="err">{{err}}</p><p class="ok">{{msg}}</p></div>
  <div class="card"><h3>My Loans</h3><table><tr><th>Type</th><th>Amount</th><th>Rate %</th><th>Months</th><th>EMI</th><th>Status</th></tr>
  @for (l of list; track l.id) { <tr><td>{{l.type}}</td><td>{{l.principal}}</td><td>{{l.rate}}</td><td>{{l.months}}</td><td>{{l.emi}}</td><td><span class="badge" [class.warn]="l.status !== 'APPROVED'">{{l.status}}</span></td></tr> }</table></div>` })
export class Loans extends Base { list: any[] = [];
  get borrowed() { return this.list.reduce((s, l) => s + +l.principal, 0); } get emiTotal() { return this.list.reduce((s, l) => s + +l.emi, 0); }
  constructor() { super(); this.f.type = 'HOME'; this.ld(); } ld() { this.api.get('/loans').subscribe(l => this.list = l); }
  apply() { this.done(this.api.post('/loans', this.f), 'Loan application submitted', () => this.ld()); } }
// Module 6: Investment Services
@Component({ standalone: true, imports: M, selector: 'p-invest', template: `<div class="hero slim g2"><div><small>Investments</small><h1>📈 Grow Your Savings</h1><p>Fixed Deposits at 6.5% per year with instant maturity value.</p></div></div><div class="stats"><div class="stat"><span>Active Investments</span><b>{{list.length}}</b></div><div class="stat"><span>Total Invested</span><b>₹ {{invested | number:'1.0-0'}}</b></div><div class="stat"><span>Maturity Value</span><b>₹ {{maturity | number:'1.0-0'}}</b></div></div><div class="card"><h3>Open Investment</h3>
  <select [(ngModel)]="f.accountNo">@for (a of accounts; track a.id) { <option [value]="a.accountNo">{{a.accountNo}} (₹{{a.balance}})</option> }</select>
  <select [(ngModel)]="f.type"><option value="FD">Fixed Deposit (6.5% p.a., lump sum)</option><option value="RD">Recurring Deposit (monthly installment)</option><option value="SIP">Mutual Fund SIP (~12% est., monthly)</option></select><input type="number" placeholder="Amount / installment" [(ngModel)]="f.amount"><input type="number" placeholder="Tenure (months)" [(ngModel)]="f.months">
  <button (click)="open()">Open FD</button><p class="err">{{err}}</p><p class="ok">{{msg}}</p></div>
  <div class="card"><h3>My Investments</h3><table><tr><th>Type</th><th>Amount</th><th>Months</th><th>Maturity Value</th><th>Status</th><th></th></tr>
  @for (i of list; track i.id) { <tr><td>{{i.type}}</td><td>{{i.amount}}</td><td>{{i.months}}</td><td>{{i.maturityValue}}</td><td><span class="badge" [class.warn]="i.status === 'CLOSED'">{{i.status || 'ACTIVE'}}</span></td><td>@if (i.status !== 'CLOSED') { <button style="margin:0;padding:5px 12px" (click)="close(i.id)">Close early</button> }</td></tr> }</table></div>` })
export class Invest extends Base { list: any[] = [];
  get invested() { return this.list.filter(i => i.status !== 'CLOSED').reduce((s, i) => s + +i.amount, 0); } get maturity() { return this.list.filter(i => i.status !== 'CLOSED').reduce((s, i) => s + +i.maturityValue, 0); }
  constructor() { super(); this.f.type = 'FD'; this.loadAccounts(); this.ld(); } ld() { this.api.get('/investments').subscribe(l => this.list = l); }
  close(id: number) { this.done(this.api.post('/investments/' + id + '/close'), 'Closed', () => { this.ld(); this.loadAccounts(); }); }
  open() { this.done(this.api.post('/investments', this.f), 'FD opened', () => { this.ld(); this.loadAccounts(); }); } }
// Module 7: Compliance, Audit & Notification (admin)
@Component({ standalone: true, imports: M, selector: 'p-admin', template: `<div class="hero slim g3"><div><small>Compliance & Audit</small><h1>🛡️ Admin Control Centre</h1><p>Monitor activity, fraud alerts and locked accounts.</p></div></div><div class="stats"><div class="stat"><span>Total Events</span><b>{{logs.length}}</b></div><div class="stat"><span>Failed Logins</span><b>{{failed}}</b></div><div class="stat"><span>High-value Alerts</span><b>{{alerts}}</b></div></div><div class="card"><h3>Freeze / Unlock User</h3><input placeholder="Username" [(ngModel)]="f.u">
  <button (click)="unlock()">Unlock</button> <button style="background:#b03030" (click)="freeze()">Freeze</button><p class="err">{{err}}</p><p class="ok">{{msg}}</p></div>
  <div class="card"><h3>Loan Applications</h3><table><tr><th>ID</th><th>Type</th><th>Amount</th><th>EMI</th><th>Status</th><th>Action</th></tr>
  @for (l of loanList; track l.id) { <tr><td>#{{l.id}}</td><td>{{l.type}}</td><td>{{l.principal}}</td><td>{{l.emi}}</td><td><span class="badge" [class.warn]="l.status !== 'APPROVED'">{{l.status}}</span></td>
   <td><button style="margin:0;padding:5px 10px" (click)="decide(l.id,'APPROVED')">Approve</button> <button style="margin:0;padding:5px 10px;background:#b03030" (click)="decide(l.id,'REJECTED')">Reject</button></td></tr> }</table></div>
  <div class="card"><h3>Audit Trail & Fraud Alerts</h3><table><tr><th>Time</th><th>User</th><th>Action</th></tr>
  @for (a of logs; track a.id) { <tr [style.color]="a.action.startsWith('HIGH') || a.action == 'LOGIN_FAILED' ? '#b03030' : ''"><td>{{a.loggedAt | date:'medium'}}</td><td>{{a.username}}</td><td>{{a.action}}</td></tr> }</table></div>` })
export class Admin extends Base { logs: any[] = []; loanList: any[] = [];
  get failed() { return this.logs.filter(a => a.action === 'LOGIN_FAILED').length; } get alerts() { return this.logs.filter(a => a.action.startsWith('HIGH')).length; }
  constructor() { super(); this.ld(); } ld() { this.api.get('/admin/audit').subscribe(l => this.logs = l); this.api.get('/admin/loans').subscribe(l => this.loanList = l); }
  unlock() { this.done(this.api.post('/admin/unlock/' + this.f.u), 'Unlocked', () => this.ld()); }
  freeze() { this.done(this.api.post('/admin/freeze/' + this.f.u), 'Frozen', () => this.ld()); }
  decide(id: number, st: string) { this.done(this.api.post('/admin/loans/' + id + '/' + st), st, () => this.ld()); } }
