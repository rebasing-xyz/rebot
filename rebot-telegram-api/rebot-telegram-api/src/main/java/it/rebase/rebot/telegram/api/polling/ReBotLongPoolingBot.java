/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.telegram.api.polling;

import it.rebase.rebot.api.object.MessageUpdate;

public interface ReBotLongPoolingBot {

    /**
     * On each update received, all class that implements this interface will be notified
     * @param update Telegram Update
     */
    void onUpdateReceived(MessageUpdate update);

    /**
     * Used to start the bot {@link it.rebase.rebot.telegram.api.UpdatesReceiver}
     * Should be used on the bot implementation
     * Example:
     *      <pre>
     *      &#064;Inject
     *      private UpdatesReceiver receiver;
     *
     *          public void start() {
     *              receiver.start();
     *          }
     *          </pre>
     */
    void start();

    /**
     * Used to stop bot, it will stop any Thread in execution
     * Should be used on the bot implementation
     * Example:
     *      <pre>
     *      &#064;Inject
     *      private UpdatesReceiver receiver;
     *
     *          public void stop() {
     *              receiver.interrupt();
     *          }
     *      </pre>
     */
    void stop();
}