-- terms
create sequence terms_seq start with 1 increment by 1;

CREATE TABLE terms
(
    id               NUMERIC(19, 0)      NOT NULL,
    title            VARCHAR2(90)        NOT NULL,
    content          CLOB                NOT NULL,
    required         NUMBER(1) DEFAULT 0 NOT NULL,
    created_datetime TIMESTAMP(6)        NOT NULL,
    updated_datetime TIMESTAMP(6)        NOT NULL
);

ALTER TABLE terms
    ADD CONSTRAINT terms_pk PRIMARY KEY (id);

ALTER TABLE terms
    ADD CONSTRAINT terms_title_uq UNIQUE (title);

CREATE INDEX terms_required_idx ON terms (required);

-- user agreed terms
create sequence user_agreed_terms_seq start with 1 increment by 1;

CREATE TABLE user_agreed_terms
(
    id               NUMERIC(19, 0) NOT NULL,
    user_id          NUMERIC(19, 0) NOT NULL,
    term_id          NUMERIC(19, 0) NOT NULL,
    created_datetime TIMESTAMP(6)   NOT NULL
);

ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_pk PRIMARY KEY (id);

ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_user_id_term_id_uq UNIQUE (user_id, term_id);

ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_user_id_fk foreign key (user_id) REFERENCES users (id);
ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_term_id_fk foreign key (term_id) REFERENCES terms (id);
