BEGIN TRANSACTION;

DROP TABLE IF EXISTS watched_item CASCADE;
DROP SEQUENCE IF EXISTS seq_watched_item_id;

CREATE SEQUENCE seq_watched_item_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    CACHE 1;

CREATE TABLE watched_item (
                              watched_item_id INT DEFAULT nextval('seq_watched_item_id'::regclass) PRIMARY KEY,
                              imdb_id VARCHAR(20) NOT NULL,
--                               title VARCHAR(255) NOT NULL,
--                               director VARCHAR(255) NOT NULL,
--                               plot VARCHAR(800) NOT NULL,
--                               actors VARCHAR(255) NOT NULL,
--                               genre VARCHAR(20) NOT NULL,
--                               release_year VARCHAR(20),
--                               media_type VARCHAR(50),
--                               language VARCHAR(20) NOT NULL,
--                               poster_url TEXT,
                              rating INT CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
                              review TEXT,
                              watched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT unique_watched_item_imdb_id UNIQUE (imdb_id)
);

COMMIT TRANSACTION;