CREATE TABLE IF NOT EXISTS customers (
                                         id BIGINT PRIMARY KEY CHECK (id >= 1000000 AND id <= 9999999),
    legal_id VARCHAR NOT NULL UNIQUE,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR,
    type VARCHAR(255) CHECK (type IN ('RETAIL', 'CORPORATE', 'INVESTMENT')) NOT NULL,
    email VARCHAR,
    addres VARCHAR,
    phone VARCHAR,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );