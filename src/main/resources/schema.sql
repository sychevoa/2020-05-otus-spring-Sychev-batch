DROP TABLE IF EXISTS PLAYER;
DROP TABLE IF EXISTS COUNTRY;
DROP TABLE IF EXISTS TEAM;

CREATE TABLE COUNTRY  (
    id BIGSERIAL IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    nosql_id VARCHAR (255)
);

CREATE TABLE TEAM  (
    id BIGSERIAL IDENTITY NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    nosql_id VARCHAR (255)
);

CREATE TABLE PLAYER  (
    id BIGSERIAL IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    country_id BIGINT references COUNTRY (id),
    team_id BIGINT references TEAM (id)
);
