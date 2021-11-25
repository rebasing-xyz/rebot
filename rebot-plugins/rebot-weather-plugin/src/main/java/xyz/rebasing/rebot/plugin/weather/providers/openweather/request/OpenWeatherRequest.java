/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.rebasing.rebot.plugin.weather.providers.openweather.request;

public class OpenWeatherRequest {

    private final String OPENWEATHER_API_ADDRESS = "https://api.openweathermap.org/data/%s/weather";
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

    private StringBuilder uri = new StringBuilder();

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
        uri.append(UNITS_KEY).append("=").append(DEFAULT_UNIT);

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
