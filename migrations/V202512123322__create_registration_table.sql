CREATE TABLE registrations
(
    id  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT  now(),
    username   VARCHAR(255) NULL,
    password   VARCHAR(255) NULL,
    email      VARCHAR(255) NULL
);

CREATE TABLE email_verification_token
(
    id  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT  now(),
    token_hash VARCHAR(255) NULL,
    email      VARCHAR(255) NULL,
    expires_at TIMESTAMP     NULL,
    verified   bool       NOT NULL
);