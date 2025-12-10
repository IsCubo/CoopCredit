-- Create role table
CREATE TABLE IF NOT EXISTS role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create coop_user table
CREATE TABLE IF NOT EXISTS coop_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create affiliate table
CREATE TABLE IF NOT EXISTS affiliate (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    annual_income NUMERIC(15, 2) NOT NULL,
    registration_date DATE NOT NULL,
    user_id BIGINT UNIQUE
);

-- Create credit_application table
CREATE TABLE IF NOT EXISTS credit_application (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount NUMERIC(15, 2) NOT NULL,
    term_months INTEGER NOT NULL,
    application_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    risk_score INTEGER,
    risk_level VARCHAR(50)
);

-- Create risk_evaluation table
CREATE TABLE IF NOT EXISTS risk_evaluation (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL UNIQUE,
    score INTEGER NOT NULL,
    risk_level VARCHAR(50) NOT NULL,
    reason VARCHAR(500),
    evaluation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
