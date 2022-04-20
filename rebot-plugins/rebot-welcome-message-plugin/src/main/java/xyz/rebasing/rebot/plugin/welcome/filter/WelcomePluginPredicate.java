package xyz.rebasing.rebot.plugin.welcome.filter;

import java.util.function.Predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.rebasing.rebot.api.domain.ChatMember;
import xyz.rebasing.rebot.api.domain.MessageUpdate;

public abstract class WelcomePluginPredicate {

    static ObjectMapper mapper = new ObjectMapper();

    public static Predicate<MessageUpdate> hasNewMember() {
        return m -> m.getMessage()
                .getAdditionalProperties()
                .entrySet()
                .stream()
                .filter(key -> key.getKey().equals("new_chat_member"))
                .filter(value -> !mapper.convertValue(value.getValue(), ChatMember.class).isBot())
                .findFirst()
                .isPresent();
    }

    public static Predicate<MessageUpdate> hasMemberLeft() {
        return m -> m.getMessage()
                .getAdditionalProperties()
                .entrySet()
                .stream()
                .filter(key -> key.getKey().equals("left_chat_participant"))
                .filter(value -> !mapper.convertValue(value.getValue(), ChatMember.class).isBot())
                .findFirst()
                .isPresent();
    }

    public static Predicate<MessageUpdate> senderIsNotBot() {
        return m -> !m.getMessage().getFrom().isIsBot();
    }

    public static Predicate<ChatMember> isNewMemberBot() {
        return m -> m.isBot();
    }
}
