CREATE TABLE IF NOT EXISTS league (
  id          BIGSERIAL    NOT NULL PRIMARY KEY,
  name        JSONB        NOT NULL,
  description JSONB        NOT NULL,
  admins      BIGINT[]     NOT NULL
);

CREATE TABLE IF NOT EXISTS league_player (
  league_id BIGINT NOT NULL,
  player_id BIGINT NOT NULL,
  PRIMARY KEY(league_id, player_id)
);

CREATE TABLE IF NOT EXISTS league_game (
  league_id  BIGINT   NOT NULL,
  game_id    BIGINT   NOT NULL,
  player_ids BIGINT[] NOT NULL,
  PRIMARY KEY(league_id, game_id)
);

CREATE TABLE IF NOT EXISTS invitation(
  id         BIGSERIAL   NOT NULL PRIMARY KEY,
  league_id  BIGINT      NOT NULL,
  player_id  BIGINT      NOT NULL,
  code       VARCHAR(64) NOT NULL,
  created_by BIGINT      NOT NULL,
  created_at TIMESTAMP   NOT NULL,
  expire_at  TIMESTAMP   NOT NULL,
  status     VARCHAR(32) NOT NULL
);

CREATE UNIQUE INDEX ON invitation(league_id, player_id, status) WHERE status = 'ACTIVE';

CREATE TABLE IF NOT EXISTS join_request(
  id            BIGSERIAL    NOT NULL PRIMARY KEY,
  league_id     BIGINT       NOT NULL,
  player_id     BIGINT       NOT NULL,
  created_at    TIMESTAMP    NOT NULL,
  decision      VARCHAR(16)  NOT NULL,
  reason        VARCHAR(512) DEFAULT NULL,
  reviewed_by   BIGINT       DEFAULT NULL,
  reviewed_at   TIMESTAMP    DEFAULT NULL,
  expire_at     TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ON join_request(league_id, player_id, decision) WHERE decision = 'PENDING';
