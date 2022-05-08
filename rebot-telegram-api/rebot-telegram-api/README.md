# ReBot Telegram Bot API

It is a Java EE Telegram API, pluggable and easy to use.

 
## Creating a Bot:

There is a Bot example with all available plugins and services: [Example](../../rebot-telegram), check it out!


## Customizing the Http Client

ReBot uses the OkHttp 4 to make requests against the Telegram's API and the client used is configurable by adding an
alternative implementation of the `IRebotOkHttpClient` interface, see the example below about how to customize it:

```java
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import io.quarkus.arc.Priority;
import okhttp3.OkHttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import xyz.rebasing.rebot.api.shared.components.httpclient.IRebotOkHttpClient;

@Alternative
@Priority(200)
@ApplicationScoped
public class MyCustomClient implements IRebotOkHttpClient {
    
    @Override
    public OkHttpClient get() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .hostnameVerifier(new NoopHostnameVerifier())
                .build();
    }
}
```

The Priority of the Default Bean is set to 100, any implementation of the `IRebotOkHttpClient` bean with priority higher
than 100 will take precedence.


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
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).