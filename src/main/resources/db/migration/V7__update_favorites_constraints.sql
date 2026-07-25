ALTER TABLE favorites
DROP CONSTRAINT IF EXISTS uk_user_position_type;
ALTER TABLE favorites
    ADD CONSTRAINT uk_user_media
        UNIQUE (user_id, media_id);