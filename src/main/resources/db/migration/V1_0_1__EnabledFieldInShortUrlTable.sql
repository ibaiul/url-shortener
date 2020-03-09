ALTER TABLE short_url ADD COLUMN enabled bit NOT NULL AFTER url;

UPDATE short_url SET enabled = true;