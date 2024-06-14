create sequence terms_seq start with 1 increment by 1;

CREATE TABLE terms
(
    id               NUMERIC(19, 0)      NOT NULL,
    title            VARCHAR2(90)        NOT NULL,
    content          CLOB                NOT NULL,
    required_flag     NUMBER(1) DEFAULT 0 NOT NULL,
    created_datetime TIMESTAMP(6)        NOT NULL,
    updated_datetime TIMESTAMP(6)        NOT NULL
);


ALTER TABLE terms
    ADD CONSTRAINT terms_pk PRIMARY KEY (id);
ALTER TABLE terms
    ADD CONSTRAINT terms_title_uq UNIQUE (title);
