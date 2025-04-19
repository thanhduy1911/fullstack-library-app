# 📚 Library App - Backend

A simple library management backend built using **Spring Boot 3.3.10**, migrated from version 2.7. Designed to support typical library features such as managing books, authors, borrowers, and lending records.

## 🚀 Tech Stack

- **Spring Boot 3.3.10**
- **Spring Data JPA**
- **Spring Data REST**
- **MySQL**
- **Lombok**
- **JUnit 5 + Spring Boot Test**
- **Spring Boot DevTools**

## 📦 Features
- **📘 Book CRUD API**
- **👤 Author & Borrower management**
- **🔄 Book checkout & return tracking**
- **📈 RESTFul API with Spring Data REST**
- **🔐 Secure REST endpoints using Spring Security and Okta (OAuth 2.0 / OIDC)**
- **✅ Unit & Integration testing**
- **🌱 Future deployment**

## ⚙️ Getting Started
### Prerequisites
- **JDK 17+**
- **Maven 3.8+**
- **MySQL server running**

### Setup
1. Clone the repo:
   `git clone https://github.com/your-username/library-app-backend.git
   cd library-app-backend`

2. Configure MySQL in application.properties:
 ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/library_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Run the app:
```bash
    ./mvnw spring-boot:run
```

Access REST endpoints at:
http://localhost:8080

## 📌 Migration Notes (from Spring Boot 2.7 → 3.3.10)
- **Updated dependencies to be compatible with Jakarta EE 9+ namespaces.**
- **Ensured usage of jakarta.persistence. and jakarta.servlet.* packages.**
- **Checked compatibility of plugins and removed deprecated configurations.**

## 🔧 To Do (Next Steps)
- **Add Swagger for documentation**