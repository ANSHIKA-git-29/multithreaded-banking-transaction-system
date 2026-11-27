USE banking_system;

-- 1. Total transactions by type
SELECT transaction_type, COUNT(*) AS total_transactions
FROM BANK_TRANSACTION
GROUP BY transaction_type;

-- 2. Successful and failed transactions
SELECT status, COUNT(*) AS total
FROM BANK_TRANSACTION
GROUP BY status;

-- 3. Recent transaction history
SELECT *
FROM BANK_TRANSACTION
ORDER BY created_at DESC
LIMIT 20;

-- 4. Audit summary
SELECT action, COUNT(*) AS total
FROM TRANSACTION_AUDIT
GROUP BY action;

-- 5. Failed transaction history
SELECT *
FROM FAILED_TRANSACTION
ORDER BY failed_at DESC;

-- 6. Account balances
SELECT account_id, account_number, balance
FROM ACCOUNT
ORDER BY account_id;

-- 7. Index inspection
SHOW INDEX FROM BANK_TRANSACTION;

-- 8. Query execution plan
EXPLAIN
SELECT *
FROM BANK_TRANSACTION
WHERE transaction_id = 'sample-id';
