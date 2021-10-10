# ReBot Telegram API

Here you can find all modules that composes the API:

 - [ReBot API](rebot-telegram-api/README.md)
 - [ReBot API Shared Components](rebot-telegram-api-shared-components/README.md)
 - [ReBot SPI](rebot-telegram-api-spi/README.md)
 - [ReBot Objects](rebot-telegram-api-domain/README.md)
 - [ReBot Emojis](rebot-telegram-api-emojis/README.md)

For more details about each component check its respective README file.

## Supported configurations

Here you can find all system properties that can be used to configure the ReBot API

- **xyz.rebasing.rebot.telegram.token**: Telegram bot token, mandatory parameter.
- **xyz.rebasing.rebot.telegram.userId**: Telegram bot username, mandatory parameter.
- **xyz.rebasing.rebot.delete.messages**: Instruct the bot to delete messages sent by itself, defaults to false.
- **xyz.rebasing.rebot.delete.messages.after**: If message deletion is enabled, it indicates how log the message will survive in the chat before be deleted, value set in seconds, defaults to 120 seconds.

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).