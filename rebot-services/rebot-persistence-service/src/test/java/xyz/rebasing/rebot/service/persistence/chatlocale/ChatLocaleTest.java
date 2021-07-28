package xyz.rebasing.rebot.service.persistence.chatlocale;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xyz.rebasing.rebot.service.persistence.domain.ChatLocale;
import xyz.rebasing.rebot.service.persistence.repository.LocaleRepository;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatLocaleTest {

    @Inject
    LocaleRepository localeRepository;

    @Test
    @Order(1)
    public void persistChatLocale() {
        Assertions.assertEquals("persisted", localeRepository.persistChatLocale(new ChatLocale(-1010, "test", "en_us")));
        Assertions.assertEquals("persisted", localeRepository.persistChatLocale(new ChatLocale(-1010, "test", "pt_br")));
    }

    @Test
    @Order(2)
    public void getChatLocale() {
        Assertions.assertEquals("persisted", localeRepository.persistChatLocale(new ChatLocale(-1012, "test", "en_us")));
        Assertions.assertEquals("persisted", localeRepository.persistChatLocale(new ChatLocale(-1011, "test", "pt_br")));
        Assertions.assertEquals(3, localeRepository.getRegisteredChatLocale().size());
    }

    @Order(3)
    @Test
    public void getChatLocaleDefinition() {
        Assertions.assertEquals("pt_br", localeRepository.get(-1011, "chatName"));
    }

    @Order(4)
    @Test
    public void getChatLocaleDefinitionException() {
        Assertions.assertEquals("en_US", localeRepository.get(-1011111, "chatName"));
    }
}
