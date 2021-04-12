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

package xyz.rebasing.rebot.plugin.postal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.text.Normalizer;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.Cache;
import xyz.rebasing.rebot.api.emojis.Emoji;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.service.cache.pojo.postal.PostalCode;
import xyz.rebasing.rebot.service.cache.qualifier.BrazilPostalCodeCache;

@ApplicationScoped
public class BrazilPostalCodeUtils {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private InputStream CSV_FILE = this.getClass().getClassLoader().getResourceAsStream("META-INF/brazil-postal-code-list.csv");
    private String CSV_SEPARATOR = ";";

    @Inject
    @BrazilPostalCodeCache
    Cache<String, PostalCode> cache;

    public void processCSVFile() {
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] linePos = line.split(CSV_SEPARATOR);
                log.finest("Processing " + linePos[0] + " " + linePos[1] + " " + linePos[2] + " " + linePos[3]);
                // remove accents before put on cache
                String county = removeAccent(linePos[2]);
                cache.put(county, new PostalCode(linePos[0], linePos[1], county, linePos[3]));
            }
        } catch (IOException e) {
            log.severe("Failed to parse CSV File: " + e.getMessage());
        }
    }

    public String query(String key, long limitResult, boolean returnUF, String locale) {
        StringBuilder stbuilder = new StringBuilder();

        log.fine(String.format("Search key ---- %s with parameters --limit-result=%d and --uf=%s", removeAccent(key).toUpperCase(), limitResult, returnUF));
        List<PostalCode> foundValues = cache.values().stream()
                .filter(item -> item instanceof PostalCode)
                .filter(item -> item.getCounty().toUpperCase().contains(removeAccent(key).toUpperCase())
                        || item.getNationalCode().equals(key.trim()))
                .limit(limitResult)
                .collect(Collectors.toList());

        if (foundValues.size() <= 0) {
            stbuilder.append(String.format(I18nHelper.resource("PostalCodeMessages", locale, "county.not.found"),
                                           key,
                                           Emoji.TRIANGULAR_FLAG_ON_POST));
        } else {
            for (PostalCode postalCode : foundValues) {
                if (returnUF) {
                    stbuilder.append(String.format(I18nHelper.resource("PostalCodeMessages", locale, "uf.response"),
                                                   postalCode.getNationalCode(),
                                                   postalCode.getUF()));
                } else {
                    stbuilder.append(String.format(I18nHelper.resource("PostalCodeMessages", locale, "postal.code.response"),
                                                   postalCode.getCounty(),
                                                   postalCode.getUF(),
                                                   postalCode.getNationalCode()));
                }
            }
        }
        return stbuilder.toString();
    }

    private String removeAccent(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
