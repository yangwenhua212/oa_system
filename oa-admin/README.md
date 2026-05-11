# OA System - Backend

## Technology Stack
- **Framework**: Spring Boot 4.0.6
- **Java Version**: JDK 21
- **ORM**: MyBatis-Plus 3.5.9
- **Security**: Spring Security + JWT
- **Database**: MySQL 8.0

## Project Structure
```
oa-admin/
├── src/main/java/com/oa/
│   ├── OaAdminApplication.java      # Entry Point
│   ├── common/                      # Common Classes
│   │   ├── R.java                   # API Response
│   │   ├── PageDTO.java             # Page Request
│   │   └── PageR.java               # Page Response
│   ├── entity/                       # Entity Classes
│   │   ├── BaseEntity.java
│   │   ├── User.java
│   │   ├── Dept.java
│   │   ├── Role.java
│   │   └── Menu.java
│   ├── mapper/                       # MyBatis Mappers
│   │   ├── UserMapper.java
│   │   ├── DeptMapper.java
│   │   ├── RoleMapper.java
│   │   └── MenuMapper.java
│   ├── service/                      # Business Logic
│   │   ├── UserService.java
│   │   ├── DeptService.java
│   │   └── impl/
│   ├── controller/                   # API Controllers
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── DeptController.java
│   │   └── RoleController.java
│   └── security/                     # Security Configuration
│       ├── JwtTokenUtil.java
│       ├── JwtAuthenticationFilter.java
│       └── SecurityConfig.java
└── src/main/resources/
    └── application.yml              # Application Configuration
```

## Quick Start

### 1. Initialize Database
```bash
mysql -u root -p < ../database/init.sql
```

### 2. Build Project
```bash
mvn clean install
```

### 3. Run Application
```bash
mvn spring-boot:run
```

### 4. Access API
- Base URL: http://localhost:8080/api
- Login: POST /auth/login
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```

## Default Account
- Username: admin
- Password: admin123
