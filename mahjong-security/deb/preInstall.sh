#!/bin/sh
echo "Creating group: mahjong-security"
/usr/sbin/groupadd -f -r mahjong-security 2> /dev/null || :

echo "Creating user: mahjong-security"
/usr/sbin/useradd -r -m -c "mahjong-security user" mahjong-security -g mahjong-security 2> /dev/null || :