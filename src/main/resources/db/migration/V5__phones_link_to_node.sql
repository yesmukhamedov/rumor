ALTER TABLE phones
    ADD COLUMN node_id bigint NOT NULL;

ALTER TABLE phones
    ALTER COLUMN value TYPE varchar(64);

ALTER TABLE phones
    ADD CONSTRAINT fk_phones_node
    FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE;

ALTER TABLE phones
    ADD CONSTRAINT uk_phones_node_id UNIQUE (node_id);

ALTER TABLE phones
    ADD CONSTRAINT uk_phones_value UNIQUE (value);
