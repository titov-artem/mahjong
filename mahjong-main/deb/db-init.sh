#!/bin/bash

echo "Check is db initialized"
IS_USER_EXISTS=$(psql postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='mahjong_main'")
if [[ ${IS_USER_EXISTS} != "1" ]]; then
  echo "Initializing database"
  echo "CREATE USER mahjong_main PASSWORD 'mahjong_main';" | psql
  echo "CREATE DATABASE mahjong_main WITH OWNER mahjong_main ENCODING 'utf-8';" | psql
fi
echo "Database initialized"