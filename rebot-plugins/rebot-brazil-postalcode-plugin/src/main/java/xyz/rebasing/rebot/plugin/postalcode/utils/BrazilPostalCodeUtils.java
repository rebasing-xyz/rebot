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

package xyz.rebasing.rebot.plugin.postalcode.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.cache.CacheResult;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.emojis.Emoji;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.plugin.postalcode.domain.PostalCode;

@ApplicationScoped
public class BrazilPostalCodeUtils {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private InputStream CSV_FILE = this.getClass().getClassLoader().getResourceAsStream("META-INF/brazil-postal-code-list.csv");
    private String CSV_SEPARATOR = ";";

    @CacheResult(cacheName = "ddd-cache")
    public Map<String, PostalCode> processCSVFile() {
        String line;
        Map<String, PostalCode> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] linePos = line.split(CSV_SEPARATOR);
                log.tracev("Processing {0} {1} {2} {3}", linePos[0], linePos[1], linePos[2], linePos[3]);
                // remove accents before put on cache
                String county = removeAccent(linePos[2]);
                map.put(county, new PostalCode(linePos[0], linePos[1], county, linePos[3]));
            }
        } catch (IOException e) {
            log.errorv("Failed to parse CSV File: {0}", e.getMessage());
        }
        return map;
    }

    public String query(String key, long limitResult, boolean returnUF, String locale) {
        StringBuilder stbuilder = new StringBuilder();

        log.debugv("Search key ---- {0} with parameters --limit-result={1} and --uf={2}",
                   removeAccent(key).toUpperCase(new Locale(locale)),
                   limitResult,
                   returnUF);

        // processCSVFile() is cached by Quarkus Cache
        List<PostalCode> foundValues = processCSVFile().entrySet().stream()
                .filter(item -> item.getValue() instanceof PostalCode)
                .filter(item -> item.getValue().getCounty().toUpperCase().contains(removeAccent(key).toUpperCase())
                        || item.getValue().getNationalCode().equals(key.trim()))
                .limit(limitResult)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (foundValues.isEmpty()) {
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
