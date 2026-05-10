BEGIN TRANSACTION;

-- =========================
-- WATCHED ITEM (user data)
-- =========================

DROP TABLE IF EXISTS watched_item CASCADE;
DROP TABLE IF EXISTS content_metadata CASCADE;

DROP SEQUENCE IF EXISTS seq_watched_item_id;

CREATE SEQUENCE seq_watched_item_id
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    CACHE 1;

CREATE TABLE watched_item (
                              watched_item_id INT DEFAULT nextval('seq_watched_item_id'::regclass) PRIMARY KEY,
                              imdb_id VARCHAR(20) NOT NULL,
                              rating INT CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
                              review TEXT,
                              watched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT unique_watched_item_imdb_id UNIQUE (imdb_id)
);

-- =========================
-- CONTENT METADATA (cached OMDb data)
-- =========================

CREATE TABLE content_metadata (
                                  imdb_id VARCHAR(20) PRIMARY KEY,
                                  title VARCHAR(255) NOT NULL,
                                  year VARCHAR(20),
                                  rated VARCHAR(10),
                                  released VARCHAR(50),
                                  runtime VARCHAR(20),
                                  genre VARCHAR(100),
                                  director VARCHAR(255),
                                  actors VARCHAR(500),
                                  plot TEXT,
                                  language VARCHAR(100),
                                  media_type VARCHAR(50),
                                  poster_url TEXT,
                                  imdb_rating VARCHAR(10),
                                  metascore VARCHAR(10),
                                  last_fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE watched_item
    ADD CONSTRAINT fk_watched_item_imdb
        FOREIGN KEY (imdb_id)
            REFERENCES content_metadata(imdb_id)
            ON DELETE CASCADE;

COMMIT TRANSACTION;