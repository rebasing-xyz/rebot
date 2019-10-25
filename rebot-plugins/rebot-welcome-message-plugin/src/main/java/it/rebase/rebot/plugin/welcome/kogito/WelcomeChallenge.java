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

package it.rebase.rebot.plugin.welcome.kogito;

import java.io.Serializable;
import java.util.Random;

public class WelcomeChallenge implements Serializable {

    private int number1;
    private int number2;
    private String op;
    private String user;
    private long chat_id;
    private long user_id;
    private String locale;
    private int answer;
    private boolean kickUser;

    public WelcomeChallenge() {}

    /**
     * randomize two numbers and a math operator to start the challenge
     */
    public WelcomeChallenge(String username) {
        this.number1 = randomNumber(100);
        this.number2 = randomNumber(100);
        this.op = defineMathOp();
        this.user = username;
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

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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
