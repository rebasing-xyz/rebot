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

package xyz.rebasing.rebot.plugin.weather.providers.openweather.request;

import java.io.StringWriter;

public class OpenWeatherRequest {

    private static final String OPENWEATHER_API_ADDRESS = "https://api.openweathermap.org/data/%s/weather";
    private final String OPENWEATHER_API_VERSION = "2.5";
    private final String QUERY_KEY = "?q";
    /**
     * Lang only updates the description and city name fields
     */
    private final String LANG_KEY = "&lang";
    private final String APPID_KEY = "&appid";
    /**
     * For temperature in Fahrenheit use units=imperial
     * For temperature in Celsius use units=metric
     * Temperature in Kelvin is used by default, no need to use units parameter in API call
     */
    private final String DEFAULT_UNIT = "imperial";
    private final String UNITS_KEY = "&units";

    private StringWriter uri = new StringWriter();

    public OpenWeatherRequest(String query, String countryCode, String units, String lang, String apiKey) {

        uri.append(String.format(OPENWEATHER_API_ADDRESS, OPENWEATHER_API_VERSION));

        if (isNullOrEmpty(query)) {
            throw new IllegalArgumentException("City Query cannot be empty");
        } else {
            uri.append(QUERY_KEY).append("=").append(query);
        }

        if (!isNullOrEmpty(countryCode)) {
            uri.append(",").append(countryCode);
        }

        // apply default unit to F
        uri.append(UNITS_KEY).append("=");
        if (!isNullOrEmpty(units)) {
            uri.append(units);
        } else {
            uri.append(DEFAULT_UNIT);
        }

        // TODO add all supported langs inside an Enum
        if (!isNullOrEmpty(lang)) {
            uri.append(LANG_KEY).append("=").append(lang);
        }

        if (isNullOrEmpty(apiKey)) {
            throw new IllegalArgumentException("API Key cannot be empty");
        } else {
            uri.append(APPID_KEY).append("=").append(apiKey);
        }
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    private boolean isNullOrEmpty(String str) {
        return null == str || str.isEmpty();
    }
}
