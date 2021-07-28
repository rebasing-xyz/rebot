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

package xyz.rebasing.rebot.plugin.welcome.test;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.test.Mock;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.domain.User;
import xyz.rebasing.rebot.api.management.user.UserManagement;

@Mock
@ApplicationScoped
public class MockUserManagement implements UserManagement {

    @Override
    public void kickUser(long userId, long chatId) {

    }

    @Override
    public void kickUser(long userId, long chatId, long waitBeforeStart) {

    }

    @Override
    public void unbanUser(long userId, long chatId) {

    }

    @Override
    public void unbanUser(long userId, long chatId, long waitBeforeBan) {

    }

    @Override
    public User getMe() {
        User bot = new User();
        bot.setFirstName("test");
        bot.setUsername("test_bot");
        bot.setId(10101010);
        return new User();
    }

    @Override
    public boolean isAdministrator(MessageUpdate messageUpdate) {
        return false;
    }

    @Override
    public boolean isBotAdministrator(MessageUpdate messageUpdate) {
        return false;
    }
}
