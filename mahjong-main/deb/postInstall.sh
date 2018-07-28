#!/bin/bash
# fix dir ownership
chown mahjong-main:mahjong-main /var/log/mahjong-main/

# configure postgresql
su postgres -c /etc/mahjong-main/db-init.sh

# reload systemd daemon to read configuration and start application
systemctl daemon-reload
systemctl enable mahjong-main
systemctl start mahjong-main