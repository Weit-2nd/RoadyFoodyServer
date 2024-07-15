CREATE SEQUENCE rewards_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE rewards
(
    id              NUMBER(19, 0) NOT NULL,
    user_id BIGINT NOT NULL,
    food_spots_history_id BIGINT NOT NULL,
    reward_point INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_rewards PRIMARY KEY (id)
);

ALTER TABLE rewards
    ADD CONSTRAINT FK_REWARDS_ON_USERS FOREIGN KEY (user_id) REFERENCES users (id);

AlTER TABLE rewards
    ADD CONSTRAINT FK_REWARDS_ON_FOOD_SPOTS_HISTORIES FOREIGN KEY (food_spots_history_id) REFERENCES food_spots_histories (id);

CREATE INDEX rewards_user_id_index ON rewards (user_id);
CREATE INDEX rewards_food_spots_history_id_index ON rewards (food_spots_history_id);
