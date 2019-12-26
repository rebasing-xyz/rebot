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

import java.io.Serializable;
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
        "message_id",
        "from",
        "chat",
        "date",
        "edit_date",
        "text",
        "entities"
})
public class EditedMessage implements Serializable {

    private final static long serialVersionUID = 7093141412295077756L;

    @JsonProperty("message_id")
    private long messageId;
    @JsonProperty("from")
    private From from;
    @JsonProperty("chat")
    private Chat chat;
    @JsonProperty("date")
    private long date;
    @JsonProperty("edit_date")
    private long editDate;
    @JsonProperty("text")
    private String text;
    @JsonProperty("entities")
    private List<Entity> entities = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("message_id")
    public long getMessageId() {
        return messageId;
    }

    @JsonProperty("message_id")
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("from")
    public From getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(From from) {
        this.from = from;
    }

    @JsonProperty("chat")
    public Chat getChat() {
        return chat;
    }

    @JsonProperty("chat")
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @JsonProperty("date")
    public long getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(long date) {
        this.date = date;
    }

    @JsonProperty("edit_date")
    public long getEditDate() {
        return editDate;
    }

    @JsonProperty("edit_date")
    public void setEditDate(long editDate) {
        this.editDate = editDate;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("entities")
    public List<Entity> getEntities() {
        return entities;
    }

    @JsonProperty("entities")
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
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
        return "EditedMessage{" +
                "messageId=" + messageId +
                ", from=" + from.toString() +
                ", chat=" + chat.toString() +
                ", date=" + date +
                ", editDate=" + editDate +
                ", text='" + text + '\'' +
                ", entities=" + entities.toString() +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}