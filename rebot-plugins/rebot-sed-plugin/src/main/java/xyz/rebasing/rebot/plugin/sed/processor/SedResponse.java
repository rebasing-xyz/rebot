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

package xyz.rebasing.rebot.plugin.sed.processor;

import java.lang.invoke.MethodHandles;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.domain.MessageUpdate;

public class SedResponse {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Pattern FULL_MSG_PATTERN = Pattern.compile("^s/.*[/$|/g]");

    private boolean processable;
    private boolean fullReplace;
    private long userId;
    private String oldString;
    private String newString;
    private int middlePosition;
    private String msg;
    private String username;

    public String getUsername() {
        return username;
    }

    public boolean isProcessable() {
        return processable;
    }

    public long getUserId() {
        return userId;
    }

    public String getOldString() {
        return oldString;
    }

    public String getNewString() {
        return newString;
    }

    public String getMsg() {
        return msg;
    }

    public int getMiddlePosition() {
        return middlePosition;
    }

    public boolean isFullReplace() {
        return fullReplace;
    }

    @Override
    public String toString() {
        return "SedResponse {" +
                "FULL_MSG_PATTERN=" + FULL_MSG_PATTERN +
                ", processable=" + processable +
                ", fullReplace=" + fullReplace +
                ", user_id=" + userId +
                ", oldString='" + oldString + '\'' +
                ", newString='" + newString + '\'' +
                ", middlePosition=" + middlePosition +
                ", msg='" + msg + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public SedResponse process(MessageUpdate update) {
        if (null != update.getMessage().getText()) {
            msg = update.getMessage().getText();
            userId = update.getMessage().getFrom().getId();
            username = update.getMessage().getFrom().getUsername();
            int count = 0;
            for (int i = 0; i < msg.length(); i++) {
                if (msg.charAt(i) == '/' && msg.charAt(i - 1) != '\\') {
                    count++;
                    if (count == 2) {
                        middlePosition = i;
                    }
                }
            }
            boolean preProcess = null == msg || count != 3 || msg.equals("s///") || msg.equals("s///g");
            boolean canProcess = !preProcess && FULL_MSG_PATTERN.matcher(msg).find();
            if (canProcess) {
                log.debugv("Sed Plugin - {0}", this.toString());
                fullReplace = msg.endsWith("/g") ? true : false;
                processable = true;
                username = null != update.getMessage().getFrom().getUsername() ? update.getMessage().getFrom().getUsername() : update.getMessage().getFrom().getFirstName();
                oldString = msg.substring(2, middlePosition).replace("\\", "");
                newString = msg.substring(middlePosition + 1, fullReplace ? msg.length() - 2 : msg.length() - 1);
            } else {
                log.debugv("Sed Plugin - can process [{0}] - false", msg);
            }

            processable = canProcess;
        }
        return this;
    }
}