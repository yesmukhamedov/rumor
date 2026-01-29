ALTER TABLE edges ALTER COLUMN to_id DROP NOT NULL;

ALTER TABLE edges DROP CONSTRAINT chk_edges_no_self_loop;
ALTER TABLE edges
    ADD CONSTRAINT chk_edges_no_self_loop CHECK (
        from_id IS NULL
        OR to_id IS NULL
        OR from_id <> to_id
    );
