# Alpha Bank Backend

A robust and secure banking backend system built with Spring Boot to provide comprehensive financial services including account management, transactions, user authentication, and administrative operations.

## 🚀 Features

- **User Authentication & Authorization**
    - JWT-based authentication
    - Role-based access control (Admin, Customer)
    - Secure password hashing with BCrypt
    - Account verification and password reset

- **Account Management**
    - Create and manage bank accounts
    - Multiple account types support
    - Account balance tracking
    - Account status management

- **Transaction Processing**
    - Money transfers between accounts
    - Deposit and withdrawal operations
    - Transaction history and statements
    - Real-time balance updates

- **Security Features**
    - Input validation with Spring Validation
    - Rate limiting
    - CORS protection
    - SQL injection prevention with JPA
    - Encryption for sensitive data

- **Administrative Features**
    - User management
    - Transaction monitoring
    - Account oversight
    - System analytics

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: BCrypt
- **Validation**: Spring Boot Validation
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven
- **Deployment**: Docker

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/MACKENA05/Alpha-Bank-Backend.git
   cd Alpha-Bank-Backend
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Environment Configuration**

   Create an `application.properties` file in `src/main/resources/` or use environment variables:
   ```properties
   # Application name
   spring.application.name=Banking-Application-backend
   
   # Server configuration
   server.port=3000
   spring.profiles.active=development
   
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/alpha_bank
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA / Hibernate Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=false
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.properties.hibernate.format_sql=true
   
   # JWT Configuration
   app.jwt.secret=your_super_secret_jwt_key
   # 1h = 3,600,000 milliseconds
   app.jwt.expiration=3600000
   
   # Email Configuration
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_email_password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   
   # Security
   app.security.bcrypt.rounds=12
   
   # Logging
   logging.level.com.mackena.banking=INFO
   logging.level.org.hibernate.SQL=INFO
   
   # CORS Configuration
   cors.allowed-origins=http://localhost:3000,https://alpha-bank.com
   cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
   cors.allowed-headers=*
   cors.allow-credentials=true
   ```

4. **Database Setup**
   ```bash
   # Create database
   createdb alpha_bank
   
   # Run the application (it will create tables automatically with ddl-auto=update)
   mvn spring-boot:run
   ```

5. **Start the application**
   ```bash
   # Development mode
   mvn spring-boot:run
   
   # Or build and run JAR
   mvn clean package
   java -jar target/banking-application-backend-*.jar
   ```

## 🏗️ Project Structure

```
Alpha-Bank-Backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mackena/banking/
│   │   │       ├── BankingApplication.java    # Main application class
│   │   │       ├── controller/                # REST controllers
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── AccountController.java
│   │   │       │   └── TransactionController.java
│   │   │       ├── entity/                   # JPA entities
│   │   │       │   ├── User.java
│   │   │       │   ├── Account.java
│   │   │       │   └── Transaction.java
│   │   │       ├── repository/               # JPA repositories
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── AccountRepository.java
│   │   │       │   └── TransactionRepository.java
│   │   │       ├── service/                  # Business logic
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── AccountService.java
│   │   │       │   └── TransactionService.java
│   │   │       ├── dto/                      # Data Transfer Objects
│   │   │       │   ├── UserDto.java
│   │   │       │   ├── AccountDto.java
│   │   │       │   └── TransactionDto.java
│   │   │       ├── config/                   # Configuration classes
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── JwtConfig.java
│   │   │       │   └── CorsConfig.java
│   │   │       ├── security/                 # Security components
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   └── UserPrincipal.java
│   │   │       └── exception/                # Exception handling
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── CustomExceptions.java
│   │   └── resources/
│   │       ├── application.properties        # Configuration
│   │       └── data.sql                     # Initial data (optional)
│   └── test/                                # Test files
│       └── java/
├── target/                                  # Build output
├── pom.xml                                  # Maven configuration
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh-token` - Refresh JWT token

### Users
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `DELETE /api/users/profile` - Delete user account

### Accounts
- `GET /api/accounts` - Get user accounts
- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get specific account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transactions
- `GET /api/transactions` - Get transaction history
- `POST /api/transactions/transfer` - Transfer money
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money
- `GET /api/transactions/{id}` - Get transaction details

### Admin (Protected Routes)
- `GET /api/admin/users` - Get all users
- `GET /api/admin/transactions` - Get all transactions
- `PUT /api/admin/users/{id}/status` - Update user status

## 🧪 Testing

Run the test suite:

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run integration tests
mvn verify
```

## 🚀 Deployment

### Using Docker

1. **📦 Build the Docker Image**
   ```bash
   docker build -t <dockerrepo-name>/alpha-bank-backend .
   ```

2. **📤 Push to Docker Hub**
   ```bash
   docker push <dockerrepo-name>/alpha-bank-backend
   ```

### Using Docker Compose

```bash
docker-compose up -d
```

### Sample Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/banking-application-backend-*.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 📊 Database Schema

### Users Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication with configurable expiration
- **BCrypt Password Hashing**: Industry-standard password encryption
- **Role-Based Access Control**: Admin and Customer roles with different permissions
- **Input Validation**: Bean validation with custom validators
- **CORS Configuration**: Configurable cross-origin resource sharing
- **SQL Injection Prevention**: JPA/Hibernate prevents SQL injection attacks
- **Rate Limiting**: Configurable request rate limiting (if implemented)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **MACKENA05** - *Initial work* - [GitHub Profile](https://github.com/MACKENA05)

## 🛠️ Dependencies

Key dependencies used in this project:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>
```

## 📈 Roadmap

- [ ] Mobile app integration
- [ ] Real-time notifications with WebSocket
- [ ] Multi-currency support
- [ ] Advanced analytics dashboard
- [ ] Loan management system
- [ ] Credit card processing
- [ ] Investment portfolio management
- [ ] Microservices architecture migration
- [ ] Redis caching implementation
- [ ] Audit logging system

---

**Note**: This is a backend API service built with Spring Boot. For the frontend application, please refer to the [https://github.com/MACKENA05/Alpha-Bank-Frontend](https://github.com/MACKENA05/Alpha-Bank-Frontend) repository.