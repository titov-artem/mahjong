#!/bin/bash

LIBS_PATH=/usr/lib/mahjong-security
PID_FILE=/etc/mahjong-security/mahjong-security.pid
LOG_FILE=/var/log/mahjong-security/mahjong-security.log
STD_LOG_FILE=/var/log/mahjong-security/mahjong-security-stdoutput.log

CLASSPATH=''

for LIB in `ls ${LIBS_PATH}`; do
  CLASSPATH="${CLASSPATH}:${LIBS_PATH}/${LIB}"
done

java -cp ${CLASSPATH} \
    -Xmx128m \
    -Dlog.file=${LOG_FILE} \
    com.github.mahjong.security.app.SecurityMain > ${STD_LOG_FILE} &