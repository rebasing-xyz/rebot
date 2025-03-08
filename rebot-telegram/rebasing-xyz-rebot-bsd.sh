#!/bin/sh

# Add environment variables to the service's context
. /etc/rc.subr

name="rebot"
rcvar="${name}_enable"
pidfile="/home/rebot/rebasing-xyz-rebot.pid"
log_file="/home/rebot/rebasing-xyz-rebot.log"

# Load configuration variables
load_rc_config "$name"

# Configuration file (update path if needed)
REBOT_CONFIG="/home/rebot/rebasing-xyz-rebot.conf"

. ${REBOT_CONFIG}

# Java command (adjust path if needed)
JAVA_CMD="/usr/local/bin/java"
rebot_user="rebot"
#command="/usr/sbin/daemon"
cmd="${JAVA_CMD} -jar \
    -Dxyz.rebasing.rebot.telegram.token=${REBOT_TELEGRAM_TOKEN_ID} \
    -Dxyz.rebasing.rebot.telegram.userId=${REBOT_TELEGRAM_USER_ID} \
    -Dxyz.rebasing.rebot.delete.messages=${REBOT_TELEGRAM_DELETE_MESSAGES} \
    -Dxyz.rebasing.rebot.delete.messages.after=${REBOT_TELEGRAM_DELETE_MESSAGES_AFTER} \
    -Dxyz.rebasing.rebot.plugin.openweather.appid=${REBOT_TELEGRAM_OPENWEATHER_APPID} \
    -Dquarkus.log.category.\"xyz.rebasing\".level=${REBOT_TELEGRAM_LOG_LEVEL} \
    /home/rebot/quarkus-app/quarkus-run.jar > ${log_file}"
# Load environment variables from config file
start_precmd="rebot_prestart"
rebot_prestart() {
    # Check if user exists
    if ! id -u ${rebot_user} >/dev/null 2>&1; then
        echo "ERROR: User '${rebot_user}' does not exist."
        exit 1
    fi

    if [ ! -f "$REBOT_CONFIG" ]; then
        echo "ERROR: Configuration file $REBOT_CONFIG not found."
        exit 1
    fi

    # Ensure PID directory is writable
    if [ ! -w $(dirname ${pidfile}) ]; then
        echo "ERROR: PID directory $(dirname ${pidfile}) is not writable by ${rebot_user}."
        exit 1
    fi

}

start_cmd="${name}_start"
rebot_start() {
    if [ -f "$pidfile" ]; then
        echo "PID file exists. Service may already be running."
        exit 1
    fi

    echo "Starting rebot."

    echo "Start Command: ${cmd}"

    exec su - ${rebot_user} ${cmd} &
    pgrep -f /home/rebot/quarkus-app/quarkus-run.jar > ${pidfile}
}

# Stop command
stop_cmd="rebot_stop"
rebot_stop() {
    if [ -f "$pidfile" ]; then
        kill -15 $(cat "$pidfile") && rm -f "$pidfile"
    else
        echo "PID file not found. Service may not be running."
    fi
}

# Run the script
rc_debug="YES"
run_rc_command "$@"


