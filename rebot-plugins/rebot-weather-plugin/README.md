# ReBot Weather Plugin

Retrieve the weather condition, in °C and °F, based on the OpenWeather API.

Usage:

```
/weather Uberlandia -c BR

Condition for: Uberlândia - BR 
        Condition:  clear sky 
  Temperature:  20.6°C / 69.01°F 
          Min:  20.6°C / 69.01°F 
          Max:  20.6°C / 69.01°F 
   Feels like:  19.6°C / 67.21°F 
     Humidity:  34.0
```

For help:

```
/weather help
```

To use this plugin you will need to provide the following system property:

```
-Dxyz.rebasing.rebot.plugin.openweather.appid=<YOUR_APP_ID>
```

To create your app, please refer https://openweathermap.org/appid. 


### Did you find a bug or do you have a suggestion?
Feel free to raise a [issue](https://github.com/rebasing-xyz/rebot/issues/new).