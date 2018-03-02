# ReBot SPI - Service Provider Interfaces

Provides the needed interfaces and features to interact with the ReBot Telegram API.

It provides:

 - Interface AdministrativeCommandProvider: Add administrative commands, these commands will remain active even if the bot is disabled.
 - Interface CommandProvider: Used to implement commands
 - Interface PluginProvider: Used to implement plugins
 
Usage:

### Creating a Commands/Plugins:

Each new command, to be recognized by the API, needs to implement the CommandProvider and include the following file under **resources/META-INF/services**:

```
it.rebase.rebot.api.spi.CommandProvider
```
Its content should be the fully qualified name of the class that implement the CommandProvider interface:

For example:

```
it.rebase.rebot.service.ping.Ping
```

To create Plugin or Administrative command just rename the file to the fully qualified name of the target interface, for example:

For the Plugin creation, you should add uder **resources/META-INF/services** the following file:

```
it.rebase.rebot.api.spi.PluginProvider
```

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

}
```

And the project structure:

```
rebot-services/rebot-ping-service/
├── pom.xml
├── README.md
├── rebot-ping-service.iml
└── src
    └── main
        ├── java
        │   └── it
        │       └── rebase
        │           └── rebot
        │               └── service
        │                   └── ping
        │                       └── Ping.java
        └── resources
            └── META-INF
                └── services
                    └── it.rebase.rebot.api.spi.CommandProvider

11 directories, 5 files

```

### Other Features

This service also provides the ability to configure System Properties in any place of your Bot,
just by injecting the `BotProperty` and configuring it to **required** or **not**. Example:

```java
@Inject
@BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
private String botTokenId;
```

If the property is set to required and it is not set, the bot will fail to start.

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email: just@rebase.it