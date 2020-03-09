CREATE TABLE short_url (
    id CHAR(36) NOT NULL,
    short_key VARCHAR(255) NOT NULL,
    url VARCHAR(1020) NOT NULL,
    created_on DATETIME(6),
    PRIMARY KEY (id)
) engine=InnoDB;

ALTER TABLE short_url ADD CONSTRAINT UK_short_key UNIQUE KEY(short_key);