package com.bankease;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Data class AppUser { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(unique=true) String username; String passwordHash; String fullName, email, role="CUSTOMER", otp; int failedAttempts; boolean locked; LocalDateTime otpExpiry; }
@Entity @Data class Account { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(unique=true) String accountNo; Long ownerId; String type, ifsc; BigDecimal balance; }
@Entity @Data class Beneficiary { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; Long ownerId; String name, accountNo, ifsc; }
@Entity @Data class Txn { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; String fromAcc, toAcc, mode, status, refNo; BigDecimal amount; LocalDateTime createdAt; }
@Entity @Data class Biller { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; String name, category; }
@Entity @Data class BillPayment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; String accountNo, consumerNo; Long billerId; BigDecimal amount; LocalDateTime paidOn; }
@Entity @Data class Loan { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; Long ownerId; String type, status; BigDecimal principal, rate, emi; int months; }
@Entity @Data class Investment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; Long ownerId; String type, accountNo, status; BigDecimal amount, maturityValue; int months; }
@Entity @Data @NoArgsConstructor @AllArgsConstructor class AuditLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; String username, action; LocalDateTime loggedAt; }
@Entity @Data @NoArgsConstructor @AllArgsConstructor class Notification { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; Long ownerId; String message; LocalDateTime sentAt; }
