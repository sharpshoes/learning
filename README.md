#!/bin/bash
### BEGIN INIT INFO
# Provides: antsentry
# Required-Start: NONE
# Required-Stop: NONE
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
# Short-Description: Start antsentry
# Descrption: This service is used to start antsentry
### END INIT INFO

case "$1" in
    start)
        echo "Start"
        /ant/apps/antsentry-client/start.sh&
        ;;
    stop)
        echo "Stop"
        /ant/apps/antsentry-client/stop.sh&
        exit 1
        ;;
    *)
        echo "antsentry start|stop"
        exit 1
        ;;
esac
exit 0
