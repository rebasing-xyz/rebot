### ReBot - A Java API for Telegram

![ReBot - Java 17](https://github.com/rebasing-xyz/rebot/workflows/ReBot%20-%20Java%2017/badge.svg)
![ReBot - Java 21](https://github.com/rebasing-xyz/rebot/workflows/ReBot%20-%20Java%2021/badge.svg)
![ReBot - Native Java 17](https://github.com/rebasing-xyz/rebot/workflows/ReBot%20-%20Native%20Java%2011/badge.svg)
![Code Analysis](https://lift.sonatype.com/api/badge/github.com/rebasing-xyz/rebot)


This API is composed by 3 Key Sub Projects, which are:

 - [ReBot API](rebot-telegram-api/README.md)
 - [ReBot Services](rebot-services/README.md)
 - [ReBot Plugins](rebot-plugins/README.md)
 
Also, you can find a [Telegram Bot](rebot-telegram/README.md), ready to use, All you need to do is provide
your Bot Token and Username.

For more details about each module, please visit its README file.

## Using SNAPSHOT artifacts

The SNAPSHOT artifacts can be used by configuring the following maven repository:

```
https://s01.oss.sonatype.org/content/groups/public/
```

## Quarkus Hot Reload feature

You can rely on the hot reload feature, you just need to start it in the `dev` mode from the `rebot-telegram` directory:

```bash
mvn clean compile quarkus:dev \
  -Dxyz.rebasing.rebot.telegram.token=<BOT_TOKEN> \
  -Dxyz.rebasing.rebot.telegram.userId=<BOT_ID>
```


## Code Style

This project uses a similar Java code style than https://github.com/kiegroup/droolsjbpm-build-bootstrap/tree/main/ide-configuration


### Known issues

- Native compilation when using embedded H2 database:
  - _General error: "java.lang.UnsupportedOperationException: H2 database compiled into a native-image is only functional as a client: can't create an Embedded Database Session" [50000-197]_
  - https://github.com/quarkusio/quarkus/issues/27021

### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).
