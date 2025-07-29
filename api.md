# Alpha Bank Backend API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL](#base-url)
4. [Response Format](#response-format)
5. [Error Handling](#error-handling)
6. [Rate Limiting](#rate-limiting)
7. [API Endpoints](#api-endpoints)
    - [Authentication Endpoints](#authentication-endpoints)
    - [User Management](#user-management)
    - [Account Management](#account-management)
    - [Transaction Management](#transaction-management)
    - [Admin Endpoints](#admin-endpoints)

## Overview

The Alpha Bank Backend API provides a comprehensive set of RESTful endpoints for banking operations including user authentication, account management, transaction processing, and administrative functions.

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

## Base URL

```
Production: https://alpha-bank-backend.onrender.com/api/v1
Development: http://localhost:3000/api/v1
```

## Response Format

All API responses follow a consistent JSON format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": {
    "code": "ERROR_CODE",
    "details": "Detailed error information"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Error Handling

### HTTP Status Codes

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `422` - Validation Error
- `429` - Too Many Requests
- `500` - Internal Server Error



---

# API Endpoints

## Authentication Endpoints

### Register User

**POST** `/auth/register`

Creates a new user account.

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-15"
}
```

#### Response
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "role": "customer",
      "isVerified": false,
      "createdAt": "2024-01-15T10:30:00Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### Validation Rules
- `firstName`: Required, 2-50 characters
- `lastName`: Required, 2-50 characters
- `email`: Required, valid email format, unique
- `password`: Required, minimum 8 characters, must contain uppercase, lowercase, number, and special character
- `phone`: Optional, valid phone format

---

### Login User

**POST** `/auth/login`

Authenticates a user and returns a JWT token.

#### Request Body
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

#### Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "role": "customer"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": "24h"
  }
}
```

---

---

## User Management

### Get User Profile

**GET** `/users/profile`

**Authentication Required**

Retrieves the authenticated user's profile information.

#### Response
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "dateOfBirth": "1990-01-15",
      "address": {
        "street": "123 Main St",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
      },
      "isVerified": true,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  }
}
```

---

### Update User Profile

**PUT** `/users/profile`

**Authentication Required**

Updates the authenticated user's profile information.

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+1234567890",
  "address": {
    "street": "456 Oak St",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90001",
    "country": "USA"
  }
}
```

#### Response
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "user": {
      // Updated user object
    }
  }
}
```

---

## Account Management

### Get User Accounts

**GET** `/accounts`

**Authentication Required**

Retrieves all accounts belonging to the authenticated user.

#### Query Parameters
- `type` (optional): Filter by account type (`checking`, `savings`)
- `status` (optional): Filter by status (`active`, `inactive`)

#### Response
```json
{
  "success": true,
  "data": {
    "accounts": [
      {
        "id": 1,
        "accountNumber": "1001234567890",
        "accountType": "checking",
        "balance": 2500.50,
        "status": "active",
        "currency": "USD",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      },
      {
        "id": 2,
        "accountNumber": "2001234567890",
        "accountType": "savings",
        "balance": 10000.00,
        "status": "active",
        "currency": "USD",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "totalAccounts": 2
  }
}
```

---

### Create New Account

**POST** `/accounts`

**Authentication Required**

Creates a new bank account for the authenticated user.

#### Request Body
```json
{
  "accountType": "savings",
  "initialDeposit": 1000.00,
  "currency": "USD"
}
```

#### Response
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "account": {
      "id": 3,
      "accountNumber": "3001234567890",
      "accountType": "savings",
      "balance": 1000.00,
      "status": "active",
      "currency": "USD",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  }
}
```

---

### Get Account Details

**GET** `/accounts/:id`

**Authentication Required**

Retrieves details of a specific account.

#### URL Parameters
- `id`: Account ID

#### Response
```json
{
  "success": true,
  "data": {
    "account": {
      "id": 1,
      "accountNumber": "1001234567890",
      "accountType": "checking",
      "balance": 2500.50,
      "status": "active",
      "currency": "USD",
      "interestRate": 0.01,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "recentTransactions": [
        {
          "id": 101,
          "amount": 500.00,
          "type": "credit",
          "description": "Salary deposit",
          "createdAt": "2024-01-14T15:30:00Z"
        }
      ]
    }
  }
}
```

---

## Transaction Management

### Get Transaction History

**GET** `/transactions`

**Authentication Required**

Retrieves transaction history for the authenticated user.

#### Query Parameters
- `accountId` (optional): Filter by specific account
- `type` (optional): Filter by transaction type (`credit`, `debit`, `transfer`)
- `startDate` (optional): Start date (YYYY-MM-DD)
- `endDate` (optional): End date (YYYY-MM-DD)
- `limit` (optional): Number of transactions to return (default: 20, max: 100)
- `offset` (optional): Number of transactions to skip (default: 0)

#### Response
```json
{
  "success": true,
  "data": {
    "transactions": [
      {
        "id": 101,
        "fromAccount": "1001234567890",
        "toAccount": "2001234567890",
        "amount": 500.00,
        "type": "transfer",
        "status": "completed",
        "description": "Monthly savings transfer",
        "reference": "TXN-20240115-101",
        "createdAt": "2024-01-15T10:30:00Z"
      },
      {
        "id": 102,
        "fromAccount": null,
        "toAccount": "1001234567890",
        "amount": 2000.00,
        "type": "credit",
        "status": "completed",
        "description": "Salary deposit",
        "reference": "TXN-20240114-102",
        "createdAt": "2024-01-14T15:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalTransactions": 95,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

---

### Transfer Money

**POST** `/transactions/transfer`

**Authentication Required**

Transfers money between accounts.

#### Request Body
```json
{
  "fromAccountId": 1,
  "toAccountNumber": "2001234567890",
  "amount": 500.00,
  "description": "Monthly savings transfer",
  "pin": "1234"
}
```

#### Response
```json
{
  "success": true,
  "message": "Transfer completed successfully",
  "data": {
    "transaction": {
      "id": 103,
      "fromAccount": "1001234567890",
      "toAccount": "2001234567890",
      "amount": 500.00,
      "type": "transfer",
      "status": "completed",
      "description": "Monthly savings transfer",
      "reference": "TXN-20240115-103",
      "fee": 0.00,
      "createdAt": "2024-01-15T10:45:00Z"
    },
    "balanceAfter": {
      "fromAccount": 2000.50,
      "toAccount": 10500.00
    }
  }
}
```

---

### Deposit Money

**POST** `/transactions/deposit`

**Authentication Required**

Deposits money into an account.

#### Request Body
```json
{
  "accountId": 1,
  "amount": 1000.00,
  "description": "Cash deposit",
  "depositMethod": "atm"
}
```

#### Response
```json
{
  "success": true,
  "message": "Deposit completed successfully",
  "data": {
    "transaction": {
      "id": 104,
      "toAccount": "1001234567890",
      "amount": 1000.00,
      "type": "credit",
      "status": "completed",
      "description": "Cash deposit",
      "reference": "TXN-20240115-104",
      "createdAt": "2024-01-15T11:00:00Z"
    },
    "newBalance": 3000.50
  }
}
```

---

### Withdraw Money

**POST** `/transactions/withdraw`

**Authentication Required**

Withdraws money from an account.

#### Request Body
```json
{
  "accountId": 1,
  "amount": 200.00,
  "description": "ATM withdrawal",
  "pin": "1234"
}
```

#### Response
```json
{
  "success": true,
  "message": "Withdrawal completed successfully",
  "data": {
    "transaction": {
      "id": 105,
      "fromAccount": "1001234567890",
      "amount": 200.00,
      "type": "debit",
      "status": "completed",
      "description": "ATM withdrawal",
      "reference": "TXN-20240115-105",
      "fee": 2.50,
      "createdAt": "2024-01-15T11:15:00Z"
    },
    "newBalance": 2798.00
  }
}
```

---

## Admin Endpoints

**Note**: All admin endpoints require admin role authentication.

### Get All Users

**GET** `/admin/users`

**Admin Authentication Required**

Retrieves all users in the system.

#### Query Parameters
- `status` (optional): Filter by user status (`active`, `inactive`)
- `role` (optional): Filter by role (`user`, `admin`)
- `search` (optional): Search by name or email
- `limit` (optional): Number of users to return (default: 20, max: 100)
- `offset` (optional): Number of users to skip (default: 0)

#### Response
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "role": "customer",
        "status": "active",
        "totalAccounts": 2,
        "totalBalance": 12500.50,
        "lastLogin": "2024-01-15T10:30:00Z",
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 10,
      "totalUsers": 195
    }
  }
}
```

---

### Get All Transactions

**GET** `/admin/transactions`

**Admin Authentication Required**

Retrieves all transactions in the system.

#### Query Parameters
- `status` (optional): Filter by status (`pending`, `completed`, `failed`)
- `type` (optional): Filter by type (`credit`, `debit`, `transfer`)
- `userId` (optional): Filter by user ID
- `startDate` (optional): Start date (YYYY-MM-DD)
- `endDate` (optional): End date (YYYY-MM-DD)
- `minAmount` (optional): Minimum transaction amount
- `maxAmount` (optional): Maximum transaction amount
- `limit` (optional): Number of transactions to return (default: 50, max: 200)
- `offset` (optional): Number of transactions to skip (default: 0)

#### Response
```json
{
  "success": true,
  "data": {
    "transactions": [
      {
        "id": 101,
        "userId": 1,
        "userName": "John Doe",
        "fromAccount": "1001234567890",
        "toAccount": "2001234567890",
        "amount": 500.00,
        "type": "transfer",
        "status": "completed",
        "description": "Monthly savings transfer",
        "reference": "TXN-20240115-101",
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 50,
      "totalTransactions": 2456
    },
    "summary": {
      "totalAmount": 1250000.00,
      "completedTransactions": 2400,
      "pendingTransactions": 45,
      "failedTransactions": 11
    }
  }
}
```

---

### Update User Status

**PUT** `/admin/users/:id/status`

**Admin Authentication Required**

Updates a user's account status.

#### URL Parameters
- `id`: User ID

#### Request Body
```json
{
  "status": "suspended",
  "reason": "Suspicious activity detected"
}
```

#### Response
```json
{
  "success": true,
  "message": "User status updated successfully",
  "data": {
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "status": "suspended",
      "updatedAt": "2024-01-15T12:00:00Z"
    }
  }
}
```

---

## Webhook Endpoints

### Transaction Webhook

**POST** `/webhooks/transaction`

Webhook endpoint for external transaction notifications.

#### Request Headers
```
X-Webhook-Signature: sha256=<signature>
Content-Type: application/json
```

#### Request Body
```json
{
  "event": "transaction.completed",
  "data": {
    "transactionId": "TXN-20240115-101",
    "amount": 500.00,
    "status": "completed",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

---

## Data Models

### User Model
```json
{
  "id": "integer",
  "firstName": "string (required, 2-50 chars)",
  "lastName": "string (required, 2-50 chars)",
  "email": "string (required, unique, valid email)",
  "phone": "string (optional, valid phone format)",
  "dateOfBirth": "date (optional)",
  "address": {
    "street": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string"
  },
  "role": "enum (customer, admin)",
  "status": "enum (active, inactive, suspended)",
  "isVerified": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

Account Model

```json
{
"id": "integer",
"userId": "integer (foreign key)",
"accountNumber": "string (unique, 10-20 chars)",
"accountType": "enum (checking, savings, business)",
"balance": "decimal (15,2)",
"currency": "string (3 chars, default: USD)",
"status": "enum (active, inactive, frozen)",
"interestRate": "decimal (5,4)",
"overdraftLimit": "decimal (15,2)",
"createdAt": "timestamp",
"updatedAt": "timestamp"
}
```

Transaction Model
```json
{
"id": "integer",
"userId": "integer (foreign key)",
"fromAccountId": "integer (optional, foreign key)",
"toAccountId": "integer (optional, foreign key)",
"amount": "decimal (15,2, required)",
"type": "enum (credit, debit, transfer)",
"status": "enum (pending, completed, failed, cancelled)",
"description": "string (optional, max 255 chars)",
"reference": "string (unique, auto-generated)",
"fee": "decimal (15,2, default: 0.00)",
"exchangeRate": "decimal (10,6, optional)",
"metadata": "json (optional)",
"createdAt": "timestamp",
"updatedAt": "timestamp"
}
```

#Test User Accounts
###For testing purposes, you can use these pre-created accounts:

```json
{
"testUsers": [
  {
    "email": "customer@gmail.com",
    "password": "Pass@123",
    "role": "customer",
     "accounts": [
        {
           "accountNumber": "ACC202507297679",
           "type": "checking",
           "balance": 2500.00,
           "pin" : 1234
        },
        {
           "accountNumber": "ACC202507293289",
           "type": "savings",
           "balance": 10000.00
        }
     ]
  },
{
"email": "admin@gmail.com",
"password": "AdminPassword123!",
"role": "admin"
}
]
}
```