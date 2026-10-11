package com.bankease;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootApplication
public class BankEaseApplication {
  public static void main(String[] a){ SpringApplication.run(BankEaseApplication.class,a); }
  @Bean CommandLineRunner seed(UserRepo users, BillerRepo billers, PasswordEncoder enc){ return x -> {
    if(users.findByUsername("admin").isEmpty()){ AppUser u=new AppUser(); u.setUsername("admin"); u.setPasswordHash(enc.encode("admin123")); u.setFullName("Bank Admin"); u.setEmail("admin@bankease.com"); u.setRole("ADMIN"); users.save(u); }
    if(billers.count()==0) for(String[] b:new String[][]{{"State Electricity Board","Electricity"},{"Jio Mobile","Mobile"},{"Tata Play","DTH"},{"Credit Card Payment","Card"}}){ Biller bl=new Biller(); bl.setName(b[0]); bl.setCategory(b[1]); billers.save(bl); }
  };}
}
