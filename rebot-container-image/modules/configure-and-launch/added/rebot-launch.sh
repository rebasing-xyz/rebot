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
echo "REBOT_TELEGRAM_WEATHER_APP_ID: $REBOT_TELEGRAM_WEATHER_APP_ID"
echo "REBOT_TELEGRAM_WEATHER_CONSUMER_KEY: $REBOT_TELEGRAM_WEATHER_CONSUMER_KEY"
echo "REBOT_TELEGRAM_WEATHER_CONSUMER_SECRET: $REBOT_TELEGRAM_WEATHER_CONSUMER_SECRET"
echo "REBOT_TELEGRAM_LOG_LEVEL: $REBOT_TELEGRAM_LOG_LEVEL"


cd $REBOT_HOME && exec java -jar \
    -Dit.rebase.rebot.telegram.token=${REBOT_TELEGRAM_TOKEN_ID} \
    -Dit.rebase.rebot.telegram.userId=${REBOT_TELEGRAM_USER_ID} \
    -Dit.rebase.rebot.plugin.yahoo.app.id=${REBOT_TELEGRAM_WEATHER_APP_ID} \
    -Dit.rebase.rebot.plugin.yahoo.app.consumerKey=${REBOT_TELEGRAM_WEATHER_CONSUMER_KEY} \
    -Dit.rebase.rebot.plugin.yahoo.app.consumerSecret=${REBOT_TELEGRAM_WEATHER_CONSUMER_SECRET} \
    -Dquarkus.log.category."it.rebase".level=${REBOT_TELEGRAM_LOG_LEVEL} \
    ${REBOT_QUARKUS_BINARY}