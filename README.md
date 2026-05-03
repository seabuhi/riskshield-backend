# 🏦 Sea-Credit Enterprise Backend API

> A professional-grade, production-ready banking backend built with **Spring Boot 3.2** featuring 10 comprehensive API modules.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/Auth-JWT-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-brightgreen?style=flat-square&logo=swagger)

---

## 📋 Table of Contents
- [Architecture](#-architecture)
- [API Modules](#-api-modules)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Project Structure](#-project-structure)

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    API Gateway Layer                      │
│  ┌─────────────┐ ┌─────────────┐ ┌──────────────────┐  │
│  │ Rate Limiter │ │ JWT Filter  │ │ CORS / Security  │  │
│  └─────────────┘ └─────────────┘ └──────────────────┘  │
├─────────────────────────────────────────────────────────┤
│                   Controller Layer                       │
│  Auth │ OTP │ Notification │ Credit │ Fraud │ Audit     │
│  Document │ Admin │ Analytics                            │
├─────────────────────────────────────────────────────────┤
│                    Service Layer                         │
│  Business Logic │ Scoring Engine │ Fraud Engine          │
├─────────────────────────────────────────────────────────┤
│                  Repository / Data Layer                 │
│  JPA │ PostgreSQL │ File System                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 API Modules

| # | Module | Endpoints | Description |
|---|--------|-----------|-------------|
| 1 | **Auth** | `/auth/**` | Register, Login, Logout, Refresh Token, Me, Change Password |
| 2 | **OTP** | `/otp/**` | Send OTP, Verify OTP, Resend OTP, Expire Time |
| 3 | **Notification** | `/api/v1/notifications/**` | Send Email, History, Failed Retry |
| 4 | **Credit Scoring** | `/api/v1/credit-scoring/**` | Calculate Score, Risk Level, Loan Eligibility |
| 5 | **Fraud Detection** | `/api/v1/fraud/**` | Transaction Analysis, Blacklist, Alert Management |
| 6 | **Audit Log** | `/api/v1/audit-logs/**` | Who Did What, When, From Where, Before/After Data |
| 7 | **File Upload** | `/api/v1/documents/**` | Upload, Download, Delete, Type Validation |
| 8 | **Admin Panel** | `/api/v1/admin/**` | List Users, Block, Change Role, Activate/Deactivate |
| 9 | **Rate Limiting** | Built-in Filter | Login: 5/min, OTP: 3/5min, General: 10/min |
| 10 | **Dashboard Analytics** | `/api/v1/analytics/**` | User Stats, Fraud Stats, Notification Stats |

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 17** | Core language |
| **Spring Boot 3.2** | Application framework |
| **Spring Security** | Authentication & Authorization |
| **JWT (jjwt 0.12)** | Stateless token auth |
| **Spring Data JPA** | Database ORM |
| **PostgreSQL** | Production database |
| **Redis** | Distributed Idempotency & Caching |
| **Spring Boot Actuator** | Production Monitoring (Health, Metrics) |
| **Spring AOP** | Audit logging |
| **Spring Mail** | Email notifications |
| **SpringDoc OpenAPI** | Swagger UI documentation |
| **Lombok** | Boilerplate reduction |
| **H2** | Test database |

---

## 🏁 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone & Configure

```bash
git clone https://github.com/YOUR_USERNAME/sea-credit-backend.git
cd sea-credit-backend
```

### 2. Create Database

```sql
CREATE DATABASE seacredit;
```

### 3. Set Environment Variables

```bash
export DB_URL=jdbc:postgresql://localhost:5432/seacredit
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key_min_64_chars
export MAIL_HOST=smtp.gmail.com
export MAIL_USERNAME=your@email.com
export MAIL_PASSWORD=your_app_password
```

### 4. Run

```bash
./mvnw spring-boot:run
```

### 5. Access

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/v3/api-docs |
| Default Admin | Loaded from environment variables |

---

## 📖 API Documentation

### Auth Module
```
POST /auth/signup          → Register new user
POST /auth/login           → Login & get tokens
POST /auth/refresh         → Refresh access token
POST /auth/logout          → Logout & revoke tokens
GET  /auth/me              → Get current user info
POST /auth/change-password → Change password
```

### OTP Module
```
POST /otp/send             → Send OTP to email
POST /otp/verify           → Verify OTP code
POST /otp/resend           → Resend OTP
GET  /otp/expire-time      → Get OTP expiry time
```

### Credit Scoring Module
```
POST /api/v1/credit-scoring/calculate    → Calculate credit score
GET  /api/v1/credit-scoring/risk-levels  → Get risk level definitions
```

### Fraud Detection Module
```
POST   /api/v1/fraud/analyze              → Analyze transaction risk
GET    /api/v1/fraud/alerts               → Get open fraud alerts
GET    /api/v1/fraud/alerts/user/{id}     → Get user's fraud alerts
PATCH  /api/v1/fraud/alerts/{id}/resolve  → Resolve alert
POST   /api/v1/fraud/blacklist            → Add to blacklist
DELETE /api/v1/fraud/blacklist/{id}       → Remove from blacklist
```

### Admin Module
```
GET    /api/v1/admin/users             → List all users (paginated)
GET    /api/v1/admin/users/{id}        → Get user details
PATCH  /api/v1/admin/users/{id}/block  → Block user
PATCH  /api/v1/admin/users/{id}/activate → Activate user
PATCH  /api/v1/admin/users/{id}/role   → Change user role
DELETE /api/v1/admin/users/{id}        → Delete user
```

---

## 🔒 Security

| Feature | Implementation |
|---------|---------------|
| **Authentication** | JWT Bearer tokens (1h access, 24h refresh) |
| **Authorization** | Role-based: ADMIN, ANALYST, CLIENT |
| **Rate Limiting** | Per-IP, per-endpoint (Bucket4j) |
| **Fraud Detection** | Real-time IP/Email blacklist, transaction analysis |
| **Audit Trail** | AOP-based automatic logging with IP tracking |
| **Password** | BCrypt hashing |
| **OTP** | 6-digit codes, 15-min expiry, purpose-scoped |

---

## 📁 Project Structure

```
src/main/java/com/seabuhi/seacredit/
├── common/
│   ├── exception/          # BusinessException, GlobalExceptionHandler
│   ├── model/              # BaseEntity (auto-timestamps)
│   └── response/           # ApiResponse<T> wrapper
├── config/
│   ├── AsyncConfig         # Async email sending
│   ├── DataInitializer     # Seed roles & admin user
│   ├── JpaConfig           # JPA Auditing
│   ├── SchedulingConfig    # Scheduled tasks
│   ├── SecurityConfig      # Spring Security + JWT
│   └── SwaggerConfig       # OpenAPI documentation
├── module/
│   ├── admin/              # Admin user management
│   ├── analytics/          # Dashboard statistics
│   ├── assessment/         # Credit scoring engine
│   ├── audit/              # Audit logs + AOP aspect
│   ├── auth/               # Auth + OTP
│   ├── document/           # File upload/download
│   ├── fraud/              # Fraud detection engine
│   ├── notification/       # Email + retry mechanism
│   ├── ratelimit/          # Bucket4j rate limiting
│   └── user/               # User & Role entities
└── security/
    ├── CustomUserDetailsService
    ├── JwtAuthenticationFilter
    ├── JwtTokenProvider
    └── UserPrincipal
```

---

## 📄 License

MIT License — free to use for personal and commercial projects.

---

> Built with ❤️ for the developer community
