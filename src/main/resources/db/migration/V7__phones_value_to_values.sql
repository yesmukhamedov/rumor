ALTER TABLE phones ADD COLUMN value_id BIGINT;

INSERT INTO app_values (text)
SELECT DISTINCT p.value
FROM phones p
LEFT JOIN app_values v ON v.text = p.value
WHERE v.id IS NULL;

UPDATE phones p
SET value_id = v.id
FROM app_values v
WHERE v.text = p.value;

ALTER TABLE phones ALTER COLUMN value_id SET NOT NULL;

ALTER TABLE phones DROP CONSTRAINT uk_phones_value;
ALTER TABLE phones DROP COLUMN value;

ALTER TABLE phones
    ADD CONSTRAINT fk_phones_value_id
    FOREIGN KEY (value_id) REFERENCES app_values(id) ON DELETE RESTRICT;

CREATE UNIQUE INDEX ux_phones_value_id ON phones(value_id);
