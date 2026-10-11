package com.bankease;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; import java.time.LocalDateTime;
interface UserRepo extends JpaRepository<AppUser,Long>{ Optional<AppUser> findByUsername(String u); }
interface AccountRepo extends JpaRepository<Account,Long>{ List<Account> findByOwnerId(Long o); Optional<Account> findByAccountNo(String n); }
interface BenRepo extends JpaRepository<Beneficiary,Long>{ List<Beneficiary> findByOwnerId(Long o); }
interface TxnRepo extends JpaRepository<Txn,Long>{ List<Txn> findByFromAccInOrToAccInOrderByCreatedAtDesc(Collection<String> a, Collection<String> b); List<Txn> findByFromAccAndCreatedAtAfter(String a, LocalDateTime t); }
interface BillerRepo extends JpaRepository<Biller,Long>{}
interface BillRepo extends JpaRepository<BillPayment,Long>{ List<BillPayment> findByAccountNoIn(Collection<String> a); }
interface LoanRepo extends JpaRepository<Loan,Long>{ List<Loan> findByOwnerId(Long o); }
interface InvRepo extends JpaRepository<Investment,Long>{ List<Investment> findByOwnerId(Long o); }
interface AuditRepo extends JpaRepository<AuditLog,Long>{ List<AuditLog> findAllByOrderByLoggedAtDesc(); }
interface NotifRepo extends JpaRepository<Notification,Long>{ List<Notification> findTop10ByOwnerIdOrderBySentAtDesc(Long o); }
