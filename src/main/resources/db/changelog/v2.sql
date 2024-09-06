-- liquibase formatted sql

-- changeset Tuf_15:1725631413148-1
CREATE TABLE file (id UUID NOT NULL, description VARCHAR(32) NOT NULL, creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, title VARCHAR(255) NOT NULL, file_data OID, CONSTRAINT pk_file PRIMARY KEY (id));

