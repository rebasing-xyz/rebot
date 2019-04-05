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

package it.rebase.rebot.service.cache.pojo.urban;

import java.io.Serializable;
import java.util.List;

public class TermDefinition implements Serializable {

    private long defid;
    private String word;
    private String author;
    private String permalink;
    private String definition;
    private String example;
    private String written_on;
    private int thumbs_up;
    private int thumbs_down;
    private String current_vote;
    private List<String> sound_urls;

    public long getDefid() {
        return defid;
    }

    public void setDefid(long defid) {
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

    public String getWritten_on() {
        return written_on;
    }

    public void setWritten_on(String written_on) {
        this.written_on = written_on;
    }

    public int getThumbs_up() {
        return thumbs_up;
    }

    public void setThumbs_up(int thumbs_up) {
        this.thumbs_up = thumbs_up;
    }

    public int getThumbs_down() {
        return thumbs_down;
    }

    public void setThumbs_down(int thumbs_down) {
        this.thumbs_down = thumbs_down;
    }

    public String getCurrent_vote() {
        return current_vote;
    }

    public void setCurrent_vote(String current_vote) {
        this.current_vote = current_vote;
    }

    public List<String> getSound_urls() {
        return sound_urls;
    }

    public void setSound_urls(List<String> sound_urls) {
        this.sound_urls = sound_urls;
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
                ", thumbsUp=" + thumbs_up +
                ", thumbsDown=" + thumbs_down +
                ", currentVote='" + current_vote + '\'' +
                '}';
    }
}
