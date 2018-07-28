#!/bin/sh
echo "Creating group: mahjong-main"
/usr/sbin/groupadd -f -r mahjong-main 2> /dev/null || :

echo "Creating user: mahjong-main"
/usr/sbin/useradd -r -m -c "mahjong-main user" mahjong-main -g mahjong-main 2> /dev/null || :