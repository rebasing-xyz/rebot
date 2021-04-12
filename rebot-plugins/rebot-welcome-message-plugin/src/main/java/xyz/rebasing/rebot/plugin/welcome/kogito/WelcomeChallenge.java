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

package xyz.rebasing.rebot.plugin.welcome.kogito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WelcomeChallenge {

    private int number1;
    private int number2;
    private String op;
    private String user;
    private String name;
    private long chatId;
    private String chatTitle;
    private long messageId;
    private List<Long> messadeIdToDelete = new ArrayList<>();
    private long userId;
    private String locale;
    private int answer;
    private boolean kickUser;
    private boolean isNewComerBot;

    public WelcomeChallenge() {
    }

    /**
     * randomize two numbers and a math operator to start the challenge
     */
    public WelcomeChallenge(String user) {
        this.number1 = randomNumber(10);
        this.number2 = randomNumber(10);
        this.op = defineMathOp();
        this.user = user;
        this.name = user.split("-")[0];
        this.kickUser = true;
    }

    public int getNumber1() {
        return number1;
    }

    public int getNumber2() {
        return number2;
    }

    public String getOp() {
        return op;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKickUser() {
        return kickUser;
    }

    public void setKickUser(boolean kickUser) {
        this.kickUser = kickUser;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        if (answer == result()) {
            this.kickUser = false;
        } else {
            this.kickUser = true;
        }
        this.answer = answer;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public List<Long> getMessadeIdToDelete() {
        return messadeIdToDelete;
    }

    public void addMessadeIdToDelete(long messageId) {
        this.messadeIdToDelete.add(messageId);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isNewComerBot() {
        return isNewComerBot;
    }

    public void setNewComerBot(boolean newComerBot) {
        isNewComerBot = newComerBot;
    }

    public String showMathOperation() {
        return this.number1 + " " + this.op + " " + this.number2 + " = ?";
    }

    public int result() {

        switch (this.op) {
            case "+":
                return this.number1 + this.number2;
            case "-":
                return this.number1 - this.number2;
            case "*":
                return this.number1 * this.number2;
            default:
                // no default value expected
                return 0;
        }
    }

    private String defineMathOp() {
        int op = randomNumber(2);
        switch (op) {
            case 0:
                return "+";
            case 1:
                return "-";
            case 2:
                return "*";
            default:
                // no default value expected
                return null;
        }
    }

    /**
     * @return random integer number
     */
    private int randomNumber(int max) {
        return new Random().nextInt(max);
    }

    @Override
    public String toString() {
        return "WelcomeChallenge{" +
                "number1=" + number1 +
                ", number2=" + number2 +
                ", op='" + op + '\'' +
                ", user='" + user + '\'' +
                ", answer=" + answer +
                ", kickUser=" + kickUser +
                '}';
    }
}
