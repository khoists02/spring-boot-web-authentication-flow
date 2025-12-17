CREATE TABLE permissions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR NOT NULL,
    description     VARCHAR NULL,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT  now(),
    CONSTRAINT      un_per_name UNIQUE(name)
);

-- Create User-Roles Table (Many-to-Many Relationship)
CREATE TABLE roles_permissions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    permission_id         UUID NOT NULL,
    role_id         UUID NOT NULL,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT  now(),
    CONSTRAINT fk_user FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);