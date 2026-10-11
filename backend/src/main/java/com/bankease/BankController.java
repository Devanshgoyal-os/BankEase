package com.bankease;
import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
import java.math.*; import java.security.Principal; import java.time.*; import java.util.*;
/** Modules 2-7: Accounts, Fund Transfer, Bill Pay, Loans, Investments, Admin/Audit */
@RestController @RequestMapping("/api") @RequiredArgsConstructor @Transactional
class BankController {
  final UserRepo users; final AccountRepo accs; final BenRepo bens; final TxnRepo txns; final BillerRepo billers; final BillRepo bills; final LoanRepo loans; final InvRepo invs; final AuditRepo audit; final NotifRepo notifs;
  AppUser me(Principal p){ return users.findByUsername(p.getName()).orElseThrow(); }
  void log(Principal p,String a){ audit.save(new AuditLog(null,p.getName(),a,LocalDateTime.now())); }
  ResponseStatusException bad(String m){ return new ResponseStatusException(HttpStatus.BAD_REQUEST,m); }
  void alert(Principal p,String m){ notifs.save(new Notification(null,me(p).getId(),m,LocalDateTime.now())); System.out.println(">>> [SMS/EMAIL SIMULATION] to "+p.getName()+": "+m); }
  Account debit(Principal p,String no,BigDecimal amt){
    if(amt==null||amt.signum()<=0) throw bad("Invalid amount");
    Account a=accs.findByAccountNo(no).filter(x->x.getOwnerId().equals(me(p).getId())).orElseThrow(()->bad("Invalid source account"));
    if(a.getBalance().compareTo(amt)<0) throw bad("Insufficient balance");
    a.setBalance(a.getBalance().subtract(amt)); return accs.save(a); }
  // ---- Module 2: Account Management
  @GetMapping("/accounts") List<Account> accounts(Principal p){ return accs.findByOwnerId(me(p).getId()); }
  @GetMapping("/statement") List<Txn> statement(Principal p){ List<String> n=accounts(p).stream().map(Account::getAccountNo).toList(); return txns.findByFromAccInOrToAccInOrderByCreatedAtDesc(n,n); }
  // ---- Module 3: Fund Transfer
  @GetMapping("/beneficiaries") List<Beneficiary> listBens(Principal p){ return bens.findByOwnerId(me(p).getId()); }
  @PostMapping("/beneficiaries") Beneficiary addBen(Principal p,@RequestBody Beneficiary b){
    if(b.getIfsc()==null||!b.getIfsc().matches("[A-Z]{4}0[A-Z0-9]{6}")) throw bad("Invalid IFSC (format: ABCD0123456)");
    b.setId(null); b.setOwnerId(me(p).getId()); log(p,"ADD_BENEFICIARY"); return bens.save(b); }
  @PostMapping("/transfers") Txn transfer(Principal p,@RequestBody Map<String,String> b){
    BigDecimal amt=new BigDecimal(b.get("amount")); String to=b.get("toAcc");
    if(to==null||to.equals(b.get("fromAcc"))) throw bad("Invalid destination account");
    if(amt.compareTo(new BigDecimal("200000"))>0) throw bad("Exceeds per-transaction limit of 200000");
    List<Txn> today=txns.findByFromAccAndCreatedAtAfter(b.get("fromAcc"),LocalDate.now().atStartOfDay());
    if(today.stream().map(Txn::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add).add(amt).compareTo(new BigDecimal("500000"))>0) throw bad("Daily limit of 500000 exceeded");
    boolean fast=today.stream().filter(x->x.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(10))).count()>=3;
    debit(p,b.get("fromAcc"),amt);
    accs.findByAccountNo(to).ifPresent(t->{ t.setBalance(t.getBalance().add(amt)); accs.save(t); });
    Txn t=new Txn(); t.setFromAcc(b.get("fromAcc")); t.setToAcc(to); t.setAmount(amt); t.setMode(b.getOrDefault("mode","IMPS")); t.setRefNo(UUID.randomUUID().toString().substring(0,12).toUpperCase()); t.setCreatedAt(LocalDateTime.now());
    boolean hi=amt.compareTo(new BigDecimal("100000"))>0||fast; t.setStatus(hi?"SUCCESS-FLAGGED":"SUCCESS"); if(hi) log(p,"HIGH_VALUE_ALERT "+t.getRefNo());
    log(p,"TRANSFER "+t.getRefNo()); alert(p,"Debited Rs "+amt+" via "+t.getMode()+". Ref "+t.getRefNo()); return txns.save(t); }
  // ---- Module 4: Bill Payment
  @GetMapping("/billers") List<Biller> billers(){ return billers.findAll(); }
  @PostMapping("/bills") BillPayment pay(Principal p,@RequestBody Map<String,String> b){
    BigDecimal amt=new BigDecimal(b.get("amount")); debit(p,b.get("accountNo"),amt);
    BillPayment x=new BillPayment(); x.setAccountNo(b.get("accountNo")); x.setBillerId(Long.valueOf(b.get("billerId"))); x.setConsumerNo(b.get("consumerNo")); x.setAmount(amt); x.setPaidOn(LocalDateTime.now());
    log(p,"BILL_PAY"); alert(p,"Bill paid Rs "+amt+" (consumer "+x.getConsumerNo()+")"); return bills.save(x); }
  @GetMapping("/bills") List<BillPayment> billHistory(Principal p){ return bills.findByAccountNoIn(accounts(p).stream().map(Account::getAccountNo).toList()); }
  // ---- Module 5: Loan Services
  @GetMapping("/loans") List<Loan> myLoans(Principal p){ return loans.findByOwnerId(me(p).getId()); }
  @PostMapping("/loans") Loan applyLoan(Principal p,@RequestBody Map<String,String> b){
    BigDecimal pr=new BigDecimal(b.get("principal")); int n=Integer.parseInt(b.get("months")); String type=b.get("type");
    double rate=switch(type){ case "HOME"->8.5; case "CAR"->9.5; default->12.0; }; double r=rate/1200, f=Math.pow(1+r,n);
    Loan l=new Loan(); l.setOwnerId(me(p).getId()); l.setType(type); l.setPrincipal(pr); l.setMonths(n); l.setRate(BigDecimal.valueOf(rate));
    l.setEmi(BigDecimal.valueOf(pr.doubleValue()*r*f/(f-1)).setScale(2,RoundingMode.HALF_UP)); l.setStatus(pr.compareTo(new BigDecimal("1000000"))>0?"PENDING_REVIEW":"APPROVED");
    log(p,"LOAN_APPLY"); alert(p,type+" loan of Rs "+pr+" submitted. Status: "+l.getStatus()); return loans.save(l); }
  // ---- Module 6: Investment Services (FD @ 6.5% p.a.)
  @GetMapping("/investments") List<Investment> myInv(Principal p){ return invs.findByOwnerId(me(p).getId()); }
  @PostMapping("/investments") Investment invest(Principal p,@RequestBody Map<String,String> b){
    BigDecimal amt=new BigDecimal(b.get("amount")); int n=Integer.parseInt(b.get("months")); String type=b.getOrDefault("type","FD"); debit(p,b.get("accountNo"),amt);
    double a=amt.doubleValue(), mv;
    if(type.equals("FD")) mv=a*Math.pow(1.065,n/12.0); else if(type.equals("RD")) mv=a*n*(1+0.065*(n+1)/24.0); else { double r=0.01; mv=a*((Math.pow(1+r,n)-1)/r)*(1+r); }
    Investment i=new Investment(); i.setOwnerId(me(p).getId()); i.setType(type); i.setAmount(amt); i.setMonths(n); i.setAccountNo(b.get("accountNo")); i.setStatus("ACTIVE");
    i.setMaturityValue(BigDecimal.valueOf(mv).setScale(2,RoundingMode.HALF_UP)); log(p,type+"_OPEN"); alert(p,type+" opened. Maturity value Rs "+i.getMaturityValue()); return invs.save(i); }
  @PostMapping("/investments/{id}/close") Map<String,String> close(Principal p,@PathVariable Long id){
    Investment i=invs.findById(id).filter(x->x.getOwnerId().equals(me(p).getId())&&!"CLOSED".equals(x.getStatus())&&x.getAccountNo()!=null).orElseThrow(()->bad("Investment not found or already closed"));
    BigDecimal back=i.getAmount().multiply(new BigDecimal("0.99")).setScale(2,RoundingMode.HALF_UP);
    Account a=accs.findByAccountNo(i.getAccountNo()).orElseThrow(); a.setBalance(a.getBalance().add(back)); accs.save(a); i.setStatus("CLOSED"); invs.save(i);
    log(p,"PREMATURE_CLOSE #"+id); alert(p,"Investment #"+id+" closed early. Rs "+back+" credited (1% penalty)."); return Map.of("message","Closed. Rs "+back+" credited after 1% penalty"); }
  // ---- Extras: open account, notifications
  @PostMapping("/accounts/open") Account openAccount(Principal p,@RequestBody Map<String,String> b){
    Account a=new Account(); a.setOwnerId(me(p).getId()); a.setAccountNo(String.valueOf(2000000000L+(long)(Math.random()*899999999))); a.setType(b.getOrDefault("type","SAVINGS")); a.setBalance(BigDecimal.ZERO); a.setIfsc("BEAS0000001"); log(p,"ACCOUNT_OPEN"); return accs.save(a); }
  @GetMapping("/notifications") List<Notification> notifications(Principal p){ return notifs.findTop10ByOwnerIdOrderBySentAtDesc(me(p).getId()); }
  // ---- Module 7: Compliance, Audit (admin only)
  @GetMapping("/admin/audit") List<AuditLog> auditLog(){ return audit.findAllByOrderByLoggedAtDesc(); }
  @PostMapping("/admin/unlock/{username}") Map<String,String> unlock(@PathVariable String username){
    AppUser u=users.findByUsername(username).orElseThrow(()->bad("User not found")); u.setLocked(false); u.setFailedAttempts(0); users.save(u); return Map.of("message","Unlocked "+username); }
  @PostMapping("/admin/freeze/{username}") Map<String,String> freeze(@PathVariable String username){
    AppUser u=users.findByUsername(username).orElseThrow(()->bad("User not found")); u.setLocked(true); users.save(u); return Map.of("message","Frozen "+username); }
  @GetMapping("/admin/loans") List<Loan> allLoans(){ return loans.findAll(); }
  @PostMapping("/admin/loans/{id}/{status}") Map<String,String> decide(@PathVariable Long id,@PathVariable String status,Principal p){
    Loan l=loans.findById(id).orElseThrow(()->bad("Loan not found")); l.setStatus(status.toUpperCase()); loans.save(l); log(p,"LOAN_"+l.getStatus()+" #"+id);
    notifs.save(new Notification(null,l.getOwnerId(),"Your "+l.getType()+" loan #"+id+" is "+l.getStatus(),LocalDateTime.now())); return Map.of("message","Loan #"+id+" "+l.getStatus()); }
}
