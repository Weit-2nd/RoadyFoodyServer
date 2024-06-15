CREATE INDEX terms_required_flag_idx ON terms (required_flag);

ALTER TABLE user_agreed_terms
    ADD (
        agreed_flag NUMBER(1) DEFAULT 1 NOT NULL,
        updated_datetime TIMESTAMP(6) DEFAULT SYSTIMESTAMP NOT NULL
        );

CREATE INDEX user_agreed_terms_agreed_flag_idx ON user_agreed_terms (agreed_flag);
CREATE INDEX user_agreed_terms_user_id_idx ON user_agreed_terms (user_id);
CREATE INDEX user_agreed_terms_term_id_idx ON user_agreed_terms (term_id);
