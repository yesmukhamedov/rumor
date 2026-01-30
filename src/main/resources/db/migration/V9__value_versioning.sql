CREATE TABLE node_values (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    value VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE INDEX idx_node_values_node_id ON node_values(node_id);

CREATE TABLE edge_values (
    id BIGSERIAL PRIMARY KEY,
    edge_id BIGINT NOT NULL REFERENCES edges(id) ON DELETE CASCADE,
    value VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE INDEX idx_edge_values_edge_id ON edge_values(edge_id);

CREATE TABLE phone_values (
    id BIGSERIAL PRIMARY KEY,
    phone_id BIGINT NOT NULL REFERENCES phones(id) ON DELETE CASCADE,
    value VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE INDEX idx_phone_values_phone_id ON phone_values(phone_id);
CREATE UNIQUE INDEX ux_phone_values_value ON phone_values(value);

INSERT INTO node_values (node_id, value, created_at, created_by)
SELECT n.id,
       v.text,
       COALESCE(v.created_at, now()),
       v.created_by
FROM nodes n
JOIN app_values v ON v.id = n.value_id;

INSERT INTO edge_values (edge_id, value, created_at, created_by)
SELECT e.id,
       v.text,
       COALESCE(v.created_at, now()),
       v.created_by
FROM edges e
JOIN app_values v ON v.id = e.value_id;

INSERT INTO phone_values (phone_id, value, created_at, created_by)
SELECT p.id,
       v.text,
       COALESCE(v.created_at, now()),
       v.created_by
FROM phones p
JOIN app_values v ON v.id = p.value_id;

DROP INDEX idx_edges_value_id;
ALTER TABLE edges DROP CONSTRAINT fk_edges_value_id;
ALTER TABLE edges DROP COLUMN value_id;

ALTER TABLE nodes DROP CONSTRAINT fk_nodes_value_id;
ALTER TABLE nodes DROP COLUMN value_id;

DROP INDEX ux_phones_value_id;
ALTER TABLE phones DROP CONSTRAINT fk_phones_value_id;
ALTER TABLE phones DROP COLUMN value_id;

DROP TABLE app_values;
