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

package xyz.rebasing.rebot.plugin.chuck.utils;

import java.lang.invoke.MethodHandles;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.service.persistence.pojo.Fact;

public class Utils {

    private static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String CHUCK_NORRIS_FACTS_ENDPOINT = "https://api.chucknorris.io/jokes/random";

    public static Fact getFact() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(CHUCK_NORRIS_FACTS_ENDPOINT);
        Response response = target.request().get();

        if (response.getStatus() != 200) {
            log.warnv("Failed to connect in the endpoint {0}, status code is: {1}",
                      CHUCK_NORRIS_FACTS_ENDPOINT,
                      response.getStatus());
        }

        return response.readEntity(Fact.class);
    }
}
