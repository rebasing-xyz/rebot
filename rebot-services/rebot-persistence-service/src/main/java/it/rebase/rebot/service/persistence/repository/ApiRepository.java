/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
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

package it.rebase.rebot.service.persistence.repository;

import it.rebase.rebot.service.persistence.pojo.BotStatus;
import it.rebase.rebot.service.persistence.pojo.CommandStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.logging.Logger;

@Transactional
@ApplicationScoped
public class ApiRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    EntityManager em;

    /**
     * Persist the bot state in the database
     * @param botStatus {@link BotStatus}
     */
    public void persist(BotStatus botStatus) {
        log.fine("Persisting " + botStatus.toString());
        em.persist(botStatus);
        em.flush();
    }

    /**
     * Delete the bot state in the database for the given chatId
     * @param chatId {@link long}
     */
    public void remove(long chatId) {
        log.fine("Enabling bot for chat  " + chatId);
        Query q = em.createNativeQuery("DELETE FROM BOT_STATUS where ID="+ chatId+";");
        q.executeUpdate();
        em.flush();
    }

    /**
     * @return if the bot is enabled or not
     * In case there is no state saved return true.
     */
    public boolean isBotEnabled(long chatId) {
        try {
            Query q = em.createNativeQuery("SELECT isEnabled from BOT_STATUS where ID="+ chatId+";");
            return (boolean) q.getSingleResult();
        } catch (final Exception e) {
            return true;
        }
    }

    /**
     * @param groupId
     * @param commandName
     * @return if the given command is enabled is enabled or not
     */
    public boolean isCommandEnabled(long groupId, String commandName) {
        try {
            Query q = em.createNativeQuery("SELECT isEnabled from COMMAND_STATUS where groupID="+ groupId+" and commandName='"+ commandName+"';");
            return (boolean) q.getSingleResult();
        } catch (final Exception e) {
            return true;
        }
    }

    /**
     * Enable the given command
     * @param chatId
     * @param commandName
     */
    public void enableCommand(long chatId, String commandName) {
        log.fine("Enabling bot command "+commandName+" for chat  " + chatId);
        Query q = em.createNativeQuery("DELETE FROM COMMAND_STATUS where groupID="+ chatId+" and commandName='"+ commandName+"';");
        q.executeUpdate();
        em.flush();
    }

    /**
     * Disable the given command
     * @param commandStatus
     */
    public void disableCommand(CommandStatus commandStatus) {
        log.fine("Disabling command " + commandStatus.toString());
        em.persist(commandStatus);
        em.flush();
    }

}