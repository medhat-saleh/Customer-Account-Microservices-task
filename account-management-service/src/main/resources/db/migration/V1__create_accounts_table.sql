
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT PRIMARY KEY CHECK (id >= 1000000000 AND id <= 9999999999),
    customer_id BIGINT NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00 CHECK (balance >= 0),
    type VARCHAR(20) CHECK (type IN ('SAVING', 'SALARY', 'INVESTMENT')) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')) DEFAULT 'ACTIVE',
    min_balance DECIMAL(15, 2) DEFAULT 0.00,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_investment_min_balance CHECK ((type != 'INVESTMENT') OR (type = 'INVESTMENT' AND balance >= 10000)));

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_accounts_type ON accounts(type);