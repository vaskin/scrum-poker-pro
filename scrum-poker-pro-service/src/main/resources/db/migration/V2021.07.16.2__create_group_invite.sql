CREATE TABLE group_invite
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR     NOT NULL,
    user_id       UUID        NOT NULL,
    created       TIMESTAMP   NOT NULL,
    modified      TIMESTAMP   NOT NULL
);