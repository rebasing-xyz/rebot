package xyz.rebasing.rebot.api.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "first_name",
        "id",
        "is_bot",
        "language_code",
        "last_name",
        "username"
})
public class User implements Serializable {

    private final static long serialVersionUID = 897894174861901932L;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("is_bot")
    private Boolean isBot;
    @JsonProperty("language_code")
    private String languageCode;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("username")
    private String username;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("is_bot")
    public Boolean getIsBot() {
        return isBot;
    }

    @JsonProperty("is_bot")
    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    @JsonProperty("language_code")
    public String getLanguageCode() {
        return languageCode;
    }

    @JsonProperty("language_code")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
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
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", id=" + id +
                ", isBot=" + isBot +
                ", languageCode='" + languageCode + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}