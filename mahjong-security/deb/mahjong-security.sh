#!/bin/sh

RUNAS=mahjong-security

PID_FILE=/var/run/mahjong-security/mahjong-security.pid
LOG_FILE=/var/log/mahjong-security/mahjong-security.log
STD_LOG_FILE=/var/log/mahjong-security/mahjong-security-stdoutput.log

start() {
  if [ -f ${PID_FILE} ] && kill -0 $(cat ${PID_FILE}); then
    echo 'Service already running' >&2
    return 1
  fi
  echo 'Starting service…' >&2
  local LIBS_PATH=/usr/lib/mahjong-security
  local CLASSPATH=''

  for LIB in `ls ${LIBS_PATH}`; do
    CLASSPATH="${CLASSPATH}:${LIBS_PATH}/${LIB}"
  done

  # TODO run from proper user
  java -cp ${CLASSPATH} \
    -Xmx128m \
    -Dlog.file=${LOG_FILE} \
    com.github.mahjong.security.app.SecurityMain > ${STD_LOG_FILE} 2>&1 &
  echo $! > ${PID_FILE}
  echo 'Service started' >&2
}

stop() {
  if [ ! -f "${PID_FILE}" ] || ! kill -0 $(cat "${PID_FILE}"); then
    echo 'Service not running' >&2
    return 1
  fi
  echo 'Stopping service…' >&2
  kill -15 $(cat "${PID_FILE}") && rm -f "${PID_FILE}"
  echo 'Service stopped' >&2
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  retart)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart}"
esac
