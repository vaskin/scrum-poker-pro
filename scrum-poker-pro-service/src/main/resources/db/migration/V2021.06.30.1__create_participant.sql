CREATE TABLE participant
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(100) NOT NULL,
    user_id       UUID         NOT NULL,
    meeting_id    UUID         NOT NULL,
    last_activity TIMESTAMP    NOT NULL,
    created       TIMESTAMP    NOT NULL,
    modified      TIMESTAMP    NOT NULL
);