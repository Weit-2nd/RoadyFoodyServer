CREATE SEQUENCE food_category_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_categories
(
    id   NUMBER(19, 0) NOT NULL,
    name VARCHAR2(30)  NOT NULL,
    CONSTRAINT pk_food_categories PRIMARY KEY (id)
);

CREATE SEQUENCE food_spots_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots
(
    id               NUMBER(19, 0) NOT NULL,
    updated_datetime TIMESTAMP     NOT NULL,
    created_datetime TIMESTAMP     NOT NULL,
    name             VARCHAR2(20)  NOT NULL,
    food_truck       NUMBER(1)     NOT NULL,
    open             NUMBER(1)     NOT NULL,
    store_closure    NUMBER(1)     NOT NULL,
    point            SDO_GEOMETRY  NOT NULL,
    CONSTRAINT pk_food_spots PRIMARY KEY (id)
);

insert into user_sdo_geom_metadata(table_name, column_name, diminfo, srid)
values ('FOOD_SPOTS',
        'point',
        SDO_DIM_ARRAY(
                SDO_DIM_ELEMENT('Longitude', -180, 180, 0.5),
                SDO_DIM_ELEMENT('Latitude', -90, 90, 0.5)
        ),
        4326);

CREATE INDEX food_spots_point_index ON food_spots (point) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE SEQUENCE food_spots_food_categories_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots_food_categories
(
    id                 NUMBER(19, 0) NOT NULL,
    created_datetime   TIMESTAMP     NOT NULL,
    food_spots_id      NUMBER(19, 0),
    food_categories_id NUMBER(19, 0),
    CONSTRAINT pk_food_spots_food_categories PRIMARY KEY (id)
);

ALTER TABLE food_spots_food_categories
    ADD CONSTRAINT food_spots_food_categories_unique_constraint UNIQUE (food_spots_id, food_categories_id);

ALTER TABLE food_spots_food_categories
    ADD CONSTRAINT FK_FOOD_SPOTS_FOOD_CATEGORIES_ON_FOOD_CATEGORIES FOREIGN KEY (food_categories_id) REFERENCES food_categories (id);

ALTER TABLE food_spots_food_categories
    ADD CONSTRAINT FK_FOOD_SPOTS_FOOD_CATEGORIES_ON_FOOD_SPOTS FOREIGN KEY (food_spots_id) REFERENCES food_spots (id);

CREATE INDEX food_spots_food_categories_food_categories_id_index ON food_spots_food_categories (food_categories_id);

CREATE INDEX food_spots_food_categories_food_spots_id_index ON food_spots_food_categories (food_spots_id);

CREATE SEQUENCE food_spots_histories_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots_histories
(
    id               NUMBER(19, 0) NOT NULL,
    created_datetime TIMESTAMP     NOT NULL,
    food_spots_id    NUMBER(19, 0),
    user_id          NUMBER(19, 0),
    name             VARCHAR2(20)  NOT NULL,
    is_food_truck    NUMBER(1)     NOT NULL,
    is_open          NUMBER(1)     NOT NULL,
    store_closure    NUMBER(1)     NOT NULL,
    point            SDO_GEOMETRY  NOT NULL,
    CONSTRAINT pk_food_spots_histories PRIMARY KEY (id)
);

insert into user_sdo_geom_metadata(table_name, column_name, diminfo, srid)
values ('FOOD_SPOTS_HISTORIES',
        'point',
        SDO_DIM_ARRAY(
                SDO_DIM_ELEMENT('Longitude', -180, 180, 0.5),
                SDO_DIM_ELEMENT('Latitude', -90, 90, 0.5)
        ),
        4326);

CREATE INDEX food_spots_histories_point_index ON food_spots_histories (point) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

ALTER TABLE food_spots_histories
    ADD CONSTRAINT FK_FOOD_SPOTS_HISTORIES_ON_FOODSPOTS FOREIGN KEY (food_spots_id) REFERENCES food_spots (id);

ALTER TABLE food_spots_histories
    ADD CONSTRAINT FK_FOOD_SPOTS_HISTORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX food_spots_histories_food_spots_id_index ON food_spots_histories (food_spots_id);

CREATE INDEX food_spots_histories_user_id_index ON food_spots_histories (user_id);

CREATE TABLE food_spots_operation_hours
(
    food_spots_id    NUMBER(19, 0) NOT NULL,
    updated_datetime TIMESTAMP     NOT NULL,
    created_datetime TIMESTAMP     NOT NULL,
    day_of_week      NUMBER(5)     NOT NULL,
    opening_hours    VARCHAR2(5)   NOT NULL,
    closing_hours    VARCHAR2(5)   NOT NULL,
    CONSTRAINT pk_food_spots_operation_hours PRIMARY KEY (food_spots_id, day_of_week)
);

ALTER TABLE food_spots_operation_hours
    ADD CONSTRAINT FK_FOOD_SPOTS_OPERATION_HOURS_ON_FOOD_SPOTS FOREIGN KEY (food_spots_id) REFERENCES food_spots (id);

CREATE INDEX food_spots_operation_hours_food_spots_id_index ON food_spots_operation_hours (food_spots_id);

CREATE SEQUENCE food_spots_photos_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots_photos
(
    id                      NUMBER(19, 0) NOT NULL,
    created_datetime        TIMESTAMP     NOT NULL,
    food_spots_histories_id NUMBER(19, 0),
    file_name               VARCHAR2(30)  NOT NULL,
    CONSTRAINT pk_food_spots_photos PRIMARY KEY (id)
);

ALTER TABLE food_spots_photos
    ADD CONSTRAINT FK_FOOD_SPOTS_PHOTOS_ON_FOOD_SPOTS_HISTORIES FOREIGN KEY (food_spots_histories_id) REFERENCES food_spots_histories (id);

CREATE INDEX food_spots_photos_food_spots_histories_id_index ON food_spots_photos (food_spots_histories_id);

CREATE SEQUENCE food_spots_reviews_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots_reviews
(
    id               NUMBER(19, 0)  NOT NULL,
    created_datetime TIMESTAMP      NOT NULL,
    food_spot_id     NUMBER(19, 0),
    user_id          NUMBER(19, 0),
    rate             INTEGER        NOT NULL,
    contents         VARCHAR2(1200) NOT NULL,
    CONSTRAINT pk_food_spots_reviews PRIMARY KEY (id)
);

ALTER TABLE food_spots_reviews
    ADD CONSTRAINT FK_FOOD_SPOTS_REVIEWS_ON_FOOD_SPOT FOREIGN KEY (food_spot_id) REFERENCES food_spots (id);

ALTER TABLE food_spots_reviews
    ADD CONSTRAINT FK_FOOD_SPOTS_REVIEWS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX food_spots_reviews_food_spot_id_index ON food_spots_reviews (food_spot_id);

CREATE INDEX food_spots_reviews_user_id_index ON food_spots_reviews (user_id);

CREATE SEQUENCE food_spots_review_photos_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE food_spots_review_photos
(
    id                    NUMBER(19, 0) NOT NULL,
    created_datetime      TIMESTAMP     NOT NULL,
    food_spots_reviews_id NUMBER(19, 0),
    file_name             VARCHAR2(30)  NOT NULL,
    CONSTRAINT pk_food_spots_review_photos PRIMARY KEY (id)
);

ALTER TABLE food_spots_review_photos
    ADD CONSTRAINT FK_FOOD_SPOTS_REVIEW_PHOTOS_ON_FOOD_SPOTS_REVIEWS FOREIGN KEY (food_spots_reviews_id) REFERENCES food_spots_reviews (id);

CREATE INDEX food_spots_review_photos_food_spots_reviews_id_index ON food_spots_review_photos (food_spots_reviews_id);
