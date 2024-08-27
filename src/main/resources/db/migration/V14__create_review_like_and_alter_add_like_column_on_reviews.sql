ALTER TABLE food_spots_reviews
    ADD "like" INTEGER DEFAULT 0 NOT NULL;

CREATE INDEX reviews_user_and_like_index ON food_spots_reviews (user_id, "like");

CREATE TABLE review_likes
(
    review_id        NUMBER(19, 0) NOT NULL,
    user_id          NUMBER(19, 0) NOT NULL,
    created_datetime TIMESTAMP(6)  NOT NULL,
    CONSTRAINT pk_review_like PRIMARY KEY (review_id, user_id)
);

ALTER TABLE review_likes
    ADD CONSTRAINT FK_REVIEW_LIKE_ON_REVIEW FOREIGN KEY (review_id) REFERENCES food_spots_reviews (id);

CREATE INDEX reviews_like_review_id_index ON review_likes (review_id);

ALTER TABLE review_likes
    ADD CONSTRAINT FK_REVIEW_LIKE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX reviews_like_user_id_index ON review_likes (user_id);
