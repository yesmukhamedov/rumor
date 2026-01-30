CREATE INDEX IF NOT EXISTS idx_edges_from_id ON edges (from_id);
CREATE INDEX IF NOT EXISTS idx_edges_to_id ON edges (to_id);
CREATE INDEX IF NOT EXISTS idx_edges_public_to ON edges (to_id)
    WHERE from_id IS NULL AND to_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_edges_private_from ON edges (from_id)
    WHERE to_id IS NULL AND from_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_edges_created_at ON edges (created_at);
CREATE INDEX IF NOT EXISTS idx_edges_expired_at ON edges (expired_at);

CREATE INDEX IF NOT EXISTS idx_node_values_node_created ON node_values (node_id, created_at);
CREATE INDEX IF NOT EXISTS idx_edge_values_edge_created ON edge_values (edge_id, created_at);
CREATE INDEX IF NOT EXISTS idx_phone_values_phone_created ON phone_values (phone_id, created_at);
