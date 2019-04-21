package it.rebase.rebot.api.object;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "user",
        "can_be_edited",
        "can_change_info",
        "can_delete_messages",
        "can_invite_users",
        "can_pin_messages",
        "can_promote_members",
        "can_restrict_members"
})
public class ChatAdministrator {
    @JsonProperty("status")
    private String status;
    @JsonProperty("user")
    private User user;
    @JsonProperty("can_be_edited")
    private Boolean canBeEdited;
    @JsonProperty("can_change_info")
    private Boolean canChangeInfo;
    @JsonProperty("can_delete_messages")
    private Boolean canDeleteMessages;
    @JsonProperty("can_invite_users")
    private Boolean canInviteUsers;
    @JsonProperty("can_pin_messages")
    private Boolean canPinMessages;
    @JsonProperty("can_promote_members")
    private Boolean canPromoteMembers;
    @JsonProperty("can_restrict_members")
    private Boolean canRestrictMembers;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("can_be_edited")
    public Boolean getCanBeEdited() {
        return canBeEdited;
    }

    @JsonProperty("can_be_edited")
    public void setCanBeEdited(Boolean canBeEdited) {
        this.canBeEdited = canBeEdited;
    }

    @JsonProperty("can_change_info")
    public Boolean getCanChangeInfo() {
        return canChangeInfo;
    }

    @JsonProperty("can_change_info")
    public void setCanChangeInfo(Boolean canChangeInfo) {
        this.canChangeInfo = canChangeInfo;
    }

    @JsonProperty("can_delete_messages")
    public Boolean getCanDeleteMessages() {
        return canDeleteMessages;
    }

    @JsonProperty("can_delete_messages")
    public void setCanDeleteMessages(Boolean canDeleteMessages) {
        this.canDeleteMessages = canDeleteMessages;
    }

    @JsonProperty("can_invite_users")
    public Boolean getCanInviteUsers() {
        return canInviteUsers;
    }

    @JsonProperty("can_invite_users")
    public void setCanInviteUsers(Boolean canInviteUsers) {
        this.canInviteUsers = canInviteUsers;
    }

    @JsonProperty("can_pin_messages")
    public Boolean getCanPinMessages() {
        return canPinMessages;
    }

    @JsonProperty("can_pin_messages")
    public void setCanPinMessages(Boolean canPinMessages) {
        this.canPinMessages = canPinMessages;
    }

    @JsonProperty("can_promote_members")
    public Boolean getCanPromoteMembers() {
        return canPromoteMembers;
    }

    @JsonProperty("can_promote_members")
    public void setCanPromoteMembers(Boolean canPromoteMembers) {
        this.canPromoteMembers = canPromoteMembers;
    }

    @JsonProperty("can_restrict_members")
    public Boolean getCanRestrictMembers() {
        return canRestrictMembers;
    }

    @JsonProperty("can_restrict_members")
    public void setCanRestrictMembers(Boolean canRestrictMembers) {
        this.canRestrictMembers = canRestrictMembers;
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
        return "ChatAdministrator{" +
                "status='" + status + '\'' +
                ", user=" + user.toString() +
                ", canBeEdited=" + canBeEdited +
                ", canChangeInfo=" + canChangeInfo +
                ", canDeleteMessages=" + canDeleteMessages +
                ", canInviteUsers=" + canInviteUsers +
                ", canPinMessages=" + canPinMessages +
                ", canPromoteMembers=" + canPromoteMembers +
                ", canRestrictMembers=" + canRestrictMembers +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}