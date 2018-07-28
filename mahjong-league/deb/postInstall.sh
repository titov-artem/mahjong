#!/bin/bash
# fix dir ownership
chown mahjong-league:mahjong-league /var/log/mahjong-league/

# configure postgresql
su postgres -c /etc/mahjong-league/db-init.sh

# reload systemd daemon to read configuration and start application
systemctl daemon-reload
systemctl enable mahjong-league
systemctl start mahjong-league