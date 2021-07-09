/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebasing.xyz ReBot
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rebasing.rebot.plugin.provider.openweather;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.systemproperties.BotProperty;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.plugin.provider.openweather.builder.OpenWeatherRequestBuilder;
import xyz.rebasing.rebot.plugin.provider.openweather.pojo.OpenWeather;
import xyz.rebasing.rebot.plugin.provider.openweather.request.OpenWeatherRequest;

@ApplicationScoped
public class OpenWeatherProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @BotProperty(name = "xyz.rebasing.rebot.plugin.openweather.appid", required = true)
    private String appId;

    /**
     * Search the weather condition for the given city
     *
     * @param parameters Ex: Uberlandia  -cc BR -
     * @return the forecast for the given city
     */
    public String execute(String parameters, String locale) {
        String args[] = parameters.split(" ");
        String countryCode = "";
        String lang = locale;
        List<String> argsList = new ArrayList<>();
        List<String> optsList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                    i = i + 1;
                    if (args[i - 1].charAt(1) == 'l') {
                        lang = args[i];
                    } else if (args[i - 1].charAt(1) == 'c') {
                        countryCode = args[i];
                    }
                    optsList.add(args[i]);
                    break;

                default:
                    argsList.add(args[i]);
                    break;
            }
        }
        String query = String.join(" ", argsList);
        log.debugv("Parameters received [Query: {0}], [Country Code: {1}], [lang: {2}]", query, countryCode, lang);

        OpenWeatherRequest openWeatherRequest = new OpenWeatherRequestBuilder()
                .withQuery(query)
                .withCountryCode(countryCode)
                .withLang(lang)
                .withAppId(appId)
                .build();

        log.debugv("Performing OpenWeather API call with the following url {0}", openWeatherRequest.toString());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .build();
        Request request = new Request.Builder()
                .url(openWeatherRequest.toString())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.code() == 404) {
                return String.format(
                        I18nHelper.resource("Weather", locale, "forecast.not.found"),
                        query, query, countryCode, locale);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            OpenWeather openWeather = objectMapper.readValue(response.body().string(), OpenWeather.class);
            String city = openWeather.getName();
            String country = openWeather.getSys().getCountry();
            String condition = openWeather.getWeather().get(0).getDescription();
            float temperature = openWeather.getMain().getTemp();
            float tempMin = openWeather.getMain().getTempMin();
            float tempMax = openWeather.getMain().getTempMax();
            float tempFeelsLike = openWeather.getMain().getFeelsLike();
            float humidity = openWeather.getMain().getHumidity();

            return String.format(
                    I18nHelper.resource("Weather", locale, "forecast"),
                    city,
                    country,
                    condition,
                    toCelsius(temperature), temperature,
                    toCelsius(tempMin), tempMin,
                    toCelsius(tempMax), tempMax,
                    toCelsius(tempFeelsLike), tempFeelsLike,
                    humidity);
        } catch (final Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Converts temperature from Fahrenheit to Celsius
     *
     * @param value temperature in Fahrenheit
     * @return temperature in Celsius
     */
    private String toCelsius(float value) {
        return String.format("%.1f", (value - 32) / 1.8000);
    }
}

