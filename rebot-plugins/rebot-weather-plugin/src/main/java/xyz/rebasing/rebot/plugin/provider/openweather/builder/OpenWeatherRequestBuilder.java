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

package xyz.rebasing.rebot.plugin.provider.openweather.builder;

import xyz.rebasing.rebot.plugin.provider.openweather.request.OpenWeatherRequest;

// TODO showCoordinates uses a map api to show geo location.
// TODO add support on the builder to query with lat and lon
// TODO add support on the builder to query by city ID http://bulk.openweathermap.org/sample/
public class OpenWeatherRequestBuilder {

    private String query;
    private String countryCode;
    private String units;
    private String lang;
    private String appId;

    public OpenWeatherRequestBuilder withQuery(String query) {
        this.query = query;
        return this;
    }

    public OpenWeatherRequestBuilder withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public OpenWeatherRequestBuilder withUnit(String units) {
        this.units = units;
        return this;
    }

    public OpenWeatherRequestBuilder withLang(String lang) {
        this.lang = lang;
        return this;
    }

    public OpenWeatherRequestBuilder withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public OpenWeatherRequest build() {
        return new OpenWeatherRequest(this.query,
                                      this.countryCode,
                                      this.units,
                                      this.lang,
                                      this.appId);
    }
}
