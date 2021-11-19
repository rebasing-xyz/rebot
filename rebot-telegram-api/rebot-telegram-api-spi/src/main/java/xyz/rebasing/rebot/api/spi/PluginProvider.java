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

package xyz.rebasing.rebot.api.spi;

import xyz.rebasing.rebot.api.domain.MessageUpdate;

public interface PluginProvider {

    /**
     * Process messages received by the plugins
     * @param update message to be processed
     * @param locale locale provided by user
     * @return a string with the result
     */
    String process(MessageUpdate update, String locale);

    /**
     * Loads the plugin.
     * It is intended to be used by execute any task needed to start the plugin.
     */
    void load();

    /**
     * @return the plugin name
     */
    String name();

    /**
     * Flag to specify if the messages handled by the given plugin will be removed or not.
     * @return false or true, if true the messages handled by the given plugin will be removed.
     */
    boolean deleteMessage();

    /**
     * The given timeout will be used to define how long the target message will survive in the chat.
     * Unit in Seconds
     * @return the configured timeout, this value will be used to delete messages
     */
    long deleteMessageTimeout();
}
