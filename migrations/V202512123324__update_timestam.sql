


-- Thêm cột created_at
ALTER TABLE permissions
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();

-- Thêm cột updated_at
ALTER TABLE permissions
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT now();
