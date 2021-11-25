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

package xyz.rebasing.rebot.plugin.urbandictionary.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TermDefinition implements Serializable {

    @JsonProperty("defid")
    private long defId;
    private String word;
    private String author;
    private String permalink;
    private String definition;
    private String example;
    @JsonProperty("written_on")
    private String writtenOn;
    @JsonProperty("thumbs_up")
    private int thumbsUp;
    @JsonProperty("thumbs_down")
    private int thumbsDown;
    @JsonProperty("current_vote")
    private String currentVote;
    @JsonProperty("sound_urls")
    private List<String> soundUrls;

    public long getDefId() {
        return defId;
    }

    public void setDefId(long defId) {
        this.defId = defId;
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

    public String getWrittenOn() {
        return writtenOn;
    }

    public void setWrittenOn(String writtenOn) {
        this.writtenOn = writtenOn;
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

    public List<String> getSoundUrls() {
        return soundUrls;
    }

    public void setSoundUrls(List<String> soundUrls) {
        this.soundUrls = soundUrls;
    }

    @Override
    public String toString() {
        return "TermDefinition{" +
                "defid=" + defId +
                ", word='" + word + '\'' +
                ", author='" + author + '\'' +
                ", permalink='" + permalink + '\'' +
                ", definition='" + definition + '\'' +
                ", example='" + example + '\'' +
                ", written_on='" + writtenOn + '\'' +
                ", thumbsUp=" + thumbsUp +
                ", thumbsDown=" + thumbsDown +
                ", currentVote='" + currentVote + '\'' +
                ", soundUrls=" + soundUrls +
                '}';
    }
}
