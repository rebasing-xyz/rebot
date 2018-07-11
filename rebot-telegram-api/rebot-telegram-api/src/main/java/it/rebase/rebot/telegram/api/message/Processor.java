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


package it.rebase.rebot.telegram.api.message;

import it.rebase.rebot.api.object.MessageUpdate;

public interface Processor {

    /**
     * Filter all the updates and process it.
     * If any plugin or service can process the update, it will be processed and a reply will be sent by the
     * plugin or service that processed the update.
     * @param messageUpdate Message to be processed
     */
    void process(MessageUpdate messageUpdate);

    /**
     * Process the commands, anything started with / will be considered a command
     * @param messageUpdate Message to be processed
     */
    void commandProcessor(MessageUpdate messageUpdate);

    /**
     * Process everything, usually it is filtered by the plugins, i.e karma plugin.
     * If the message processed matches a plugin condition, it will be processed.
     * @param messageUpdate Message to be processed
     */
    void nonCommandProcessor(MessageUpdate messageUpdate);

}
