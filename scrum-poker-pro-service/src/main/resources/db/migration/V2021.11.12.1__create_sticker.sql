CREATE TABLE sticker
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    text            VARCHAR   NOT NULL,
    position        INTEGER   NOT NULL,
    retro_column_id UUID      NOT NULL,
    user_id         UUID      NOT NULL,
    user_name       VARCHAR   NOT NULL,
    created         TIMESTAMP NOT NULL,
    modified        TIMESTAMP NOT NULL
);