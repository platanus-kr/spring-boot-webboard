-- boards Table Create SQL
CREATE TABLE boards
(
    `ID`           INT             NOT NULL    AUTO_INCREMENT,
    `NAME`         VARCHAR(255)    NULL,
    `DISCRIPTION`  VARCHAR(255)    NULL,
    CONSTRAINT PK_boards PRIMARY KEY (ID)
);


-- users Table Create SQL
CREATE TABLE users
(
    `ID`        INT             NOT NULL    AUTO_INCREMENT,
    `USERNAME`  VARCHAR(255)    NULL,
    `PASSWORD`  VARCHAR(255)    NULL,
    `NICKNAME`  VARCHAR(255)    NULL,
    `EMAIL`     VARCHAR(255)    NULL,
    CONSTRAINT PK_users PRIMARY KEY (ID)
);


-- articles Table Create SQL
CREATE TABLE articles
(
    `ID`             INT             NOT NULL    AUTO_INCREMENT,
    `BOARD_ID`       INT             NULL,
    `CONTENT`        TEXT            NULL,
    `AUTHOR_ID`      INT             NULL,
    `CREATED_DATE`   DATETIME        NULL,
    `MODIFIED_DATE`  DATETIME        NULL,
    `DELETED`        TINYINT         NULL,
    `TITLE`          VARCHAR(255)    NULL,
    CONSTRAINT PK_articles PRIMARY KEY (ID)
);

ALTER TABLE articles
    ADD CONSTRAINT FK_articles_BOARD_ID_boards_ID FOREIGN KEY (BOARD_ID)
        REFERENCES boards (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE articles
    ADD CONSTRAINT FK_articles_AUTHOR_ID_users_ID FOREIGN KEY (AUTHOR_ID)
        REFERENCES users (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;


-- comments Table Create SQL
CREATE TABLE comments
(
    `ID`             INT         NOT NULL    AUTO_INCREMENT,
    `ARTICLE_ID`     INT         NULL,
    `CONTENT`        TEXT        NULL,
    `AUTHOR_ID`      INT         NULL,
    `CREATED_DATE`   DATETIME    NULL,
    `MODIFIED_DATE`  DATETIME    NULL,
    `DELETED`        TINYINT     NULL,
    CONSTRAINT PK_comments PRIMARY KEY (ID)
);

ALTER TABLE comments
    ADD CONSTRAINT FK_comments_ARTICLE_ID_articles_ID FOREIGN KEY (ARTICLE_ID)
        REFERENCES articles (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE comments
    ADD CONSTRAINT FK_comments_AUTHOR_ID_users_ID FOREIGN KEY (AUTHOR_ID)
        REFERENCES users (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;


