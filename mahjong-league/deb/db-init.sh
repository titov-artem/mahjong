#!/bin/bash

echo "Check is db initialized"
IS_USER_EXISTS=$(psql postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='mahjong_league'")
if [[ ${IS_USER_EXISTS} != "1" ]]; then
  echo "Initializing database"
  echo "CREATE USER mahjong_league PASSWORD 'mahjong_league';" | psql
  echo "CREATE DATABASE mahjong_league WITH OWNER mahjong_league ENCODING 'utf-8';" | psql
fi
echo "Database initialized"