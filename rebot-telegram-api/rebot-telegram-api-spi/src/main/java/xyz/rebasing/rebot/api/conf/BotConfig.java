package xyz.rebasing.rebot.api.conf;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import xyz.rebasing.rebot.api.conf.systemproperties.BotProperty;

@ApplicationScoped
public class BotConfig {

    @Inject
    @BotProperty(name = "xyz.rebasing.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    @BotProperty(name = "xyz.rebasing.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    @BotProperty(name = "xyz.rebasing.rebot.delete.messages")
    boolean deleteMessages;

    @Inject
    @BotProperty(name = "xyz.rebasing.rebot.delete.messages.after")
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
