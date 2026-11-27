# Entity Relationship Diagram

```mermaid
erDiagram
    CUSTOMER ||--o{ ACCOUNT : owns
    ACCOUNT ||--o{ BANK_TRANSACTION : sends
    ACCOUNT ||--o{ BANK_TRANSACTION : receives

    CUSTOMER {
        BIGINT customer_id PK
        VARCHAR customer_name
        VARCHAR email
        VARCHAR phone
    }

    ACCOUNT {
        BIGINT account_id PK
        BIGINT customer_id FK
        VARCHAR account_number
        DECIMAL balance
        VARCHAR account_type
    }

    BANK_TRANSACTION {
        VARCHAR transaction_id PK
        BIGINT from_account
        BIGINT to_account
        VARCHAR transaction_type
        DECIMAL amount
        VARCHAR status
        TIMESTAMP created_at
    }

    TRANSACTION_AUDIT {
        BIGINT audit_id PK
        VARCHAR transaction_id
        VARCHAR action
        VARCHAR details
        TIMESTAMP created_at
    }

    FAILED_TRANSACTION {
        BIGINT failure_id PK
        VARCHAR transaction_id
        VARCHAR error_message
        TIMESTAMP failed_at
    }
