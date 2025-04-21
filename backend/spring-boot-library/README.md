# 📚 Library App - Backend

A simple library management backend built using **Spring Boot 3.3.10**, migrated from version 2.7. Designed to support typical library features such as managing books, authors, borrowers, and lending records.

## 🚀 Tech Stack

- **Spring Boot 3.3.10**
- **Spring Data JPA**
- **Spring Data REST**
- **MySQL**
- **Lombok**
- **Spring Boot DevTools**

## 📦 Features
- **Book CRUD API**
- **Author & Borrower management**
- **Book checkout & return tracking**
- **Payment Method With Stripe**
- **RESTFul API with Spring Data REST**
- **Secure REST endpoints using Spring Security and Okta (OAuth 2.0 / OIDC)**
- **History tracking by user email**
- **SSL Certificated (OpenSSL)**

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

## 🔧 To Do (Next Steps)
- **Deployment in future**