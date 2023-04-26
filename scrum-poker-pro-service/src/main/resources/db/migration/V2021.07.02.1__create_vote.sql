CREATE TABLE vote
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    story_point VARCHAR(3) NOT NULL,
    issue_id    UUID       NOT NULL,
    user_id     UUID       NOT NULL,
    created     TIMESTAMP  NOT NULL,
    modified    TIMESTAMP  NOT NULL,
    CONSTRAINT issue_id_user_id_unique UNIQUE (issue_id, user_id)
);