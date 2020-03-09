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

package it.rebase.rebot.telegram.api.message.endpoint;

import it.rebase.rebot.api.object.Chat;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.message.sender.MessageSender;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@Path("/message")
@ApplicationScoped
public class RestMessageSender {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    private MessageSender sender;

    @GET
    @Path("send/{chatId}/{message}")
    public Response send(@PathParam("chatId") Long chatId, @PathParam("message") String message) {
        log.fine("Rest Endpoint called, trying to send the message: [" + message + "] to  chat id [" + chatId + "]");
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