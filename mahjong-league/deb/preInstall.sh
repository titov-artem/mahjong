#!/bin/sh
echo "Creating group: mahjong-league"
/usr/sbin/groupadd -f -r mahjong-league 2> /dev/null || :

echo "Creating user: mahjong-league"
/usr/sbin/useradd -r -m -c "mahjong-league user" mahjong-league -g mahjong-league 2> /dev/null || :