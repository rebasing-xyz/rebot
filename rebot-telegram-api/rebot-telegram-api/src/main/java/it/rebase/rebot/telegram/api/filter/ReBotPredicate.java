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

package it.rebase.rebot.telegram.api.filter;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import it.rebase.rebot.api.object.ChatAdministrator;
import it.rebase.rebot.api.object.MessageUpdate;

public class ReBotPredicate {

    // same from karma plugin
    private static Pattern KARMA_PATTERN = Pattern.compile("(^\\S+)(\\+\\+|\\-\\-|\\—|\\–)($)");

    public static Predicate<MessageUpdate> messageIsNotNull() {
        return m -> null != m.getMessage().getText();
    }

    public static Predicate<MessageUpdate> isCommand() {
        return m -> m.getMessage().getText().startsWith("/") && !KARMA_PATTERN.matcher(m.getMessage().getText()).find();
    }

    public static Predicate<MessageUpdate> botCommand(String botUserId) {
        return m -> (m.getMessage().getText().contains("@" + botUserId) || !m.getMessage().getText().contains("@"));
    }

    public static Predicate<MessageUpdate> isBot() {
        return m -> m.getMessage().getFrom().isIsBot();
    }

    public static Predicate<MessageUpdate> isPrivateChat() {
        return m -> m.getMessage().getChat().getType().equals("private");
    }

    public static Predicate<ChatAdministrator> isUserAdmin(String user) {
        return chatAdministrator -> chatAdministrator.getUser().getUsername().equals(user) ||
                chatAdministrator.getUser().getFirstName().equals(user);
    }
}
