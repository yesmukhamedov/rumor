ALTER TABLE names RENAME TO app_values;

ALTER TABLE nodes RENAME COLUMN name_id TO value_id;
ALTER TABLE edges RENAME COLUMN name_id TO value_id;

ALTER TABLE nodes RENAME CONSTRAINT fk_nodes_name_id TO fk_nodes_value_id;
ALTER TABLE edges RENAME CONSTRAINT fk_edges_name_id TO fk_edges_value_id;

ALTER INDEX idx_edges_name_id RENAME TO idx_edges_value_id;
