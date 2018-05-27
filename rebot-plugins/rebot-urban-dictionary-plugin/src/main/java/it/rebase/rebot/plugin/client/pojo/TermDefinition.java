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

package it.rebase.rebot.plugin.client.pojo;

import org.codehaus.jackson.annotate.JsonProperty;
import java.io.Serializable;

public class TermDefinition implements Serializable {

    @JsonProperty("defid")
    @com.fasterxml.jackson.annotation.JsonProperty("defid")
    private long defid;
    private String word;
    private String author;
    private String permalink;
    private String definition;
    private String example;
    private String written_on;
    @JsonProperty("thumbs_up")
    @com.fasterxml.jackson.annotation.JsonProperty("thumbs_up")
    private int thumbsUp;
    @JsonProperty("thumbs_down")
    @com.fasterxml.jackson.annotation.JsonProperty("thumbs_down")
    private int thumbsDown;
    @JsonProperty("current_vote")
    @com.fasterxml.jackson.annotation.JsonProperty("current_vote")
    private String currentVote;

    public long getId() {
        return defid;
    }

    public void setId(long defid) {
        this.defid = defid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(int thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public int getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(int thumbsDown) {
        this.thumbsDown = thumbsDown;
    }

    public String getCurrentVote() {
        return currentVote;
    }

    public void setCurrentVote(String currentVote) {
        this.currentVote = currentVote;
    }

    public String getWritten_on() {
        return written_on;
    }

    public void setWritten_on(String written_on) {
        this.written_on = written_on;
    }

    @Override
    public String toString() {
        return "TermDefinition{" +
                "defid=" + defid +
                ", word='" + word + '\'' +
                ", author='" + author + '\'' +
                ", permalink='" + permalink + '\'' +
                ", definition='" + definition + '\'' +
                ", example='" + example + '\'' +
                ", thumbsUp=" + thumbsUp +
                ", thumbsDown=" + thumbsDown +
                ", currentVote='" + currentVote + '\'' +
                '}';
    }
}
