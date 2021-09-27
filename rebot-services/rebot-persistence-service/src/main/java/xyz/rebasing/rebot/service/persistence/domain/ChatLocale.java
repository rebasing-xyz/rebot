package xyz.rebasing.rebot.service.persistence.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = "CHAT_LOCALE")
@NamedQuery(name = "ChatLocale.Get",
        query = "SELECT c FROM ChatLocale c where c.chatId = :chatId",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
public class ChatLocale {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private long chatId;

    @Column(name = "CHAT_TITLE", nullable = false)
    private String chatTitle;

    @Column(name = "CHAT_LOCALE_DEFINITION", nullable = false)
    private String chatLocale;

    public ChatLocale(long chatId, String chatTitle, String chatLocale) {
        this.chatId = chatId;
        this.chatTitle = chatTitle;
        this.chatLocale = chatLocale;
    }

    public ChatLocale() {
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatLocale() {
        return chatLocale;
    }

    public void setChatLocale(String chatLocale) {
        this.chatLocale = chatLocale;
    }

    @Override
    public String toString() {
        return "ChatLocale{" +
                "chatId=" + chatId +
                ", chatTitle='" + chatTitle + '\'' +
                ", chatLocale='" + chatLocale + '\'' +
                '}';
    }
}
