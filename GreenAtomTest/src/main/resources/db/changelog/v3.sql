-- liquibase formatted sql

-- changeset Tuf_15:1725694489762-1
CREATE TABLE file (id UUID NOT NULL, title VARCHAR(255) NOT NULL, description VARCHAR(32) NOT NULL, creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, file_data OID, CONSTRAINT pk_file PRIMARY KEY (id));

