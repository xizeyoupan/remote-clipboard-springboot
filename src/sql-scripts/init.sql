DROP table if EXISTS user;
CREATE TABLE user
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(30) NULL DEFAULT NULL,
    password VARCHAR(35) NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP table if EXISTS clip;
CREATE TABLE clip
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    create_time       DATETIME NULL DEFAULT NULL,
    modify_time       DATETIME NULL DEFAULT NULL,
    content_type      VARCHAR(30) NULL DEFAULT NULL,
    status           INT NULL DEFAULT NULL,
    file_name         VARCHAR(260) NULL DEFAULT NULL,
    uuid             VARCHAR(40) NULL DEFAULT NULL,
    oss_name          VARCHAR(30) NULL DEFAULT NULL,
    file_name_in_remote VARCHAR(260) NULL DEFAULT NULL,
    user_id           BIGINT NULL DEFAULT NULL,
    size             BIGINT NULL DEFAULT NULL,
    PRIMARY KEY (id)
);