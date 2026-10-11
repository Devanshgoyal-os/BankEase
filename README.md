
# BankEase – NextGen Net Banking

A full-stack banking simulation built with **Java, Spring Boot, Angular, and MySQL**. BankEase demonstrates common online banking workflows through a web-based interface.

## Overview

BankEase is a B.Tech CSE project designed to demonstrate full-stack development, REST API integration, relational database management, authentication, and banking-style transaction workflows.

## Features

- **Authentication & Security** — Registration, login, JWT authentication, OTP verification, and account lockout after repeated failed login attempts.
- **Account Management** — View accounts, account balances, and transaction statements.
- **Fund Transfers** — Manage beneficiaries, transfer funds, validate balances and transaction limits, and view transaction records.
- **Bill Payments** — Pay bills and review payment history.
- **Loan Services** — Submit loan applications and calculate EMIs.
- **Investment Services** — Simulated fixed deposits and recurring deposits with maturity calculations.
- **Dashboard & Notifications** — View banking activity and in-app notifications.
- **Admin & Audit** — Review audit logs, manage account lockouts, and process loan applications.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular, TypeScript, CSS |
| Backend | Java, Spring Boot |
| API | REST APIs |
| Database | MySQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT, BCrypt |
| Build Tools | Maven, npm |

## Project Structure

```text
bankease/
├── backend/
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── package.json
│   └── src/
├── docs/
│   └── BankEase_System_Design.docx
├── .gitignore
└── README.md
```

## Prerequisites

Install the following before running the project:

- Java JDK 17 or compatible version
- Maven
- Node.js and npm
- MySQL Server
- Visual Studio Code (VS Code)

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/Devanshgoyal-os/BankEase.git
cd BankEase
```

### 2. Configure MySQL

Start MySQL Server and create the database:

```sql
CREATE DATABASE bankease;
```

Open `backend/src/main/resources/application.properties` and configure your local database username and password. Keep credentials private and never commit real passwords or production secrets.

### 3. Start the backend

Open a terminal in the project root:

```bash
cd backend
mvn spring-boot:run
```

The Spring Boot backend runs at:

`http://localhost:8080`

### 4. Start the frontend

Open a second terminal in the project root:

```bash
cd frontend
npm install
npm start
```

Open the application in your browser:

`http://localhost:4200`

Keep both terminals and the MySQL Server running while using the application.

## Security Notes

- This project is an educational banking simulation.
- OTP delivery is simulated for local development.
- Do not use real banking credentials or sensitive customer data.
- Review authentication, authorization, transaction handling, and configuration before any deployment.
- Never publish database passwords, JWT signing secrets, or other credentials.

## Documentation

See `docs/BankEase_System_Design.docx` for the project's system design documentation.

## Future Improvements

- Automated backend and frontend testing
- Improved transaction validation and error handling
- Responsive UI refinements and dashboard analytics
- Secure environment-based configuration
- Deployment with HTTPS and production-grade security

## Author

**Devansh Goyal**

- GitHub: [Devanshgoyal-os](https://github.com/Devanshgoyal-os)
- LinkedIn: [Devansh Goyal](https://www.linkedin.com/in/devanshgoyal-cse)

---

*Built as a full-stack B.Tech CSE project to explore Java backend development, Angular frontend development, and MySQL database integration.*
