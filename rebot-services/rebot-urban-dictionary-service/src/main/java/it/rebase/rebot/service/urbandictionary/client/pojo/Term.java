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

package it.rebase.rebot.service.urbandictionary.client.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Term {

    private List<String> tags;
    @JsonProperty("result_type")
    @com.fasterxml.jackson.annotation.JsonProperty("result_type")
    private String resultType;
    @JsonProperty("list")
    @com.fasterxml.jackson.annotation.JsonProperty("list")
    private List<TermDefinition> termDefinitions;
    private List<String> sounds;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<TermDefinition> getTermDefinitions() {
        return termDefinitions;
    }

    public void setTermDefinitions(List<TermDefinition> termDefinitions) {
        this.termDefinitions = termDefinitions;
    }

    public List<String> getSounds() {
        return sounds;
    }

    public void setSounds(List<String> sounds) {
        this.sounds = sounds;
    }

    @Override
    public String toString() {
        return "Term{" +
                "tags=" + tags +
                ", resultType='" + resultType + '\'' +
                ", termDefinitions=" + termDefinitions +
                ", sounds=" + sounds +
                '}';
    }
}
