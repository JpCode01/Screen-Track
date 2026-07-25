ALTER TABLE favorites
    ADD COLUMN media_type VARCHAR(20);

UPDATE favorites f
SET media_type = m.type
    FROM medias m
WHERE f.media_id = m.id;

ALTER TABLE favorites
    ALTER COLUMN media_type SET NOT NULL;

SELECT
    user_id,
    position,
    media_id,
    COUNT(*)
FROM favorites
GROUP BY user_id, position, media_id
HAVING COUNT(*) > 1;
SELECT
    user_id,
    position,
    media_type,
    COUNT(*)
FROM favorites
GROUP BY user_id, position, media_type
HAVING COUNT(*) > 1;
ALTER TABLE favorites
    ADD CONSTRAINT uk_user_position_type
        UNIQUE (user_id, position, media_type);