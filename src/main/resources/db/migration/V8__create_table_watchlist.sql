CREATE TABLE watchlists (
                            id BIGSERIAL PRIMARY KEY,

                            user_id BIGINT NOT NULL,
                            media_id BIGINT NOT NULL,

                            CONSTRAINT fk_watchlists_user
                                FOREIGN KEY (user_id)
                                    REFERENCES usuarios(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_watchlists_media
                                FOREIGN KEY (media_id)
                                    REFERENCES medias(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT uk_user_media_watchlist
                                UNIQUE (user_id, media_id)
);

CREATE INDEX idx_watchlists_user
    ON watchlists(user_id);