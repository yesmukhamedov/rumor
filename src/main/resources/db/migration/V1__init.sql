CREATE TABLE nodes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE edges (
    id BIGSERIAL PRIMARY KEY,
    from_id BIGINT NOT NULL,
    to_id BIGINT NOT NULL,
    CONSTRAINT fk_edges_from_node FOREIGN KEY (from_id) REFERENCES nodes (id) ON DELETE CASCADE,
    CONSTRAINT fk_edges_to_node FOREIGN KEY (to_id) REFERENCES nodes (id) ON DELETE CASCADE,
    CONSTRAINT chk_edges_no_self_loop CHECK (from_id <> to_id),
    CONSTRAINT uq_edges_from_to UNIQUE (from_id, to_id)
);

CREATE INDEX idx_edges_from_id ON edges (from_id);
CREATE INDEX idx_edges_to_id ON edges (to_id);
