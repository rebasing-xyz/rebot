# ReBot Telegram Bot API

It is a Java EE Telegram API, pluggable and easy to use.

 
## Creating a Bot:

There is a Bot example with all available plugins and services: [Example](../../rebot-telegram), check it out!


## Sending messages

The API exposes a rest endpoint that allows you to send a message to a Chat:

```java
GET /message/send/{chatId}/{message}
```

To send messages just use the endpoint, like this example, send the message "just a test":

```bash
curl http://rebot.apps.spolti.cloud/message/send/-143169202/just%20a%20test 
```

Do get the Chat id, the API also exposes a command that you can retrieve this information:

```bash
/id
```

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email: just@rebase.it