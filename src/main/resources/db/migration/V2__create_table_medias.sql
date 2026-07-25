CREATE TABLE medias (
                        id BIGSERIAL PRIMARY KEY,
                        imdb_id VARCHAR(20) NOT NULL UNIQUE,
                        title VARCHAR(255) NOT NULL,
                        year VARCHAR(10),
                        type VARCHAR(20),
                        poster VARCHAR(500)
);