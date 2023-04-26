CREATE TABLE sticker_like
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sticker_id UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    created    TIMESTAMP NOT NULL,
    modified   TIMESTAMP NOT NULL
);