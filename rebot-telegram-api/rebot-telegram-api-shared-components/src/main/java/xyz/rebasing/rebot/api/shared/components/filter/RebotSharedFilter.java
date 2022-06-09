/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Rebasing.xyz ReBot
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package xyz.rebasing.rebot.api.shared.components.filter;

import java.util.function.Predicate;

import xyz.rebasing.rebot.api.domain.ChatAdministrator;
import xyz.rebasing.rebot.api.domain.MessageUpdate;

public abstract class RebotSharedFilter {

    public static Predicate<MessageUpdate> isPrivateChat() {
        return m -> m.getMessage().getChat().getType().equals("private");
    }

    public static Predicate<ChatAdministrator> isUserAdmin(long user) {
        return chatAdministrator -> chatAdministrator.getUser().getId() == user;
    }
}