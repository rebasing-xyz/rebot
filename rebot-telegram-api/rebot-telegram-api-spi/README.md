# ReBot SPI - Service Provider Interfaces

Provides the needed interfaces and features to interact with the ReBot Telegram API.

It provides:

 - Interface AdministrativeCommandProvider: Add administrative commands, these commands will remain active even if the bot is disabled.
 - Interface CommandProvider: Used to implement commands
 - Interface PluginProvider: Used to implement plugins
 
Usage:

### Creating Commands/Plugins:

Each new command, to be recognized by the API, needs to implement the CommandProvider and include the following file under **resources/META-INF/services**:

```
CommandProvider
```
Its content should be the fully qualified name of the class that implement the CommandProvider interface:

For example:

```
xyz.rebasing.rebot.service.ping.Ping
```

To create Plugin or Administrative command just rename the file to the fully qualified name of the target interface, for example:

Example:

This example will use the ReBot Ping Service:

Ping Service Class:

```java
@ApplicationScoped
public class Ping implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Override
    public void load() {
        log.fine("Loading command  " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        return "pong";
    }

    @Override
    public String name() {
        return "/ping";
    }

    @Override
    public String help() {
        return "/ping - pong";
    }

    @Override
    public String description() {
        return "pong";
    }
    @Override
    public boolean deleteMessage() {
        return true;
    }

    @Override
    public long deleteMessageTimeout() {
        return 10;
    }

}
```

And the project structure:

```
rebot-services/rebot-ping-service/
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── xyz
        │       └── rebasing
        │           └── rebot
        │               └── service
        │                   └── ping
        │                       └── Ping.java
        └── resources
            └── beans.xml

11 directories, 4 files
```

### Other Features

This service also provides the ability to configure System Properties in any place of your Bot,
just by injecting the `BotProperty` and configuring it to **required** or **not**. Example:

```java
@Inject
@BotProperty(name = "xyz.rebasing.rebot.my.property", required = true)
private String myProperty;
```

For global configurations use the `BotConfig` class.

If the property is set to required and is not set, the bot will fail to start.


## Internationalization Plugin

The api supports internationalization, for now there are two languages support: en_Us and pt_BR.
The locale is set at chat level which means the configuration will be applied to all chat members.

Only chat administrators can change this configuration. To chage the locale definition just do:

```bash
/locale pt_br

# or

/locale en_us
```

### Add support to internationalization to your plugin

For create a ReBot plugin you must implement the CommandProvider or the PluginProvider interfaces which is already
prepared for the locale setup, i.e. the methods receives the locale which is set by the Core API, that way the locale is
set/read by chatID on only one place.

All you need to do is define the resource bundle under *resources* directory of your project, then use it on your plugin like
the example below:

```java
    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        String response;
        try {
            response = yahoo.execute(key.get(), locale);
        } catch (final Exception e) {
            response = String.format(
                    I18nHelper.resource("Weather", locale, "error.state"),
                    this.name(),
                    e.getMessage());
            e.printStackTrace();
        }
        return key.get().length() > 0 ? response : String.format(
                I18nHelper.resource("Weather", locale, "parameter.required"),
                this.name());
    }
```

For more information take a look on the [i18n helper](https://github.com/rebasing-xyz/rebot/tree/master/rebot-telegram-api/rebot-telegram-api-spi/src/main/java/xyz/rebasing/rebot/api/i18n).

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).