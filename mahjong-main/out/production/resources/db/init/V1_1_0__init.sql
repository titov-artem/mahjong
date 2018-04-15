CREATE TABLE IF NOT EXISTS player (
  id    BIGSERIAL    NOT NULL PRIMARY KEY,
  login VARCHAR(512) NOT NULL,
  name  VARCHAR(512) NOT NULL,
  lang  VARCHAR(8)   NOT NULL
);

CREATE UNIQUE INDEX ON player (login);

CREATE TABLE IF NOT EXISTS game (
  id           BIGSERIAL NOT NULL PRIMARY KEY,
  player_id1   BIGINT    NOT NULL,
  player_id2   BIGINT    NOT NULL,
  player_id3   BIGINT    NOT NULL,
  player_id4   BIGINT    NOT NULL,
  game_data    JSONB     NOT NULL,
  final_score  INT []    NOT NULL,
  is_completed BOOLEAN   NOT NULL
);