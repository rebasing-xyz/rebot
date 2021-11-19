/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebasing.xyz ReBot
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rebasing.rebot.service.persistence.repository;

import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.service.persistence.domain.BotStatus;
import xyz.rebasing.rebot.service.persistence.domain.CommandStatus;

@Transactional
@ApplicationScoped
public class ApiRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    EntityManager em;

    /**
     * Persist the bot state in the database
     *
     * @param botStatus {@link BotStatus}
     */
    public void persist(BotStatus botStatus) {
        log.debugv("Persisting {0}", botStatus.toString());
        em.persist(botStatus);
        em.flush();
    }

    /**
     * Delete the bot state in the database for the given chatId
     *
     * @param chatId {@link long}
     */
    public void remove(long chatId) {
        log.debugv("Enabling bot for chat {0}", chatId);
        Query q = em.createNativeQuery("DELETE FROM BOT_STATUS where ID=" + chatId + ";");
        q.executeUpdate();
        em.flush();
    }

    /**
     * @return if the bot is enabled or not
     * In case there is no state saved return true.
     *
     * @param chatId chat id to verify if the bos enabled
     */
    public boolean isBotEnabled(long chatId) {
        try {
            Query q = em.createNativeQuery("SELECT isEnabled from BOT_STATUS where ID=" + chatId + ";");
            return (boolean) q.getSingleResult();
        } catch (final Exception e) {
            return true;
        }
    }

    /**
     * Check if the given command is active in the provided chat group
     *
     * @param groupId chat group to be verified
     * @param commandName command to verify
     * @return if the given command is enabled is enabled or not
     */
    public boolean isCommandEnabled(long groupId, String commandName) {
        try {
            Query q = em.createNativeQuery("SELECT isEnabled from COMMAND_STATUS where groupID=" + groupId + " and commandName='" + commandName + "';");
            return (boolean) q.getSingleResult();
        } catch (final Exception e) {
            return true;
        }
    }

    /**
     * Enable the given command in the provided chatId
     *
     * @param chatId chat id or group to be verified
     * @param commandName command to be enabled
     */
    public void enableCommand(long chatId, String commandName) {
        log.debugv("Enabling bot command {0} for chat {1}", commandName, chatId);
        Query q = em.createNativeQuery("DELETE FROM COMMAND_STATUS where groupID=" + chatId + " and commandName='" + commandName + "';");
        q.executeUpdate();
        em.flush();
    }

    /**
     * Disable the given command
     *
     * @param commandStatus {@link CommandStatus} command to be deleted
     */
    public void disableCommand(CommandStatus commandStatus) {
        log.debugv("Disabling command {0}", commandStatus.toString());
        em.persist(commandStatus);
        em.flush();
    }
}