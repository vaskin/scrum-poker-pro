CREATE TABLE retro_column
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR    NOT NULL,
    color      VARCHAR(8) NOT NULL,
    position   INTEGER    NOT NULL,
    meeting_id UUID       NOT NULL,
    created    TIMESTAMP  NOT NULL,
    modified   TIMESTAMP  NOT NULL
);