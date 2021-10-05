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

package xyz.rebasing.rebot.plugin.karma;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.emojis.Emoji;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.PluginProvider;
import xyz.rebasing.rebot.service.persistence.domain.Karma;
import xyz.rebasing.rebot.service.persistence.repository.KarmaRepository;

import static xyz.rebasing.rebot.api.utils.Formatter.normalize;

@ApplicationScoped
public class KarmaPlugin implements PluginProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Pattern FULL_MSG_PATTERN = Pattern.compile("(\\w*)(\\+\\+|\\-\\-|\\—|\\–)(\\s|$)");
    private final Pattern KARMA_PATTERN = Pattern.compile("(^\\S+)(\\+\\+|\\-\\-|\\—|\\–)($)");

    @Inject
    BotConfig config;

    @ConfigProperty(name = "xyz.rebasing.rebot.plugin.karma.timeout", defaultValue = "30")
    Long timeout;

    @Inject
    KarmaRepository karma;

    Cache<String, Integer> karmaCache;

    @Override
    public void load() {
        new Thread(() -> {
            karmaCache = Caffeine.newBuilder()
                    .removalListener((String key, Integer value, RemovalCause cause) ->
                                             log.debugv("entry {0}={1} removed from the cache, cause: {2}",
                                                        key,
                                                        value,
                                                        cause))
                    .evictionListener((String key, Integer value, RemovalCause cause) ->
                                              log.debugv("entry {0}={1} evicted from the cache, cause: {2}",
                                                         key,
                                                         value,
                                                         cause))
                    .expireAfterWrite(timeout, TimeUnit.SECONDS)
                    .build();
            log.debug("Plugin karma-plugin enabled.");
        }).start();
    }

    @Override
    public String name() {
        return "karma";
    }

    @Override
    public String process(MessageUpdate update, String locale) {
        StringBuilder response = new StringBuilder();
        try {
            if (canProcess(update.getMessage().getText()) && !update.isEdited()) {
                List<String> itens = Arrays.asList(update.getMessage().getText().replaceAll("\\r|\\n", " ").split(" "));
                HashMap<String, String> finalTargets = new HashMap<>();
                String username = update.getMessage().getFrom().getUsername() != null ? update.getMessage().getFrom().getUsername() : update.getMessage().getFrom().getFirstName().toLowerCase();
                itens.stream().distinct().forEach(item -> {
                    if ((KARMA_PATTERN.matcher(item).find())) {
                        String keyOperator;
                        String key;
                        if (item.charAt(item.length() - 1) == 8212 || item.charAt(item.length() - 1) == 8211) {
                            keyOperator = "--";
                            key = item.substring(0, item.length() - 1);
                        } else {
                            keyOperator = item.substring(item.length() - 2);
                            key = item.substring(0, item.length() - 2);
                        }
                        finalTargets.putIfAbsent(key, keyOperator);
                    }
                });
                for (Map.Entry<String, String> entry : finalTargets.entrySet()) {
                    response.append(processKarma(entry.getValue(), entry.getKey(), username, locale));
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }

    /**
     * Process the karma, to trigger it is necessary to use ++ or -- at the end of any string.
     *
     * @param operator ++ or --
     * @param target   key that will have its karma changed
     * @param username user that requested the karma
     * @return the amount of karma + or - 1, or does nothing in case of excessive karma update for the same target
     */
    private String processKarma(String operator, String target, String username, String locale) {

        if (target.equals(username)) {
            return String.format(I18nHelper.resource("KarmaMessages", locale, "own.karma"),
                                 Emoji.DIZZY_FACE);
        }

        final int currentKarma = karma.get(target);
        switch (operator) {
            case "++":
                if (karmaCache.getIfPresent(target + ":" + username) == null) {
                    // update it when Quarkus cache supports event listeners
                    karmaCache.put(target + ":" + username, currentKarma + 1);
                    karma.updateOrCreateKarma(new Karma(target, String.valueOf(currentKarma + 1)));
                }
                break;

            case "--":
                if (karmaCache.getIfPresent(target + ":" + username) == null) {
                    // update it when Quarkus cache supports event listeners
                    karmaCache.put(target + ":" + username, currentKarma - 1);
                    karma.updateOrCreateKarma(new Karma(target, String.valueOf(currentKarma - 1)));
                }
                break;

            default:
                //do nothing
                break;
        }
        return String.format(I18nHelper.resource("KarmaMessages", locale, "karma.updated"),
                             normalize(target), karmaCache.getIfPresent(target + ":" + username));
    }

    /**
     * Verifies if the received text can be processed by this plugin
     *
     * @param messageContent
     * @return true if the message matches the karma pattern, otherwise returns false
     */
    private boolean canProcess(String messageContent) {
        boolean canProcess = null == messageContent ? false : FULL_MSG_PATTERN.matcher(messageContent).find();
        log.debugv("Karma plugin - can process [{0}] - {1}", messageContent, canProcess);
        return canProcess;
    }
}