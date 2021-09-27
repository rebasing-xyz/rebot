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
java -Dxyz.rebasing.rebot.telegram.token=<TELEGRAM_TOKEN> 
     -Dxyz.rebasing.rebot.telegram.userId=<BOT_USER_ID> \
     -Dxyz.rebasing.rebot.plugin.openweather.appid=<YOUR_APP_ID> \
     -jar rebot-telegram-bot-<VERSION>-runner.jar
 ```
 
For mor information about how to create an Yahoo app, please refer to this [link](https://developer.yahoo.com/weather/).
Those parameters are required for the weather plugin work.
 
    
PS: The bot token is private, you can retrieve yours through the Telegram's BotFather

### Would you like to try it?
The bot is up and running, feel free to add it in a group or to test it on a private chat.
The bot username is: **@rebaseit_bot**


### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).

