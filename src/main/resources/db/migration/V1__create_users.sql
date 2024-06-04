create sequence users_seq start with 1 increment by 1;

CREATE TABLE users (
                       id NUMERIC(19, 0) NOT NULL,
                       nickname VARCHAR2(48) NOT NULL,
                       created_datetime TIMESTAMP(6)  NOT NULL,
                       updated_datetime TIMESTAMP(6) NOT NULL
);

ALTER TABLE users ADD CONSTRAINT users_pk PRIMARY KEY (id);
ALTER TABLE users ADD CONSTRAINT users_nickname_uq UNIQUE (nickname);
