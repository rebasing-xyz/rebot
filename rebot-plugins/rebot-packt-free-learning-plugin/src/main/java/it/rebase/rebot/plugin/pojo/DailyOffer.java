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

package it.rebase.rebot.plugin.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "type",
        "coverImage",
        "productId",
        "isbn13",
        "oneLiner",
        "pages",
        "publicationDate",
        "length",
        "about",
        "learn",
        "features",
        "authors",
        "shopUrl",
        "readUrl",
        "category",
        "earlyAccess",
        "available"
})
public class DailyOffer {

    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("coverImage")
    private String coverImage;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("isbn13")
    private String isbn13;
    @JsonProperty("oneLiner")
    private String oneLiner;
    @JsonProperty("pages")
    private Integer pages;
    @JsonProperty("publicationDate")
    private String publicationDate;
    @JsonProperty("length")
    private String length;
    @JsonProperty("about")
    private String about;
    @JsonProperty("learn")
    private String learn;
    @JsonProperty("features")
    private String features;
    @JsonProperty("authors")
    private List<String> authors = null;
    @JsonProperty("shopUrl")
    private String shopUrl;
    @JsonProperty("readUrl")
    private String readUrl;
    @JsonProperty("category")
    private String category;
    @JsonProperty("earlyAccess")
    private Boolean earlyAccess;
    @JsonProperty("available")
    private Boolean available;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("coverImage")
    public String getCoverImage() {
        return coverImage;
    }

    @JsonProperty("coverImage")
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("isbn13")
    public String getIsbn13() {
        return isbn13;
    }

    @JsonProperty("isbn13")
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    @JsonProperty("oneLiner")
    public String getOneLiner() {
        return oneLiner;
    }

    @JsonProperty("oneLiner")
    public void setOneLiner(String oneLiner) {
        this.oneLiner = oneLiner;
    }

    @JsonProperty("pages")
    public Integer getPages() {
        return pages;
    }

    @JsonProperty("pages")
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    @JsonProperty("publicationDate")
    public String getPublicationDate() {
        return publicationDate;
    }

    @JsonProperty("publicationDate")
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    @JsonProperty("length")
    public String getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(String length) {
        this.length = length;
    }

    @JsonProperty("about")
    public String getAbout() {
        return about;
    }

    @JsonProperty("about")
    public void setAbout(String about) {
        this.about = about;
    }

    @JsonProperty("learn")
    public String getLearn() {
        return learn;
    }

    @JsonProperty("learn")
    public void setLearn(String learn) {
        this.learn = learn;
    }

    @JsonProperty("features")
    public String getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(String features) {
        this.features = features;
    }

    @JsonProperty("authors")
    public List<String> getAuthors() {
        return authors;
    }

    @JsonProperty("authors")
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @JsonProperty("shopUrl")
    public String getShopUrl() {
        return shopUrl;
    }

    @JsonProperty("shopUrl")
    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    @JsonProperty("readUrl")
    public String getReadUrl() {
        return readUrl;
    }

    @JsonProperty("readUrl")
    public void setReadUrl(String readUrl) {
        this.readUrl = readUrl;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("earlyAccess")
    public Boolean getEarlyAccess() {
        return earlyAccess;
    }

    @JsonProperty("earlyAccess")
    public void setEarlyAccess(Boolean earlyAccess) {
        this.earlyAccess = earlyAccess;
    }

    @JsonProperty("available")
    public Boolean getAvailable() {
        return available;
    }

    @JsonProperty("available")
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "DailyOffer{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", productId='" + productId + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", oneLiner='" + oneLiner + '\'' +
                ", pages=" + pages +
                ", publicationDate='" + publicationDate + '\'' +
                ", length='" + length + '\'' +
                ", about='" + about + '\'' +
                ", learn='" + learn + '\'' +
                ", features='" + features + '\'' +
                ", authors=" + authors +
                ", shopUrl='" + shopUrl + '\'' +
                ", readUrl='" + readUrl + '\'' +
                ", category='" + category + '\'' +
                ", earlyAccess=" + earlyAccess +
                ", available=" + available +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}