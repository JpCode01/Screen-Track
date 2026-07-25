CREATE TABLE favorites (

                           id BIGSERIAL PRIMARY KEY,

                           position INTEGER NOT NULL,

                           user_id BIGINT NOT NULL,

                           media_id BIGINT NOT NULL,


                           CONSTRAINT fk_favorites_user
                               FOREIGN KEY (user_id)
                                   REFERENCES usuarios(id)
                                   ON DELETE CASCADE,


                           CONSTRAINT fk_favorites_media
                               FOREIGN KEY (media_id)
                                   REFERENCES medias(id)
                                   ON DELETE CASCADE,


                           CONSTRAINT uk_favorites_user_media
                               UNIQUE (user_id, media_id)

);