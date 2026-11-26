
USE banking_system;

CREATE TABLE CUSTOMER (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15)
);

CREATE TABLE ACCOUNT (
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(30) UNIQUE NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    CHECK (balance >= 0)
);

CREATE TABLE BANK_TRANSACTION (
    transaction_id VARCHAR(100) PRIMARY KEY,
    from_account BIGINT,
    to_account BIGINT,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (amount > 0)
);

CREATE TABLE TRANSACTION_AUDIT (
    audit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(100) NOT NULL,
    action VARCHAR(50),
    details VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE FAILED_TRANSACTION (
    failure_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(100),
    error_message VARCHAR(500),
    failed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
