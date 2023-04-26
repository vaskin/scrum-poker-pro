CREATE TABLE issue
(
    id           UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    jira_id      VARCHAR(100),
    key          VARCHAR(100),
    link         VARCHAR(100),
    title        VARCHAR     NOT NULL,
    story_point  VARCHAR(3),
    synchronized BOOLEAN     NOT NULL DEFAULT FALSE,
    description  VARCHAR,
    status       VARCHAR(30),
    type         VARCHAR(20) NOT NULL,
    parent_id    UUID,
    user_id      UUID        NOT NULL,
    meeting_id   UUID        NOT NULL,
    created      TIMESTAMP   NOT NULL,
    modified     TIMESTAMP   NOT NULL
);