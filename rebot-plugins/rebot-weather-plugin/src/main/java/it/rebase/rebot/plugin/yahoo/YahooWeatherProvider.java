/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
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

package it.rebase.rebot.plugin.yahoo;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.plugin.yahoo.pojo.Condition;
import it.rebase.rebot.plugin.yahoo.pojo.YahooQueryResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class YahooWeatherProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String YAHOO_WEATHER_ENDPOINT = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

    @Inject
    @BotProperty(name = "it.rebase.rebot.plugin.yahoo.app.id", required = true)
    private String yahooAppID;

    @Inject
    @BotProperty(name = "it.rebase.rebot.plugin.yahoo.app.consumerKey", required = true)
    private String consumerKey;

    @Inject
    @BotProperty(name = "it.rebase.rebot.plugin.yahoo.app.consumerSecret", required = true)
    private String consumerSecret;

    /**
     * Search the weather condition for the given city
     *
     * @param location Ex: Uberlandia, MG
     * @return the forecast for the given city
     */
    public String execute(String location, String locale) {

        String normalizedLocation = normalize(location);
        String endpointQuery = YAHOO_WEATHER_ENDPOINT + "?location=" + normalizedLocation + "&format=json";

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(endpointQuery);

        Response response = target.request()
                .header("Yahoo-App-Id", yahooAppID)
                .header("Content-Type", "application/json")
                .header("Authorization", prepareAuthorization(normalizedLocation))
                .get();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to connect with Yahoo's API on " + endpointQuery + ", status code is: " + response.getStatus());
        }

        YahooQueryResponse yahooQueryResponse = response.readEntity(YahooQueryResponse.class);
        log.fine(yahooQueryResponse.toString());
        if (null == yahooQueryResponse.getLocation().getWoeid()) {
            return String.format(
                    I18nHelper.resource("Weather", locale, "forecast.not.found"),
                    location);

        } else {
            Condition condition = yahooQueryResponse.getCurrent_observation().getCondition();
            String city = yahooQueryResponse.getLocation().getCity();
            String region = yahooQueryResponse.getLocation().getRegion();
            String country = yahooQueryResponse.getLocation().getCountry();
            String cityRegion = String.format("%s, %s - %s", city, region, country);
            return String.format(
                    I18nHelper.resource("Weather", locale, "forecast"),
                    cityRegion,
                    toCelsius(condition.getTemperature()),
                    condition.getTemperature(),
                    condition.getText(),
                    country.replace(" ", "-"),
                    city.replace(" ", "-"),
                    yahooQueryResponse.getLocation().getWoeid());
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

    /**
     * Normalize the parameter and remove accents
     *
     * @param param
     * @return encoded param
     */
    private String normalize(String param) {
        String normalized = Normalizer.normalize(param, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        normalized = normalized.replace("\"", "").replace(" ", "+");
        try {
            normalized = URLEncoder.encode(normalized, StandardCharsets.UTF_8.displayName());
            log.fine(String.format("Parameter to encode: [%s], Encoded: [%s]", param, normalized));
        } catch (final Exception e) {
            log.warning("Failed to encode " + param);
        }
        return normalized;
    }

    /**
     * Prepare the Oauth Authorization Header
     *
     * @param location
     * @return Authorization Oauth 1.0
     */
    private String prepareAuthorization(String location) {

        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        String oauthNonce = new String(nonce).replaceAll("\\W", "");
        long timestamp = new Date().getTime() / 1000;

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        parameters.add("location=" + location);
        parameters.add("format=json");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }

        String signatureString = null;
        try {
            signatureString = "GET&" +
                    URLEncoder.encode(YAHOO_WEATHER_ENDPOINT, "UTF-8") + "&" +
                    URLEncoder.encode(parametersList.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            Base64.Encoder encoder = Base64.getEncoder();
            signature = encoder.encodeToString(rawHMAC);
        } catch (Exception e) {
            log.severe("Unable to append signature");
        }

        return "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";
    }
}