# BankEase – NextGen Net Banking (Java · Spring Boot · Angular · MySQL)
## Run
1. MySQL running on localhost:3306 (edit user/password in backend/src/main/resources/application.properties). DB `bankease` is auto-created.
2. Backend (JDK 17+, Maven):  cd backend && mvn spring-boot:run   -> http://localhost:8080
3. Frontend (Node 18+):       cd frontend && npm install && npm start -> http://localhost:4200
Demo admin: admin / admin123. New users get a SAVINGS account with 10000 opening balance.
## Modules
1 Auth (JWT, BCrypt, lock after 3 fails) · 2 Accounts/Statement · 3 Beneficiaries & Transfers (ACID, limits, fraud flag >1,00,000)
4 Bill Pay · 5 Loans (EMI) · 6 Fixed Deposits · 7 Admin audit trail & unlock. Design doc: docs/BankEase_System_Design.docx
