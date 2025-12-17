-- USERS

INSERT INTO permissions (id, name, description)
VALUES (uuid_generate_v4(), 'manageUser', 'Manage User')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (id, name, description)
VALUES (uuid_generate_v4(), 'viewUser', 'View User')
    ON CONFLICT (name) DO NOTHING;