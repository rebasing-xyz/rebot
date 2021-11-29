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

package xyz.rebasing.rebot.telegram.api.message.endpoint;

import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.domain.Chat;
import xyz.rebasing.rebot.api.domain.Message;
import xyz.rebasing.rebot.api.shared.components.message.sender.MessageSender;

@Path("/message")
@ApplicationScoped
public class RestMessageSender {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    private MessageSender sender;

    @GET
    @Path("send/{chatId}/{message}")
    public Response send(@PathParam("chatId") Long chatId, @PathParam("message") String message) {
        log.debugv("Rest Endpoint called, trying to send the message: [{0}] to  chat id [{1}]", message, chatId);
        try {
            sender.processOutgoingMessage(buildMessage(chatId, message), false, 0L);
            return Response.ok("Message Sent").build();
        } catch (final Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private Message buildMessage(Long target, String txt) {
        Chat chat = new Chat(target);
        Message message = new Message();
        message.setChat(chat);
        message.setText(txt);
        return message;
    }
}