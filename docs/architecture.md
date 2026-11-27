# System Architecture

```mermaid
flowchart TD
    A[Main Application] --> B[Performance Simulator]
    B --> C[Transaction Workers]
    C --> D[Banking Service]

    D --> E[Account DAO]
    D --> F[Transaction DAO]

    E --> G[(MySQL Database)]
    F --> G

    G --> H[ACCOUNT]
    G --> I[BANK_TRANSACTION]
    G --> J[TRANSACTION_AUDIT]
    G --> K[FAILED_TRANSACTION]

    D --> L[SLF4J / Logback]
