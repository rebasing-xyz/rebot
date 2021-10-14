# ReBot Telegram bot

The ReBot Bot implementation.

### Bot Functions:

    - karma - Karma operations username++|--
    - Chuck Norris - When Chuck norris is typed, a fun fact about him will be returnad
    - karma - search the karma points for the given key
    - urban - search for a English term on urban dictionary
    - ddd - Return the national code (ddd) for the given county
    - currency - currency rates + exchange rate
    - ping - pong
    - uptime - returns the time the bot is running
    - weather - returns the forecast for the given city
    - packt - returns the daily free ebook
    - faq - list the information about the given project
    - disable - disable the bot
    - enable - enable the bot
    - dump - dump the available commands in the Telegram's commands pattern
    - id - returns the user and chat ID
    - help - shows the bot help
    
### Starting the Bot

Execute the following command:

```sh
java -Dxyz.rebasing.rebot.telegram.token=<telegram_bot_token> 
     -Dxyz.rebasing.rebot.telegram.userId=<telegram_user_id> \
     -Dxyz.rebasing.rebot.plugin.openweather.appid=<openweather_app_id> \
     -jar quarkus-app/quarkus-run.jar
 ```
 
For mor information OpenWeather plugin, please refer to this [link](../rebot-plugins/rebot-weather-plugin/README.md).
Those parameters are required for the weather plugin work.
    
PS: The bot token is private, you can retrieve yours through the Telegram's BotFather


### Building a Container Image
The ReBot relies on the Quarkus Runtime, it already provides a few ways to build a container image
ready to run on your Kubernetes cluster. It can be done by running this command:

```console
mvn clean package -Dquarkus.container-image.build=true
```

After the image is built, it can be started with this command:

```console
$ podman | docker run -it \ 
    --env XYZ_REBASING_REBOT_TELEGRAM_USERID=<telegram_user_id> \ 
    --env XYZ_REBASING_REBOT_TELEGRAM_TOKEN=<telegram_bot_token> \
    --env XYZ_REBASING_REBOT_PLUGIN_OPENWEATHER_APPID=<openweather_app_id> \
    $USERNAME/rebot-telegram-bot:1.0-SNAPSHOT
```

You can also provide all the required system properties in the `application.properties` file:

```properties
xyz.rebasing.rebot.telegram.userId=<telegram_user_id>
xyz.rebasing.rebot.telegram.token=<bot_token>
xyz.rebasing.rebot.plugin.openweather.appid=<openweather_app_id> # only required if the openweather is enabled (present on the pom.xml)
```

For mode details, please refer to Quarkus Container Image [documentation](https://quarkus.io/guides/container-image)

### Would you like to try it?
The bot is up and running, feel free to add it in a group or to test it on a private chat.
The bot username is: **@rebaseit_bot**


### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).

