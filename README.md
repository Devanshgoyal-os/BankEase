# BankEase – NextGen Net Banking

A Full-Stack Banking Simulation built using **Java, Spring Boot, Angular, and MySQL**.

BankEase is a web-based banking application developed as a B.Tech CSE project to demonstrate full-stack development, REST API integration, database management, authentication, and banking-style transaction workflows.

## Overview

BankEase – NextGen Net Banking provides a simulated online banking experience through a user-friendly web interface. It demonstrates how modern banking applications manage accounts, transactions, payments, loans, investments, and administrative operations.

The project follows a layered architecture with Angular on the frontend, Spring Boot on the backend, and MySQL for relational data storage.

## Features

### 1. Authentication & Security
- User registration and login.
- JWT-based authentication.
- BCrypt password encryption.
- OTP verification for authentication workflows.
- Account lockout after repeated failed login attempts.

### 2. Account Management
- View registered bank accounts.
- Check available account balances.
- Access transaction statements.
- Monitor account activity.

### 3. Fund Transfers
- Add and manage beneficiaries.
- Transfer funds between accounts.
- Validate available balance and transaction limits.
- Maintain transaction records.
- View transfer history.

### 4. Bill Payments
- Simulated online bill payments.
- Manage payment transactions.
- View bill payment history.

### 5. Loan Services
- Submit loan applications.
- Calculate Equated Monthly Installments (EMI).
- Track loan application status.
- Admin-side loan application processing.

### 6. Investment Services
- Simulated Fixed Deposits (FD).
- Simulated Recurring Deposits (RD).
- Calculate investment maturity amounts.
- View investment details.

### 7. Dashboard & Notifications
- Banking activity overview.
- Account and transaction summaries.
- In-app notifications.

### 8. Admin & Audit
- Review audit logs.
- Manage account lockouts.
- Process loan applications.
- Monitor administrative activities.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular, TypeScript, HTML, CSS |
| Backend | Java, Spring Boot |
| API | REST APIs |
| Database | MySQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT, BCrypt |
| Build Tools | Maven, npm |
| IDE | Visual Studio Code |

## Project Architecture

The application follows a three-layer architecture:

1. **Frontend:** Angular handles the user interface and communicates with backend APIs.
2. **Backend:** Spring Boot manages business logic, authentication, and transaction workflows.
3. **Database:** MySQL stores user, account, transaction, payment, and other application data.

## Project Structure

```text
BankEase/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           └── resources/
│
├── frontend/
│   ├── package.json
│   └── src/
│
├── docs/
│   └── BankEase_System_Design.docx
│
├── .gitignore
└── README.md
```

## Prerequisites

Install the following software before running the application:

- Java JDK (version compatible with the backend configuration).
- Apache Maven.
- Node.js and npm.
- MySQL Server.
- Visual Studio Code (VS Code).

## Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/Devanshgoyal-os/BankEase.git
cd BankEase
```

### Step 2: Configure MySQL

Start your MySQL Server and create the database:

```sql
CREATE DATABASE bankease;
```

Open:

`backend/src/main/resources/application.properties`

Configure your local database username, password, and other required application properties.

Keep all credentials and JWT secrets private. Never commit actual passwords or production secrets.

### Step 3: Start the Backend

Open a terminal in the project directory:

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

`http://localhost:8080`

### Step 4: Start the Frontend

Open a second terminal in the project directory:

```bash
cd frontend
npm install
npm start
```

Frontend URL:

`http://localhost:4200`

Keep the backend, frontend, and MySQL Server running while using the application.

## Application Screenshots

Screenshots of the application can be added here to demonstrate the user interface and major banking workflows.

### Login Page

![Login Page](docs/screenshots/login.png)



![Dashboard](docs/screenshots/dashboard.png)

### Account Management

![Account Management](docs/screenshots/accounts.png)

### Fund Transfer

![Fund Transfer](docs/screenshots/transfer.png)

### Bill Payments

![Bill Payments](docs/screenshots/payments.png)

### Loan Services

![Loan Services](docs/screenshots/loans.png)

*Replace these sample image paths with actual screenshots added to the repository.*

## Documentation

Detailed system design documentation is available in:

`docs/BankEase_System_Design.docx`

It includes the project's architecture, workflow, database design, and module descriptions.

## Security Notes

- BankEase is an educational banking simulation, not a real banking platform.
- OTP delivery is simulated for local development.
- Do not use real banking credentials or sensitive customer information.
- Database passwords and JWT signing secrets must never be exposed publicly.
- Authentication, authorization, and transaction handling should be reviewed before deployment.
- Production deployment requires HTTPS and additional security hardening.

## Future Improvements

- Automated backend and frontend testing.
- Improved transaction validation and error handling.
- Enhanced responsive UI and dashboard analytics.
- Secure environment-based configuration.
- Improved logging and monitoring.
- Cloud deployment with HTTPS.
- Additional security and performance optimizations.

## Author

**Devansh Goyal**

- GitHub: [Devanshgoyal-os](https://github.com/Devanshgoyal-os)
- LinkedIn: [Devansh Goyal](https://www.linkedin.com/in/devanshgoyal-cse)

---

*Built as a Full-Stack B.Tech CSE project to explore Java backend development, Angular frontend development, REST API integration, and MySQL database management.*
