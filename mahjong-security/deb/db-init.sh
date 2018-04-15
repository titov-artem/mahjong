#!/bin/bash

echo "Check is db initialized"
IS_USER_EXISTS=$(psql postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='mahjong_auth'")
if [[ ${IS_USER_EXISTS} != "1" ]]; then
  echo "Initializing database"
  echo "CREATE USER mahjong_auth PASSWORD 'mahjong_auth';" | psql
  echo "CREATE DATABASE mahjong_auth WITH OWNER mahjong_auth ENCODING 'utf-8';" | psql
fi
echo "Database initialized"