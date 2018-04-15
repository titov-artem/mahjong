#!/bin/bash
# fix dir ownership
chown mahjong-security:mahjong-security /var/log/mahjong-security/

# configure postgresql
su postgres -c /etc/mahjong-security/db-init.sh

# reload systemd daemon to read configuration and start application
systemctl daemon-reload
systemctl enable mahjong-security
systemctl start mahjong-security