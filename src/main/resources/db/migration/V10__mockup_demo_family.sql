-- Mockup demo family seed data

-- Temporary lookup tables for deterministic references
CREATE TEMP TABLE demo_nodes (
    label TEXT PRIMARY KEY,
    node_id BIGINT NOT NULL
);

CREATE TEMP TABLE demo_edges (
    label TEXT PRIMARY KEY,
    edge_id BIGINT NOT NULL
);

CREATE TEMP TABLE demo_phones (
    label TEXT PRIMARY KEY,
    phone_id BIGINT NOT NULL
);

-- Nodes
WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Ayan', id FROM inserted;

WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Aigerim', id FROM inserted;

WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Serik', id FROM inserted;

WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Gulnara', id FROM inserted;

WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Dias', id FROM inserted;

WITH inserted AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
)
INSERT INTO demo_nodes (label, node_id)
SELECT 'Madina', id FROM inserted;

-- Node names
INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Ayan', now()
FROM demo_nodes
WHERE label = 'Ayan';

INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Aigerim', now()
FROM demo_nodes
WHERE label = 'Aigerim';

INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Serik', now()
FROM demo_nodes
WHERE label = 'Serik';

INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Gulnara', now()
FROM demo_nodes
WHERE label = 'Gulnara';

INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Dias', now()
FROM demo_nodes
WHERE label = 'Dias';

INSERT INTO node_values (node_id, value, created_at)
SELECT node_id, 'Madina', now()
FROM demo_nodes
WHERE label = 'Madina';

-- Public categories
WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), DATE '1980-07-05', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_nationality', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), DATE '1982-11-22', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_nationality', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Serik'), DATE '1950-04-12', DATE '2015-09-30')
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Serik'), DATE '2015-09-30', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_dead', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Serik'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Serik'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_nationality', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'), DATE '1955-02-10', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'gulnara_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'gulnara_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'gulnara_nationality', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Dias'), DATE '2008-03-18', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'dias_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Dias'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'dias_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Dias'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'dias_nationality', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Madina'), DATE '2012-08-09', NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'madina_alive', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Madina'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'madina_gender', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES (NULL, (SELECT node_id FROM demo_nodes WHERE label = 'Madina'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'madina_nationality', id FROM inserted;

-- Family relations
WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Serik'), (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_parent_ayan', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'), (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'gulnara_parent_ayan', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), (SELECT node_id FROM demo_nodes WHERE label = 'Dias'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_parent_dias', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), (SELECT node_id FROM demo_nodes WHERE label = 'Dias'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_parent_dias', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), (SELECT node_id FROM demo_nodes WHERE label = 'Madina'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_parent_madina', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), (SELECT node_id FROM demo_nodes WHERE label = 'Madina'), NULL, NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_parent_madina', id FROM inserted;

-- Private notes
WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Ayan'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'ayan_note', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'aigerim_note', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Serik'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'serik_note', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'gulnara_note', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Dias'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'dias_note', id FROM inserted;

WITH inserted AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    VALUES ((SELECT node_id FROM demo_nodes WHERE label = 'Madina'), NULL, now(), NULL)
    RETURNING id
)
INSERT INTO demo_edges (label, edge_id)
SELECT 'madina_note', id FROM inserted;

-- Edge values
INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '1980-07-05', NULL
FROM demo_edges
WHERE label = 'ayan_alive';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Male'
FROM demo_edges
WHERE label = 'ayan_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'ayan_nationality';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '1982-11-22', NULL
FROM demo_edges
WHERE label = 'aigerim_alive';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Female'
FROM demo_edges
WHERE label = 'aigerim_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'aigerim_nationality';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '1950-04-12', DATE '2015-09-30'
FROM demo_edges
WHERE label = 'serik_alive';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Dead', DATE '2015-09-30', NULL
FROM demo_edges
WHERE label = 'serik_dead';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Male'
FROM demo_edges
WHERE label = 'serik_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'serik_nationality';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '1955-02-10', NULL
FROM demo_edges
WHERE label = 'gulnara_alive';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Female'
FROM demo_edges
WHERE label = 'gulnara_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'gulnara_nationality';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '2008-03-18', NULL
FROM demo_edges
WHERE label = 'dias_alive';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Male'
FROM demo_edges
WHERE label = 'dias_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'dias_nationality';

INSERT INTO edge_values (edge_id, value, created_at, expired_at)
SELECT edge_id, 'Alive', DATE '2012-08-09', NULL
FROM demo_edges
WHERE label = 'madina_alive';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Female'
FROM demo_edges
WHERE label = 'madina_gender';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'Kazakh'
FROM demo_edges
WHERE label = 'madina_nationality';

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'FATHER'
FROM demo_edges
WHERE label IN (
    'serik_parent_ayan',
    'ayan_parent_dias',
    'ayan_parent_madina'
);

INSERT INTO edge_values (edge_id, value)
SELECT edge_id, 'MOTHER'
FROM demo_edges
WHERE label IN (
    'gulnara_parent_ayan',
    'aigerim_parent_dias',
    'aigerim_parent_madina'
);

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Worked as an engineer', now()
FROM demo_edges
WHERE label = 'serik_note';

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Moved to Almaty in 2010', now()
FROM demo_edges
WHERE label = 'gulnara_note';

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Leads a small design studio', now()
FROM demo_edges
WHERE label = 'ayan_note';

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Loves hiking', now()
FROM demo_edges
WHERE label = 'aigerim_note';

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Plays football on weekends', now()
FROM demo_edges
WHERE label = 'dias_note';

INSERT INTO edge_values (edge_id, value, created_at)
SELECT edge_id, 'Enjoys painting and music', now()
FROM demo_edges
WHERE label = 'madina_note';

-- Phones
WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Ayan'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'ayan_phone', id FROM inserted;

WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Aigerim'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'aigerim_phone', id FROM inserted;

WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Serik'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'serik_phone', id FROM inserted;

WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Gulnara'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'gulnara_phone', id FROM inserted;

WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Dias'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'dias_phone', id FROM inserted;

WITH inserted AS (
    INSERT INTO phones (pattern_id, node_id)
    VALUES ((SELECT id FROM phone_patterns WHERE code = 'KZ'), (SELECT node_id FROM demo_nodes WHERE label = 'Madina'))
    RETURNING id
)
INSERT INTO demo_phones (label, phone_id)
SELECT 'madina_phone', id FROM inserted;

-- Phone values
INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 01', now()
FROM demo_phones
WHERE label = 'ayan_phone';

INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 02', now()
FROM demo_phones
WHERE label = 'aigerim_phone';

INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 03', now()
FROM demo_phones
WHERE label = 'serik_phone';

INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 04', now()
FROM demo_phones
WHERE label = 'gulnara_phone';

INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 05', now()
FROM demo_phones
WHERE label = 'dias_phone';

INSERT INTO phone_values (phone_id, value, created_at)
SELECT phone_id, '+7 (701) - 111 - 11 - 06', now()
FROM demo_phones
WHERE label = 'madina_phone';

DROP TABLE demo_phones;
DROP TABLE demo_edges;
DROP TABLE demo_nodes;
