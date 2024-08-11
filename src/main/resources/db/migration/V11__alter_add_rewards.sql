ALTER TABLE REWARDS
    ADD CONSTRAINT UQ_REWARDS_ON_FOOD_SPOTS_HISTORIES
        UNIQUE (food_spots_history_id);

ALTER TABLE REWARDS
    ADD COLUMN reward_type NUMBER(1) NOT NULL;

ALTER TABLE REWARDS ADD COLUMN reward_reason VARCHAR2(20) NOT NULL;