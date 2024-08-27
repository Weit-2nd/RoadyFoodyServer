ALTER TABLE users
    ADD badge VARCHAR2(30) DEFAULT 'BEGINNER' NOT NULL;

CREATE INDEX users_badge_idx ON users (badge);

create
    sequence user_promotion_reward_histories_seq start
    with 1 increment by 1;

CREATE TABLE user_promotion_reward_histories
(
    id               NUMERIC(19, 0) NOT NULL,
    user_id          NUMERIC(19, 0) NOT NULL,
    badge            VARCHAR2(30)   NOT NULL,
    created_datetime TIMESTAMP(6)   NOT NULL
);

ALTER TABLE user_promotion_reward_histories
    ADD CONSTRAINT user_promotion_reward_histories_pk PRIMARY KEY (id);

ALTER TABLE user_promotion_reward_histories
    ADD CONSTRAINT user_promotion_reward_histories_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_promotion_reward_histories
    ADD CONSTRAINT user_promotion_reward_histories_user_id_badge_uk UNIQUE (user_id, badge);

