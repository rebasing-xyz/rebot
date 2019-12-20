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

package it.rebase.rebot.api.user.management;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.User;

public interface UserManagement {

    /**
     * kick the given user from the given chatID
     * bot needs to be a group admin.
     * @param userId
     * @param chatId
     */
    void kickUser(long userId, long chatId);

    /**
     * kick the given user from the given chatID within the custom delay
     * bot needs to be a group admin.
     * @param userId
     * @param chatId
     * @param waitBeforeStart
     */
    void kickUser(long userId, long chatId, long waitBeforeStart);

    /**
     * unban the given user from the given chatID after kick him
     * bot needs to be a group admin.
     * @param userId
     * @param chatId
     */
    void unbanUser(long userId, long chatId);

    /**
     * unban after the given delay the given user from the given chatID after kick the user.
     * bot needs to be a group admin.
     * @param userId
     * @param chatId
     * @param waitBeforeBan
     */
    void unbanUser(long userId, long chatId, long waitBeforeBan);

    /**
     * returns information about the Bot's user.
     * @return
     */
    User getMe();

    /**
     * return if the user who is interacting with the bot is admin or not.
     * @param messageUpdate
     * @return
     */
    boolean isAdministrator(MessageUpdate messageUpdate);

    /**
     * Verifies if the Bot user is a group admin or not.
     * @param messageUpdate
     * @return
     */
    boolean isBotAdministrator(MessageUpdate messageUpdate);
}
