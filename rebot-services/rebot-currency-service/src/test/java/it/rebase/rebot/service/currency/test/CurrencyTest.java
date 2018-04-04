/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.service.currency.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.service.currency.CurrencyCommand;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class CurrencyTest {

    private String currencyName = "{ " +
            "\"ok\": true, " +
            "\"result\": [{ " +
            "\"update_id\": 615374055, " +
            "\"message\": { " +
            "\"message_id\": 3501, " +
            "\"from\": { " +
            "\"id\": 11234485, " +
            "\"is_bot\": false, " +
            "\"first_name\": \"Filippe\", " +
            "\"last_name\": \"Spolti\", " +
            "\"username\": \"fspolti\", " +
            "\"language_code\": \"en-US\" " +
            "}," +
            "\"chat\": { " +
            "\"id\": -11234485, " +
            "\"title\": \"testBots\", " +
            "\"type\": \"group\", " +
            "\"all_members_are_administrators\": true " +
            "}, " +
            "\"date\": 1520870671, " +
            "\"text\": \"/currency name gbp\", " +
            "\"entities\": [ " +
            "{ " +
            "\"offset\": 0, " +
            "\"length\": 9, " +
            "\"type\": \"bot_command\" " +
            "}]}}]}";

    private String currencyGet = "{ " +
            "\"ok\": true, " +
            "\"result\": [{" +
            "\"update_id\": 615374056, " +
            "\"message\": { " +
            "\"message_id\": 3502, " +
            "\"from\": { " +
            "\"id\": 11234485, " +
            "\"is_bot\": false, " +
            "\"first_name\": \"Filippe\", " +
            "\"last_name\": \"Spolti\", " +
            "\"username\": \"fspolti\", " +
            "\"language_code\": \"en-US\" " +
            "}, " +
            "\"chat\": { " +
            "\"id\": -11234485, " +
            "\"title\": \"testBots\", " +
            "\"type\": \"group\", " +
            "\"all_members_are_administrators\": true " +
            "}, " +
            "\"date\": 1520870680, " +
            "\"text\": \"/currency get\", " +
            "\"entities\": [ " +
            "{ " +
            "\"offset\": 0, " +
            "\"length\": 9, " +
            "\"type\": \"bot_command\" " +
            "}]}}]}";

    private String currencyExrate = "{ " +
            "\"ok\": true, " +
            "\"result\": [{ " +
            "\"update_id\": 615374057, " +
            "\"message\": { " +
            "\"message_id\": 3503, " +
            "\"from\": { " +
            "\"id\": 14289485, " +
            "\"is_bot\": false, " +
            "\"first_name\": \"Filippe\", " +
            "\"last_name\": \"Spolti\", " +
            "\"username\": \"fspolti\", " +
            "\"language_code\": \"en-US\" " +
            "}, " +
            "\"chat\": { " +
            "\"id\": -11234485, " +
            "\"title\": \"testBots\", " +
            "\"type\": \"group\", " +
            "\"all_members_are_administrators\": true " +
            "}, " +
            "\"date\": 1520870687, " +
            "\"text\": \"/currency exrate 100USD-GBP\", " +
            "\"entities\": [ " +
            "{ " +
            "\"offset\": 0, " +
            "\"length\": 9, " +
            "\"type\": \"bot_command\" " +
            "}]}}]}";

    private String currencyBase = "{ " +
            "\"ok\": true, " +
            "\"result\": [{ " +
            "\"update_id\": 615374058, " +
            "\"message\": { " +
            "\"message_id\": 3504, " +
            "\"from\": { " +
            "\"id\": 11234485, " +
            "\"is_bot\": false, " +
            "\"first_name\": \"Filippe\", " +
            "\"last_name\": \"Spolti\", " +
            "\"username\": \"fspolti\", " +
            "\"language_code\": \"en-US\" " +
            "}, " +
            "\"chat\": { " +
            "\"id\": -11234485, " +
            "\"title\": \"testBots\", " +
            "\"type\": \"group\", " +
            "\"all_members_are_administrators\": true " +
            "}, " +
            "\"date\": 1520870697, " +
            "\"text\": \"/currency base BRL USD,GBP,AUD\", " +
            "\"entities\": [ " +
            "{ " +
            "\"offset\": 0, " +
            "\"length\": 9, " +
            "\"type\": \"bot_command\" " +
            "}]}}]}";

    private String currency = "{ " +
            "\"ok\": true, " +
            "\"result\": [{ " +
            "\"update_id\": 615374059, " +
            "\"message\": { " +
            "\"message_id\": 3505, " +
            "\"from\": { " +
            "\"id\": 11234485, " +
            "\"is_bot\": false, " +
            "\"first_name\": \"Filippe\", " +
            "\"last_name\": \"Spolti\", " +
            "\"username\": \"fspolti\", " +
            "\"language_code\": \"en-US\" " +
            "}, " +
            "\"chat\": { " +
            "\"id\": -11234485, " +
            "\"title\": \"testBots\", " +
            "\"type\": \"group\", " +
            "\"all_members_are_administrators\": true " +
            "}, " +
            "\"date\": 1520870711, " +
            "\"text\": \"/currency BRL\", " +
            "\"entities\": [ " +
            "{ " +
            "\"offset\": 0, " +
            "\"length\": 9, " +
            "\"type\": \"bot_command\" " +
            "}]}}]}";

    @Test
    public void testCurrencyName() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currencyName).getResult().get(0);
        Assert.assertEquals("/currency name gbp", message.getMessage().getText());

        Assert.assertEquals("Pound", currencyCommand.execute(Optional.of("name gbp"), message));
    }

    @Test
    public void testCurrencyGet() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currencyGet).getResult().get(0);
        Assert.assertEquals("/currency get", message.getMessage().getText());
        Assert.assertEquals("[AUD, BGN, BRL, CAD, CHF, CNY, CZK, DKK, EUR, GBP, HKD, HRK, HUF, IDR, ILS, INR, JPY, KRW, MXN, MYR, NOK, NZD, PHP, PLN, RON, RUB, SEK, SGD, THB, TRY, USD, ZAR]", currencyCommand.execute(Optional.of("get"), message));
    }

    @Test
    public void testCurrencyExrate() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currencyExrate).getResult().get(0);
        Assert.assertEquals("/currency exrate 100USD-GBP", message.getMessage().getText());
        Assert.assertThat((String) currencyCommand.execute(Optional.of("exrate 100USD-GBP"), message),
                Matchers.matchesPattern("100 USD = <b>*.*</b>"));
    }

    @Test
    public void testCurrencyBase() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currencyBase).getResult().get(0);
        Assert.assertEquals("/currency base BRL USD,GBP,AUD", message.getMessage().getText());
        Assert.assertThat((String)currencyCommand.execute(Optional.of("base BRL USD,GBP,AUD"), message),
                Matchers.matchesPattern("Base: <b>BRL</b>\n" +
                "   - 1 <b>BRL</b> = <code>*.*</code> <b>USD</b>\n" +
                "   - 1 <b>BRL</b> = <code>*.*</code> <b>GBP</b>\n" +
                "   - 1 <b>BRL</b> = <code>*.*</code> <b>AUD</b>\n"));
    }

    @Test
    public void testCurrency() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currency).getResult().get(0);
        Assert.assertEquals("/currency BRL", message.getMessage().getText());
        Assert.assertThat((String) currencyCommand.execute(Optional.of("BRL"), message),
                Matchers.matchesPattern("Base: <b>USD</b>\n" +
                        "   - 1 <b>USD</b> = <code>*.*</code> <b>BRL</b>\n"));
    }

    @Test
    public void testCurrencyNoParameters() throws IOException {
        CurrencyCommand currencyCommand = new CurrencyCommand();
        MessageUpdate message = processUpdates(currency).getResult().get(0);
        Assert.assertEquals("Parameter is required.", currencyCommand.execute(Optional.of(""), message));
    }

    private TelegramResponse<ArrayList<MessageUpdate>> processUpdates(String update) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(update, new TypeReference<TelegramResponse<ArrayList<MessageUpdate>>>() {
        });
    }
}