CREATE TABLE group_invite_member
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(100) NOT NULL,
    group_invite_id UUID         NOT NULL,
    created         TIMESTAMP    NOT NULL,
    modified        TIMESTAMP    NOT NULL
);