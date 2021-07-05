#!/bin/sh

REBOT_TELEGRAM_LOG_LEVEL=${REBOT_TELEGRAM_LOG_LEVEL:-INFO}

if [ -n "${REBOT_TELEGRAM_TOKEN_ID}" -a -n "${REBOT_TELEGRAM_USER_ID}" ]; then
    echo "Bot correctly configured."
else
    echo "Missing configurations, needed vars: REBOT_TELEGRAM_TOKEN_ID and REBOT_TELEGRAM_USER_ID"
    exit 1
fi

echo "Running ReBot image with the following configurations:"
echo "REBOT_TELEGRAM_TOKEN_ID: $REBOT_TELEGRAM_TOKEN_ID"
echo "REBOT_TELEGRAM_USER_ID: $REBOT_TELEGRAM_USER_ID"
echo "REBOT_TELEGRAM_DELETE_MESSAGES: $REBOT_TELEGRAM_DELETE_MESSAGES"
echo "REBOT_TELEGRAM_DELETE_MESSAGES_AFTER: $REBOT_TELEGRAM_DELETE_MESSAGES_AFTER"
echo "REBOT_TELEGRAM_OPENWEATHER_APPID: $REBOT_TELEGRAM_OPENWEATHER_APPID"
echo "REBOT_TELEGRAM_LOG_LEVEL: $REBOT_TELEGRAM_LOG_LEVEL"


cd $REBOT_HOME && exec java -jar \
    -Dxyz.rebasing.rebot.telegram.token=${REBOT_TELEGRAM_TOKEN_ID} \
    -Dxyz.rebasing.rebot.telegram.userId=${REBOT_TELEGRAM_USER_ID} \
    -Dxyz.rebasing.rebot.delete.messages=${REBOT_TELEGRAM_DELETE_MESSAGES} \
    -Dxyz.rebasing.rebot.delete.messages.after=${REBOT_TELEGRAM_DELETE_MESSAGES_AFTER} \
    -Dxyz.rebasing.rebot.plugin.openweather.appid=${REBOT_TELEGRAM_OPENWEATHER_APPID} \
    -Dquarkus.log.category."xyz.rebasing".level=${REBOT_TELEGRAM_LOG_LEVEL} \
    ${REBOT_QUARKUS_BINARY}