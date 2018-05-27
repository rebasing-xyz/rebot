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

package it.rebase.rebot.plugin.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * This POJO will be used to parser the following JSON file:
 * https://raw.githubusercontent.com/rebase-it/rebot/master/rebot-services/rebot-faq-service/src/main/resources/META-INF/faq-properties.json
 */
@Indexed
public class Project {

    @JsonProperty("id")
    @Field(store = Store.YES, analyze = Analyze.NO)
    public String id;
    @JsonProperty("link")
    @Field(store = Store.YES, analyze = Analyze.NO)
    public String link;
    @JsonProperty("description")
    @Field(store = Store.YES, analyze = Analyze.NO)
    public String description;

    public Project() {
    }

    /*
    * Returns the project name with the project's link in the markdown pattern
    */
    @Override
    public String toString() {
        return "<a href=\"" +  getLink() + "\">" + getId() + "</a>";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}