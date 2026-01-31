DROP TABLE IF EXISTS profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NULL,
    UNIQUE (node_id)
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'phones'
    ) THEN
        INSERT INTO users (node_id, created_at)
        SELECT DISTINCT p.node_id, now()
        FROM phones p
        ON CONFLICT (node_id) DO NOTHING;
    END IF;
END $$;

CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    external_user_uuid UUID NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE UNIQUE INDEX ux_profiles_external_uuid ON profiles(external_user_uuid);
CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_user_created ON profiles (user_id, created_at);

DROP TABLE IF EXISTS phone_values CASCADE;
DROP TABLE IF EXISTS phone_patterns CASCADE;
DROP TABLE IF EXISTS phones CASCADE;
