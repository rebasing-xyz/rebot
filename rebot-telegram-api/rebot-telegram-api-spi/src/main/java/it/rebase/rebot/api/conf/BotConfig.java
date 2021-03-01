package it.rebase.rebot.api.conf;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;

@ApplicationScoped
public class BotConfig {

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    @BotProperty(name = "it.rebase.rebot.delete.messages")
    boolean deleteMessages;

    @Inject
    @BotProperty(name = "it.rebase.rebot.delete.messages.after")
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
