CREATE INDEX food_spots_name_index ON food_spots (name);

CREATE INDEX food_spots_id_food_categories_id_index ON food_spots_food_categories (food_spots_id, food_categories_id);


