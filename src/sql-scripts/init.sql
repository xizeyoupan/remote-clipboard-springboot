DROP table if EXISTS user;
CREATE TABLE user
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(30) NULL DEFAULT NULL,
    password VARCHAR(35) NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP table if EXISTS file;
CREATE TABLE file
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    create_time   BIGINT NULL DEFAULT NULL,
    last_modified BIGINT NULL DEFAULT NULL,
    uuid          VARCHAR(40) NULL DEFAULT NULL,
    mime_type     VARCHAR(30) NULL DEFAULT NULL,
    path          VARCHAR(1024) NULL DEFAULT NULL,
    storage       VARCHAR(64) NULL DEFAULT NULL,
    type          VARCHAR(64) NULL DEFAULT NULL,
    visibility    VARCHAR(64) NULL DEFAULT NULL,
    basename      VARCHAR(1024) NULL DEFAULT NULL,
    extension     VARCHAR(64) NULL DEFAULT NULL,
    user_id       BIGINT NULL DEFAULT NULL,
    file_size     BIGINT NULL DEFAULT NULL,
    PRIMARY KEY (id)
);
