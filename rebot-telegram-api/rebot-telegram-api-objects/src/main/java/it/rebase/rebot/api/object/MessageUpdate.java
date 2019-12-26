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

package it.rebase.rebot.api.object;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "update_id",
        "message",
        "edited_message"
})
public class MessageUpdate implements Serializable {

    private final static long serialVersionUID = 83748178983892731L;

    @JsonProperty("update_id")
    private long updateId;
    @JsonProperty("message")
    private Message message;
    @JsonProperty("edited_message")
    private EditedMessage editedMessage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonIgnore
    public boolean isEdited;

    @JsonProperty("update_id")
    public long getUpdateId() {
        return updateId;
    }

    @JsonProperty("update_id")
    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    @JsonProperty("message")
    public Message getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Message message) {
        this.message = message;
    }

    @JsonProperty("edited_message")
    public EditedMessage getEditedMessage() {
        return editedMessage;
    }

    @JsonProperty("edited_message")
    public void setEditedMessage(EditedMessage editedMessage) {
        this.editedMessage = editedMessage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    @Override
    public String toString() {
        return "MessageUpdate{" +
                "updateId=" + updateId +
                ", message=" + message.toString() +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}