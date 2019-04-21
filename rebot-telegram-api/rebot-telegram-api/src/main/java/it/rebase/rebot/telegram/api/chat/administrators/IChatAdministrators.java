package it.rebase.rebot.telegram.api.chat.administrators;

import it.rebase.rebot.api.object.MessageUpdate;

public interface IChatAdministrators {

    boolean isAdministrator(MessageUpdate messageUpdate);
}
