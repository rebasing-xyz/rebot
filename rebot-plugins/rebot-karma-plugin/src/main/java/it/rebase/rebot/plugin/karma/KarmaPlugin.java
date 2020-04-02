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

package it.rebase.rebot.plugin.karma;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.plugin.karma.listener.KarmaEventListener;
import it.rebase.rebot.service.cache.qualifier.KarmaCache;
import it.rebase.rebot.service.persistence.repository.KarmaRepository;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static it.rebase.rebot.api.utils.Formatter.normalize;

@ApplicationScoped
public class KarmaPlugin implements PluginProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Pattern FULL_MSG_PATTERN = Pattern.compile("(\\w*)(\\+\\+|\\-\\-|\\—|\\–)(\\s|$)");
    private final Pattern KARMA_PATTERN = Pattern.compile("(^\\S+)(\\+\\+|\\-\\-|\\—|\\–)($)");

    @Inject
    @BotProperty(name = "it.rebase.rebot.plugin.karma.timeout", value = "30")
    String timeout;

    @Inject
    @KarmaCache
    Cache<String, Integer> cache;

    @Inject
    KarmaEventListener karmaEventListener;

    @Inject
    KarmaRepository karma;

    @Override
    public void load() {
        new Thread(() -> {
            cache.addListener(karmaEventListener);
            log.fine("Plugin karma-plugin enabled.");
        }).start();
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
                            key = item.substring(0, item.length() - 1).toLowerCase();
                        } else {
                            keyOperator = item.substring(item.length() - 2);
                            key = item.substring(0, item.length() - 2).toLowerCase();
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
            log.warning(e.getMessage());
        }
        return response.toString();
    }

    @Override
    public boolean removeMessage() {
        return false;
    }

    @Override
    public long deleteMessageTimeout() {
        return 0L;
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

        int karmaAtual = karma.get(target);
        switch (operator) {
            case "++":
                cache.putIfAbsent(target + ":" + username, ++karmaAtual, getTimeout(), TimeUnit.SECONDS);
                break;

            case "--":
                cache.putIfAbsent(target + ":" + username, --karmaAtual, getTimeout(), TimeUnit.SECONDS);
                break;

            default:
                //do nothing
                break;

        }
        return String.format(I18nHelper.resource("KarmaMessages", locale, "karma.updated"),
                normalize(target), cache.get(target + ":" + username));
    }

    /**
     * Verifies if the received text can be processed by this plugin
     *
     * @param messageContent
     * @return true if the message matches the karma pattern, otherwise returns false
     */
    private boolean canProcess(String messageContent) {
        boolean canProcess = null == messageContent ? false : FULL_MSG_PATTERN.matcher(messageContent).find();
        log.fine("Karma plugin - can process [" + messageContent + "] - " + canProcess);
        return canProcess;
    }

    private int getTimeout() {
        return Integer.parseInt(timeout);
    }
}