CREATE TABLE retro_template
(
    id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    columns  JSONB     NOT NULL,
    user_id  UUID      NOT NULL,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);