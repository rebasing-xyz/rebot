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

package xyz.rebasing.rebot.plugin.weather.test;

import org.junit.Assert;
import org.junit.Test;
import xyz.rebasing.rebot.plugin.weather.providers.openweather.builder.OpenWeatherRequestBuilder;
import xyz.rebasing.rebot.plugin.weather.providers.openweather.request.OpenWeatherRequest;

public class OpenWeatherRequestBuilderTest {

    private static final String OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&appid=%s";
    private final String API_KEY = "my-key";

    @Test(expected = IllegalArgumentException.class)
    public void noAPiKey() {
        OpenWeatherRequest requestUrl = new OpenWeatherRequestBuilder()
                .withQuery("Uberlandia")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noQuery() {
        OpenWeatherRequest requestUrl = new OpenWeatherRequestBuilder().build();
    }

    @Test
    public void testUrlWithCity() {
        OpenWeatherRequest requestUrl = new OpenWeatherRequestBuilder()
                .withQuery("Uberlandia")
                .withAppId(API_KEY)
                .build();

        String expected = String.format(OPENWEATHER_BASE_URL, "Uberlandia", "imperial", API_KEY);
        Assert.assertEquals(expected, requestUrl.toString());
    }

    @Test
    public void testUrlWithCityAndCountryCode() {
        OpenWeatherRequest requestUrl = new OpenWeatherRequestBuilder()
                .withQuery("Uberlandia")
                .withCountryCode("BR")
                .withAppId(API_KEY)
                .build();

        String expected = String.format(OPENWEATHER_BASE_URL, "Uberlandia,BR", "imperial", API_KEY);
        Assert.assertEquals(expected, requestUrl.toString());
    }

    @Test
    public void testUrlWithCityAndAndLang() {
        OpenWeatherRequest requestUrl = new OpenWeatherRequestBuilder()
                .withQuery("Uberlandia")
                .withLang("pt_br")
                .withAppId(API_KEY)
                .build();
        String expected = "https://api.openweathermap.org/data/2.5/weather?q=Uberlandia&units=imperial&lang=pt_br&appid=my-key";
        Assert.assertEquals(expected, requestUrl.toString());
    }
}