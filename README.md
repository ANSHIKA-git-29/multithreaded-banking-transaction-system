# Multi-Threaded Banking Transaction Processing System

## Overview
A Java-based banking application supporting concurrent deposits, withdrawals, and fund transfers with transaction integrity.

## Technologies
- Java 17
- Maven
- MySQL
- JDBC
- HikariCP
- SLF4J and Logback
- JUnit 5

## Features
- Deposit, withdrawal, and transfer operations
- Multithreaded transaction processing
- JDBC transaction management
- Account locking
- Idempotency protection
- Transaction audit logging
- Failed transaction logging
- Database connection pooling

## Database Setup
1. Open MySQL Workbench.
2. Execute database/schema.sql.
3. Execute database/seed.sql.
4. Use the banking_system database.

## Database Configuration
Set the MySQL password in PowerShell:

    $env:DB_PASSWORD = Read-Host "Enter MySQL password"

## Build

    mvn compile

## Run Tests

    mvn test

## Run Application

    mvn exec:java -Dexec.mainClass="com.newgen.banking.Main"

## Automated Tests
1. ConcurrentWithdrawalTest
2. IdempotencyTest
3. TransferAuditTest

All three tests have passed.

## Performance Testing
The simulator supports 1, 5, 10, and 20 worker threads with 50,000 transactions per configuration.

Record actual benchmark results after successful runs.

## Database Tables
- CUSTOMER
- ACCOUNT
- BANK_TRANSACTION
- TRANSACTION_AUDIT
- FAILED_TRANSACTION

## GitHub
https://github.com/ANSHIKA-git-29/multithreaded-banking-transaction-system
