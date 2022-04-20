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

package xyz.rebasing.rebot.telegram.api.filter;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import xyz.rebasing.rebot.api.domain.MessageUpdate;

import static xyz.rebasing.rebot.telegram.api.utils.StringUtils.concat;

/**
 * Predicate utility class
 */
public final class ReBotPredicate {

    private ReBotPredicate(){}

    // same from karma plugin
    private static Pattern KARMA_PATTERN = Pattern.compile("(^\\S+)(\\+\\+|\\-\\-|\\—|\\–)($)");

    public static Predicate<MessageUpdate> messageIsNotNull() {
        return m -> null != m.getMessage().getText();
    }

    public static Predicate<MessageUpdate> isCommand() {
        return m -> m.getMessage().getText().startsWith("/") && !KARMA_PATTERN.matcher(m.getMessage().getText()).find();
    }

    public static Predicate<MessageUpdate> botCommand(String botUserId) {
        return m -> m.getMessage().getText().contains("@" + botUserId) || !m.getMessage().getText().contains("@");
    }

    public static Predicate<MessageUpdate> isBot() {
        return m -> m.getMessage().getFrom().isIsBot();
    }

    public static Predicate<MessageUpdate> help() {
        return m -> "help".equals(concat(m.getMessage().getText().split(" ")));
    }
}
