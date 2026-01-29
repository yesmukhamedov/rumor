CREATE TABLE phone_patterns (
    id bigserial PRIMARY KEY,
    code varchar(10) NOT NULL UNIQUE,
    value varchar(64) NOT NULL
);

CREATE TABLE phones (
    id bigserial PRIMARY KEY,
    pattern_id bigint NOT NULL REFERENCES phone_patterns(id) ON DELETE RESTRICT,
    value varchar(32) NOT NULL
);

INSERT INTO phone_patterns (code, value)
VALUES
    ('KZ', '+7 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('TR', '+90 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('RU', '+7 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('USA', '+1 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('UZ', '+998 ( _ _ ) - _ _ _ - _ _ - _ _'),
    ('KG', '+996 ( _ _ _ ) - _ _ - _ _ - _ _'),
    ('AE', '+971 ( _ ) - _ _ _ - _ _ _ - _ _'),
    ('CN', '+86 ( _ _ _ ) - _ _ _ - _ _ _ - _ _'),
    ('AZ', '+994 ( _ _ ) - _ _ _ - _ _ - _ _'),
    ('MY', '+60 ( _ _ ) - _ _ _ _ - _ _ _ _'),
    ('TJ', '+992 ( _ _ ) - _ _ _ - _ _ - _ _')
ON CONFLICT (code) DO NOTHING;
