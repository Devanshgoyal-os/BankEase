package com.bankease;
import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.Map;
/** Module 1: Authentication & Security */
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
class AuthController {
  final UserRepo users; final AccountRepo accs; final PasswordEncoder enc; final JwtUtil jwt; final AuditRepo audit;
  @PostMapping("/register") Map<String,String> register(@RequestBody Map<String,String> b){
    if(b.get("username")==null||b.get("password")==null||b.get("password").length()<6) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username and password (min 6 chars) required");
    if(users.findByUsername(b.get("username")).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username already taken");
    AppUser u=new AppUser(); u.setUsername(b.get("username")); u.setPasswordHash(enc.encode(b.get("password"))); u.setFullName(b.get("fullName")); u.setEmail(b.get("email")); u=users.save(u);
    Account a=new Account(); a.setOwnerId(u.getId()); a.setAccountNo(String.valueOf(1000000000L+u.getId()*7919)); a.setType("SAVINGS"); a.setBalance(new BigDecimal("10000")); a.setIfsc("BEAS0000001"); accs.save(a);
    audit.save(new AuditLog(null,u.getUsername(),"REGISTER",LocalDateTime.now()));
    return Map.of("message","Registered. Your account no: "+a.getAccountNo()+" (opening balance 10000)"); }
  @PostMapping("/login") Map<String,Object> login(@RequestBody Map<String,String> b){
    AppUser u=users.findByUsername(b.get("username")).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));
    if(u.isLocked()) throw new ResponseStatusException(HttpStatus.LOCKED,"Account locked after 3 failed attempts. Contact admin.");
    if(!enc.matches(b.get("password"),u.getPasswordHash())){ u.setFailedAttempts(u.getFailedAttempts()+1); if(u.getFailedAttempts()>=3) u.setLocked(true); users.save(u);
      audit.save(new AuditLog(null,u.getUsername(),"LOGIN_FAILED",LocalDateTime.now())); throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"); }
    String otp=String.valueOf(100000+new java.util.Random().nextInt(900000)); u.setFailedAttempts(0); u.setOtp(otp); u.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); users.save(u);
    System.out.println(">>> [SMS SIMULATION] OTP for "+u.getUsername()+" is "+otp);
    return Map.of("otpRequired",true,"demoOtp",otp); }
  @PostMapping("/verify-otp") Map<String,Object> verify(@RequestBody Map<String,String> b){
    AppUser u=users.findByUsername(b.get("username")).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid request"));
    if(u.getOtp()==null||u.getOtpExpiry().isBefore(LocalDateTime.now())||!u.getOtp().equals(b.get("otp"))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired OTP");
    u.setOtp(null); users.save(u); audit.save(new AuditLog(null,u.getUsername(),"LOGIN",LocalDateTime.now()));
    return Map.of("token",jwt.make(u.getUsername(),u.getRole()),"role",u.getRole(),"name",u.getFullName()==null?u.getUsername():u.getFullName()); }
}
