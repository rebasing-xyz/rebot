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
        "stars",
        "subscriptions",
        "updates",
        "discussions"
})
public class Counts {

    @JsonProperty("stars")
    private int stars;
    @JsonProperty("subscriptions")
    private int subscriptions;
    @JsonProperty("updates")
    private int updates;
    @JsonProperty("discussions")
    private int discussions;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The stars
     */
    @JsonProperty("stars")
    public int getStars() {
        return stars;
    }

    /**
     *
     * @param stars
     * The stars
     */
    @JsonProperty("stars")
    public void setStars(int stars) {
        this.stars = stars;
    }

    /**
     *
     * @return
     * The subscriptions
     */
    @JsonProperty("subscriptions")
    public int getSubscriptions() {
        return subscriptions;
    }

    /**
     *
     * @param subscriptions
     * The subscriptions
     */
    @JsonProperty("subscriptions")
    public void setSubscriptions(int subscriptions) {
        this.subscriptions = subscriptions;
    }

    /**
     *
     * @return
     * The updates
     */
    @JsonProperty("updates")
    public int getUpdates() {
        return updates;
    }

    /**
     *
     * @param updates
     * The updates
     */
    @JsonProperty("updates")
    public void setUpdates(int updates) {
        this.updates = updates;
    }

    /**
     *
     * @return
     * The discussions
     */
    @JsonProperty("discussions")
    public int getDiscussions() {
        return discussions;
    }

    /**
     *
     * @param discussions
     * The discussions
     */
    @JsonProperty("discussions")
    public void setDiscussions(int discussions) {
        this.discussions = discussions;
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