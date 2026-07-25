CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,

                          name VARCHAR(100) NOT NULL,

                          email VARCHAR(255) NOT NULL UNIQUE,

                          password VARCHAR(255) NOT NULL,

                          username VARCHAR(50) NOT NULL UNIQUE,

                          bio TEXT,

                          token_verification VARCHAR(255),

                          creation_date TIMESTAMP NOT NULL,

                          expiration_date_token TIMESTAMP,

                          active BOOLEAN NOT NULL DEFAULT FALSE,

                          verified BOOLEAN NOT NULL DEFAULT FALSE,

                          role VARCHAR(20) NOT NULL
);