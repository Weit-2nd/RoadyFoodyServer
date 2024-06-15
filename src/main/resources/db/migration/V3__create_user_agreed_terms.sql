create sequence user_agreed_terms_seq start with 1 increment by 1;

CREATE TABLE user_agreed_terms
(
    id               NUMERIC(19, 0) NOT NULL,
    user_id          NUMERIC(19, 0) NOT NULL,
    term_id          NUMERIC(19, 0) NOT NULL,
    created_datetime TIMESTAMP(6)   NOT NULL,
    CONSTRAINT user_agreed_terms_user_id_fk foreign key (user_id) REFERENCES users (id),
    CONSTRAINT user_agreed_terms_term_id_fk foreign key (term_id) REFERENCES terms (id)
);

ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_pk PRIMARY KEY (id);

ALTER TABLE user_agreed_terms
    ADD CONSTRAINT user_agreed_terms_user_id_term_id_uq UNIQUE (user_id, term_id);
