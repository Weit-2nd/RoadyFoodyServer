CREATE TABLE report_operation_hours
(
    food_spots_history_id NUMBER(38, 0) NOT NULL,
    created_datetime      TIMESTAMP     NOT NULL,
    day_of_week           NUMBER(5)     NOT NULL,
    opening_hours         VARCHAR2(5)   NOT NULL,
    closing_hours         VARCHAR2(5)   NOT NULL,
    CONSTRAINT pk_report_operation_hours PRIMARY KEY (food_spots_history_id, day_of_week)
);

ALTER TABLE report_operation_hours
    ADD CONSTRAINT FK_REPORT_OPERATION_HOURS_ON_FOOD_SPOTS_HISTORY FOREIGN KEY (food_spots_history_id) REFERENCES food_spots_histories (id);

CREATE INDEX report_operation_hours_food_spots_history_id_index ON report_operation_hours (food_spots_history_id);

CREATE SEQUENCE report_food_categories_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE report_food_categories
(
    id                    NUMBER(38, 0) NOT NULL,
    created_datetime      TIMESTAMP     NOT NULL,
    food_spots_history_id NUMBER(38, 0),
    food_categories_id    NUMBER(38, 0),
    CONSTRAINT pk_report_food_categories PRIMARY KEY (id)
);

ALTER TABLE report_food_categories
    ADD CONSTRAINT FK_REPORT_FOOD_CATEGORIES_ON_FOOD_CATEGORIES FOREIGN KEY (food_categories_id) REFERENCES food_categories (id);

ALTER TABLE report_food_categories
    ADD CONSTRAINT FK_REPORT_FOOD_CATEGORIES_ON_FOOD_SPOTS_HISTORY FOREIGN KEY (food_spots_history_id) REFERENCES food_spots_histories (id);

CREATE INDEX report_food_categories_food_categories_id_index ON report_food_categories (food_categories_id);

CREATE INDEX report_food_categories_food_spots_history_id_index ON report_food_categories (food_spots_history_id);
