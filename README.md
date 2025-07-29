# Alpha Bank Backend

A robust and secure banking backend system built with modern technologies to provide comprehensive financial services including account management, transactions, user authentication, and administrative operations.

## 🚀 Features

- **User Authentication & Authorization**
    - JWT-based authentication
    - Role-based access control (Admin, Customer)
    - Secure password hashing
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
    - Input validation and sanitization
    - Rate limiting
    - CORS protection
    - SQL injection prevention
    - Encryption for sensitive data

- **Administrative Features**
    - User management
    - Transaction monitoring
    - Account oversight
    - System analytics

## 🛠️ Tech Stack

- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt
- **Validation**: Joi/express-validator
- **Testing**: Jest/Mocha
- **Deployment**: Docker

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- Node.js (v16 or higher)
- npm 
- PostgreSQL
- Git

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/MACKENA05/Alpha-Bank-Backend.git
   cd Alpha-Bank-Backend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Environment Configuration**

   Create a `.env` file in the root directory and add the following variables:
   ```env
   # Server Configuration
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
# 24h = 86,400,000 milliseconds
app.jwt.expiration=86400000

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
   # Run database migrations
   npm run migrate

   # Seed initial data (optional)
   npm run seed
   ```

5. **Start the application**
   ```bash
   # Development mode
   npm run dev

   # Production mode
   npm start
   ```

## 🏗️ Project Structure

```
Alpha-Bank-Backend/
├── src/
│   ├── controllers/         # Request handlers
│   │   ├── authController.js
│   │   ├── userController.js
│   │   ├── accountController.js
│   │   └── transactionController.js
│   ├── models/             # Database models
│   │   ├── User.js
│   │   ├── Account.js
│   │   └── Transaction.js
│   ├── routes/             # API routes
│   │   ├── auth.js
│   │   ├── users.js
│   │   ├── accounts.js
│   │   └── transactions.js
│   ├── middleware/         # Custom middleware
│   │   ├── auth.js
│   │   ├── validation.js
│   │   └── errorHandler.js
│   ├── utils/              # Utility functions
│   │   ├── database.js
│   │   ├── jwt.js
│   │   └── helpers.js
│   ├── config/             # Configuration files
│   │   ├── database.js
│   │   └── config.js
│   └── app.js              # Express app setup
├── tests/                  # Test files
├── docs/                   # API documentation
├── migrations/             # Database migrations
├── seeds/                  # Database seeds
├── .env.example           # Environment variables template
├── package.json
├── README.md
└── server.js              # Application entry point
```

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout


### Users
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `DELETE /api/users/profile` - Delete user account

### Accounts
- `GET /api/accounts` - Get user accounts
- `POST /api/accounts` - Create new account
- `GET /api/accounts/:id` - Get specific account
- `PUT /api/accounts/:id` - Update account
- `DELETE /api/accounts/:id` - Delete account

### Transactions
- `GET /api/transactions` - Get transaction history
- `POST /api/transactions/transfer` - Transfer money
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money
- `GET /api/transactions/:id` - Get transaction details

### Admin (Protected Routes)
- `GET /api/admin/users` - Get all users
- `GET /api/admin/transactions` - Get all transactions
- `PUT /api/admin/users/:id/status` - Update user status

## 🧪 Testing

Run the test suite:

```bash
# Run all tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

## 🚀 Deployment

### Using Docker

1. **📦 Build the Docker Image**
   ```bash
   docker build -t <repo-name>/alpha-bank-backend .
   ```

2. **📤 Push to Docker Hub**
   ```bash
   docker push <repo-name>/alpha-bank-backend
   ```

### Using Docker Compose

```bash
docker-compose up -d
```
🔐 Make sure you are logged in:

```bash
docker login
```

#🔁 Updating Your Deployment

1. Make code changes.

2. Rebuild the image:

```bash
docker build -t mackena05/alpha-bank-backend .
```

3. Push again:

```bash
docker push mackena05/alpha-bank-backend
```

4. Render will pull the new version automatically or manually if configured.

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'customer',
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Accounts Table
```sql
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    from_account_id INTEGER REFERENCES accounts(id),
    to_account_id INTEGER REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔒 Security Considerations

- All passwords are hashed using bcrypt
- JWT tokens are used for authentication
- Input validation on all endpoints
- Rate limiting implemented
- CORS configured for production
- SQL injection prevention
- Sensitive data encryption

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



## 📈 Roadmap

- [ ] Mobile app integration
- [ ] Real-time notifications
- [ ] Multi-currency support
- [ ] Advanced analytics dashboard
- [ ] Loan management system
- [ ] Credit card processing
- [ ] Investment portfolio management

---

**Note**: This is a backend API service. For the frontend application, please refer to the [Alpha Bank Frontend](https://github.com/MACKENA05/Alpha-Bank-Frontend) repository.