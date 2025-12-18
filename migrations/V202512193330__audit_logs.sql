CREATE TABLE audit_logs
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP        DEFAULT now(),
    updated_at TIMESTAMP        DEFAULT now(),
    username   VARCHAR(255) NULL,
    action     VARCHAR(255) NULL,
    resource   VARCHAR(255) NULL,
    status     VARCHAR(255) NULL
);