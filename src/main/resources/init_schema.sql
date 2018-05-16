PRAGMA foreign_keys = off;
BEGIN TRANSACTION;
DROP TABLE IF EXISTS meta;
CREATE TABLE meta
(
    id INTEGER PRIMARY KEY UNIQUE NOT NULL,
    name VARCHAR(64) DEFAULT '' NOT NULL,
    value text DEFAULT null ,
    owner INTEGER DEFAULT 0 NOT NULL,
    create_time bigint DEFAULT 0 NOT NULL,
    update_time bigint DEFAULT 0 NOT NULL,
    expire_time bigint DEFAULT 0 NOT NULL,
    status tinyint DEFAULT 0 NOT NULL
);
CREATE UNIQUE INDEX idx_name ON meta (name);

DROP TABLE IF EXISTS user;
create table user
(
  id INTEGER PRIMARY KEY UNIQUE NOT NULL,
  account     varchar(64) default ''  not null,
  name        varchar(64) default ''  not null,
  third_id    varchar(64) default ''  not null,
  more_info   text                    null,
  email       varchar(200) default '' null,
  oauth_from  int default '1'         not null,
  avatar_url  varchar(500) default '' null,
  create_time bigint default '0'      not null,
  update_time bigint default '0'      not null,
  constraint account_oauth_from_index
  unique (account, oauth_from)
);

DROP TABLE IF EXISTS device;
create table device
(
  id          varchar(64) default ''  not null,
  user_id     varchar(64) default ''  not null,
  name        varchar(64) default ''  not null,
  create_time bigint default '0'      not null,
  update_time bigint default '0'      not null,
  unique (id)
);

DROP TABLE IF EXISTS task;
CREATE TABLE task
(
    id INTEGER PRIMARY KEY UNIQUE NOT NULL,
    topic varchar(32) DEFAULT '' NOT NULL,
    contents text null ,
    owner int DEFAULT 0 NOT NULL,
    remark varchar(500) DEFAULT '' not null ,
    status int DEFAULT 0 NOT NULL,
    session_id varchar(64) DEFAULT '' NOT NULL,
    create_time bigint DEFAULT 0 NOT NULL,
    update_time bigint DEFAULT 0 NOT NULL
);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
