DROP INDEX reviews_user_and_like_index;

ALTER TABLE food_spots_reviews
DROP
COLUMN "like";

ALTER TABLE food_spots_reviews
    ADD like_total INTEGER DEFAULT 0 NOT NULL;

CREATE INDEX reviews_user_and_like_total_index ON food_spots_reviews (user_id, like_total);
