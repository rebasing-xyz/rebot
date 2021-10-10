package xyz.rebasing.rebot.api.conf;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class BotConfig {

    @ConfigProperty(name = "xyz.rebasing.rebot.telegram.token")
    String botTokenId;

    @ConfigProperty(name = "xyz.rebasing.rebot.telegram.userId")
    String botUserId;

    @ConfigProperty(name = "xyz.rebasing.rebot.delete.messages", defaultValue = "false")
    boolean deleteMessages;

    @ConfigProperty(name = "xyz.rebasing.rebot.delete.messages.after", defaultValue = "120")
    int deleteMessagesAfter;

    public String botTokenId() {
        return botTokenId;
    }

    public String botUserId() {
        return botUserId;
    }

    public boolean deleteMessages() {
        return deleteMessages;
    }

    public int deleteMessagesAfter() {
        return deleteMessagesAfter;
    }
}
