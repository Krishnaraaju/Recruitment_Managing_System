# Campus Recruitment & Internship Management System

A production-style Campus Recruitment platform built entirely with Spring Boot 3, Spring Security 6 (JWT), and MySQL 8.

## Resume Points

- Designed and developed a Campus Recruitment Platform using Spring Boot 3, Spring Security 6, and JWT.
- Implemented state-of-the-art stateless authentication with JWT (`jjwt`), enabling secure access for 3 distinct user roles (STUDENT, RECRUITER, ADMIN).
- Built a RESTful API containing over 50 endpoints with pagination, dynamic sorting, filtering, and robust validation.
- Engineered a normalized, scalable MySQL schema comprising 13 tables utilizing Spring Data JPA, soft delete mechanisms, and automatic auditing fields.
- Documented entire API surface area using Swagger/OpenAPI 3 dynamically served via Springdoc.
- Achieved a highly decoupled layered architecture by implementing DTO-to-Entity mapping, global exception handlers, and isolated service facades.
- Added file upload mechanisms allowing students to securely submit resumes to local storage.

## Features

- **Students**: Create profiles, list skills, upload resume copies, search job posts, apply to jobs, track application statuses.
- **Recruiters**: Tie accounts to companies, post new job/internship listings, review candidates, explicitly update application status (Shortlist/Reject), schedule interviews.
- **Admin**: Approve recruiter accounts, manage application wide dictionaries (categories, skills), ban users, monitor platform health stats via a central dashboard.
- **System**: Automatic notifications on interview/application updates (Email integration ready + local UI notifications).

## Stack

- **Backend**: Java 17, Spring Boot 3.2.x, Spring Data JPA, Hibernate, JWT.
- **Database**: H2 (In-memory, dev profile) and MySQL 8 (prod profile).
- **Tooling**: Maven, Lombok, Swagger3, Docker.

## How to Run Locally

### Approach 1: Using local H2 Database (Easiest)

1. Make sure you have Java 17 and Maven installed.
2. Open terminal inside the project root directory.
3. Run: `mvn spring-boot:run`
4. This runs with the default `dev` profile. Data is stored in-memory and will be lost on restart.

### Approach 2: Using Docker & MySQL (Production Like)

1. Ensure Docker Desktop is running.
2. Open terminal inside the root directory and build the jars:
   ```bash
   mvn clean install -DskipTests
   ```
3. Boot the environment via Docker Compose:
   ```bash
   docker-compose up -d
   ```
This will start a MySQL 8 container and a container for the Spring Boot app. The database will persist to the Docker volume.

## API Documentation

Once the app is running (on port `8080`), you can interact with all the API endpoints directly from your browser. 

Navigate to:
> [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**Note**: To authenticate requests in Swagger, first use the `/api/auth/login` endpoint perfectly, copy the `accessToken` from the response, and then click **"Authorize"** at the top top of Swagger UI and format as: `Bearer <your-token>`.

### Default Accounts seeded in Dev/Prod modes:

- **Admin Account**: `admin@campus.com` / `Admin@123`
