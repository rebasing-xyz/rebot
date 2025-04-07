package xyz.rebasing.rebot.telegram.api.internal.commands;

import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;

@ApplicationScoped
public class TimeCommand implements AdministrativeCommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Override
    public void load() {
        log.debugv("Enabling administrative command {0}", this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        StringBuilder response = new StringBuilder();
        response.append("<b>Current time/date:</b> <code>" + ZonedDateTime.now() + "</code>");
        return response.toString();
    }

    @Override
    public String name() {
        return "/time";
    }

    @Override
    public String help(String locale) {
        return this.name() + " " + this.description(locale);
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Administrative", locale, "time.command.description");
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }
}
