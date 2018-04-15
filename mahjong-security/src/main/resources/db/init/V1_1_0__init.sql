CREATE TABLE IF NOT EXISTS mahjong_user (
  login          VARCHAR(512) NOT NULL PRIMARY KEY,
  password       VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_token (
    login     VARCHAR(512) NOT NULL,
    token     VARCHAR(128) NOT NULL,
    expire_at TIMESTAMP    NOT NULL,
    PRIMARY KEY(login, token)
)
