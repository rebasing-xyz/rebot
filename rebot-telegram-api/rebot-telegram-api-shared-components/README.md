# ReBot API Shared Components

Provides the capability to manage users on a Telegram Chat Group

Capabilities:

 - Kick User: at this moment only the bot can kick a user. Bot must be Group Admin.
 - Unban User: When the user is kicked by a admin, the user will not be able to join using invite link.
               Thus the user needs to be unbaned or invited by an admin. Bot must be Group Admin.
 - Get Me: A simple method which will return the Bot User information.
 - Message Sender: all outgoing messages processed by the api will use this sender.
 - http client: small httpclient shared accross api and plugins.

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).