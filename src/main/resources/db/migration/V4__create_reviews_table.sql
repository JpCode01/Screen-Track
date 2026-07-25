CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         rating VARCHAR(20) NOT NULL,
                         comment VARCHAR(2000),

                         user_id BIGINT NOT NULL,
                         media_id BIGINT NOT NULL,

                         CONSTRAINT fk_review_user
                             FOREIGN KEY (user_id)
                                 REFERENCES usuarios(id),

                         CONSTRAINT fk_review_media
                             FOREIGN KEY (media_id)
                                 REFERENCES medias(id),

                         CONSTRAINT uk_review_user_media
                             UNIQUE (user_id, media_id)
);