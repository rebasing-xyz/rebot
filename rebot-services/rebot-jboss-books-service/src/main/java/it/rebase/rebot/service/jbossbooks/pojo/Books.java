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

package it.rebase.rebot.service.jbossbooks.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "id",
        "status",
        "name",
        "title",
        "description",
        "public",
        "topics",
        "license",
        "language",
        "locked",
        "cover",
        "urls",
        "counts",
        "dates",
        "permissions",
        "publish",
        "author"
})
public class Books {

    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("name")
    private String name;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("public")
    private boolean _public;
    @JsonProperty("topics")
    private java.util.List<Object> topics = new ArrayList<Object>();
    @JsonProperty("license")
    private String license;
    @JsonProperty("language")
    private String language;
    @JsonProperty("locked")
    private boolean locked;
    @JsonProperty("cover")
    private Cover cover;
    @JsonProperty("urls")
    private Urls urls;
    @JsonProperty("counts")
    private Counts counts;
    @JsonProperty("dates")
    private Dates dates;
    @JsonProperty("permissions")
    private Permissions permissions;
    @JsonProperty("publish")
    private Publish publish;
    @JsonProperty("author")
    private Author author;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The _public
     */
    @JsonProperty("public")
    public boolean isPublic() {
        return _public;
    }

    /**
     *
     * @param _public
     * The public
     */
    @JsonProperty("public")
    public void setPublic(boolean _public) {
        this._public = _public;
    }

    /**
     *
     * @return
     * The topics
     */
    @JsonProperty("topics")
    public java.util.List<Object> getTopics() {
        return topics;
    }

    /**
     *
     * @param topics
     * The topics
     */
    @JsonProperty("topics")
    public void setTopics(java.util.List<Object> topics) {
        this.topics = topics;
    }

    /**
     *
     * @return
     * The license
     */
    @JsonProperty("license")
    public String getLicense() {
        return license;
    }

    /**
     *
     * @param license
     * The license
     */
    @JsonProperty("license")
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     *
     * @return
     * The language
     */
    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The locked
     */
    @JsonProperty("locked")
    public boolean isLocked() {
        return locked;
    }

    /**
     *
     * @param locked
     * The locked
     */
    @JsonProperty("locked")
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     *
     * @return
     * The cover
     */
    @JsonProperty("cover")
    public Cover getCover() {
        return cover;
    }

    /**
     *
     * @param cover
     * The cover
     */
    @JsonProperty("cover")
    public void setCover(Cover cover) {
        this.cover = cover;
    }

    /**
     *
     * @return
     * The urls
     */
    @JsonProperty("urls")
    public Urls getUrls() {
        return urls;
    }

    /**
     *
     * @param urls
     * The urls
     */
    @JsonProperty("urls")
    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    /**
     *
     * @return
     * The counts
     */
    @JsonProperty("counts")
    public Counts getCounts() {
        return counts;
    }

    /**
     *
     * @param counts
     * The counts
     */
    @JsonProperty("counts")
    public void setCounts(Counts counts) {
        this.counts = counts;
    }

    /**
     *
     * @return
     * The dates
     */
    @JsonProperty("dates")
    public Dates getDates() {
        return dates;
    }

    /**
     *
     * @param dates
     * The dates
     */
    @JsonProperty("dates")
    public void setDates(Dates dates) {
        this.dates = dates;
    }

    /**
     *
     * @return
     * The permissions
     */
    @JsonProperty("permissions")
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     *
     * @param permissions
     * The permissions
     */
    @JsonProperty("permissions")
    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    /**
     *
     * @return
     * The publish
     */
    @JsonProperty("publish")
    public Publish getPublish() {
        return publish;
    }

    /**
     *
     * @param publish
     * The publish
     */
    @JsonProperty("publish")
    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    /**
     *
     * @return
     * The author
     */
    @JsonProperty("author")
    public Author getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    @JsonProperty("author")
    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}