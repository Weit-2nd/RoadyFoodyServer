create
    sequence food_spots_review_flags_seq start
    with 1 increment by 1;

-- 리뷰 신고 내역
CREATE TABLE food_spots_review_flags
(
    id               NUMERIC(19, 0) NOT NULL,
    review_id        NUMERIC(19, 0) NOT NULL,
    user_id          NUMERIC(19, 0) NOT NULL,
    created_datetime TIMESTAMP      NOT NULL
);

ALTER TABLE food_spots_review_flags
    ADD CONSTRAINT food_spots_review_flags_pk PRIMARY KEY (id);

ALTER TABLE food_spots_review_flags
    ADD CONSTRAINT food_spots_review_flags_review_id_fk FOREIGN KEY (review_id) REFERENCES food_spots_reviews (id);

ALTER TABLE food_spots_review_flags
    ADD CONSTRAINT food_spots_review_flags_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE food_spots_review_flags
    ADD CONSTRAINT food_spots_review_flags_user_id_review_id_uq UNIQUE (user_id, review_id);

CREATE INDEX food_spots_review_flags_review_id_idx ON food_spots_review_flags (review_id);
