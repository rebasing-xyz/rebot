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
        "edit",
        "admin",
        "important"
})
public class Permissions {

    @JsonProperty("edit")
    private boolean edit;
    @JsonProperty("admin")
    private boolean admin;
    @JsonProperty("important")
    private boolean important;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The edit
     */
    @JsonProperty("edit")
    public boolean isEdit() {
        return edit;
    }

    /**
     *
     * @param edit
     * The edit
     */
    @JsonProperty("edit")
    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    /**
     *
     * @return
     * The admin
     */
    @JsonProperty("admin")
    public boolean isAdmin() {
        return admin;
    }

    /**
     *
     * @param admin
     * The admin
     */
    @JsonProperty("admin")
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     *
     * @return
     * The important
     */
    @JsonProperty("important")
    public boolean isImportant() {
        return important;
    }

    /**
     *
     * @param important
     * The important
     */
    @JsonProperty("important")
    public void setImportant(boolean important) {
        this.important = important;
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