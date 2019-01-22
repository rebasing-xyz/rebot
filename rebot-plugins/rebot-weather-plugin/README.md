# ReBot Weather Plugin

Retrieve the weather condition, in °C and °F, based on the Yahoo weather API.

Usage:

```
/weather Uberlandia, Minas Gerais

Conditions for Uberlandia, MG, BR at 01:00 AM BRST:
21.1°C / 70°F - Cloudy
https://weather.yahoo.com/country/state/city-455917/
```

To use this plugin you will need to provide the following system properties:

```java
-Dit.rebase.rebot.plugin.yahoo.app.id=<YOUR_APP_ID>
-Dit.rebase.rebot.plugin.yahoo.app.consumerKey=<YOUR_APP_COMSUMER_KEY>
-Dit.rebase.rebot.plugin.yahoo.app.consumerSecret=<YOUR_APP_CONSUMER_SECRET>
```

To create your app, please refer https://developer.yahoo.com/weather/. 


### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebase-it/rebot/issues/new) or send a email: just@rebase.it