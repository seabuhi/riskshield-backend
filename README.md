🏦 RiskShield Backend

A production-ready FinTech backend platform built with Spring Boot, designed for secure credit, risk, and user management systems.


![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Caching-red?style=flat-square&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)
![JWT](https://img.shields.io/badge/Auth-JWT-red?style=flat-square)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-brightgreen?style=flat-square&logo=swagger)






🚀 Overview

RiskShield is a modular backend system that simulates a real-world financial platform, including:

Secure authentication & authorization
OTP-based verification
Credit scoring engine
Fraud detection system
Redis-based rate limiting & idempotency
Audit logging with AOP
Notification system with retry
Document management
Admin control panel
Analytics dashboard
🏗 Architecture
API Gateway Layer
  ├── Rate Limiter (Redis)
  ├── Idempotency (Redis)
  ├── JWT Security

Controller Layer
  ├── Auth / OTP
  ├── Credit / Fraud
  ├── Admin / Analytics

Service Layer
  ├── Business Logic
  ├── Scoring Engine
  ├── Fraud Engine

Data Layer
  ├── PostgreSQL (JPA)
  ├── Redis (cache + idempotency)
🔥 Key Features
✔ JWT Authentication & Role-based Authorization
✔ OTP Verification (hashed storage)
✔ Credit Scoring Engine
✔ Fraud Detection System
✔ Redis-based Rate Limiting
✔ Redis-based Idempotency
✔ Audit Logging (AOP)
✔ Notification System with Retry
✔ File Upload & Document Management
✔ Admin Panel APIs
✔ Analytics Dashboard
✔ Correlation ID for tracing
✔ Feature Toggle system
📦 API Modules
Module	Description
Auth	Registration, login, JWT, refresh tokens
OTP	Email verification with expiration
Credit	Risk scoring & eligibility
Fraud	Transaction analysis & blacklist
Notification	Email + retry mechanism
Audit	AOP-based activity tracking
Documents	File upload/download
Admin	User & role management
Analytics	Dashboard metrics
Rate Limit	Redis-based protection
🛠 Tech Stack
Technology	Usage
Java 17	Core language
Spring Boot 3.2	Backend framework
Spring Security	Auth & access control
JWT	Stateless authentication
PostgreSQL	Main database
Redis	Caching, rate limiting, idempotency
Docker	Containerization
Spring AOP	Audit logging
Spring Mail	Email service
Actuator	Monitoring
Swagger	API documentation
🐳 Run with Docker
docker-compose up --build
⚙️ Environment Variables
DB_URL=jdbc:postgresql://localhost:5432/riskshield
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_secret_key

MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=your@email.com
MAIL_PASSWORD=your_app_password
📖 API Docs

Swagger UI:

http://localhost:8080/swagger-ui.html
🔒 Security
JWT-based authentication
Role-based access control (RBAC)
Redis-backed rate limiting
Redis-backed idempotency
Hashed OTP & refresh tokens
Audit logging with correlation ID
📁 Project Structure
com.seabuhi.riskshield
 ├── common
 ├── config
 ├── module
 ├── security
💡 Why This Project?

This project demonstrates:

✔ Real-world backend architecture
✔ Security best practices
✔ Distributed system thinking (Redis)
✔ Clean code & modular design
✔ Production-ready mindset
📄 License

MIT License

👨‍💻 Author

Seabuhi Khazimzade