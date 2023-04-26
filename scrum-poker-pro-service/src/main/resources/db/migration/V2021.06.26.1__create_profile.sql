CREATE TABLE profile
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID UNIQUE NOT NULL,
    name       VARCHAR(100),
    avatar_key UUID,
    created    TIMESTAMP   NOT NULL,
    modified   TIMESTAMP   NOT NULL
);