CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE meeting
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR     NOT NULL,
    type          VARCHAR(10) NOT NULL,
    voting_system VARCHAR(15),
    user_id       UUID        NOT NULL,
    created       TIMESTAMP   NOT NULL,
    modified      TIMESTAMP   NOT NULL
);