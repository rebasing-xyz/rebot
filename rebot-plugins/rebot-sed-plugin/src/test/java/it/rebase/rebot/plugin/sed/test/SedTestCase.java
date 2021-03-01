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

package it.rebase.rebot.plugin.sed.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.rebase.rebot.api.object.From;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.plugin.sed.processor.SedResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SedTestCase {

    private SedResponse sedResponse = null;
    private Message message = null;
    private From from = null;
    private MessageUpdate messageUpdate = null;
    private HashMap<Long, String> cache = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        sedResponse = new SedResponse();
        from = new From();
        from.setId(110111);
        message = new Message();
        message.setFrom(from);
        messageUpdate = new MessageUpdate();
        messageUpdate.setMessage(message);
    }

    private final List<String> valuesFalse = Arrays.asList(
            "hello",
            "s/test/test",
            "s/test",
            "s/test/",
            "s/",
            "s///g"
    );

    private final List<String> valuesTrue = Arrays.asList(
            "s/test/tests/g",
            "s/test/tests/",
            "s/?/!|/",
            "s/$/$$/",
            "s/\\^/\\~/",
            "s/ / abc/",
            "s/\\//\\/\\//",
            "s/12/31/"
    );

    @Test
    public void testPluginMatchPatternAllFalse() {
        valuesFalse.stream().forEach(value -> {
            message.setText(value);
            sedResponse.process(messageUpdate);
            Assert.assertFalse(sedResponse.isProcessable());
        });
    }

    @Test
    public void testPluginMatchPatternAllTrue() {
        valuesTrue.stream().forEach(value -> {
            message.setText(value);
            sedResponse.process(messageUpdate);
            Assert.assertTrue(sedResponse.isProcessable());
        });
    }

    @Test
    public void testNormalText() {
        message.setText("s/erada/errada/");
        sedResponse.process(messageUpdate);
        Assert.assertTrue(sedResponse.isProcessable());
        Assert.assertEquals("erada", sedResponse.getOldString());
        Assert.assertEquals("errada", sedResponse.getNewString());
        Assert.assertEquals(7, sedResponse.getMiddlePosition());
    }

    @Test
    public void testMultipleString() {
        message.setText("s/esta frase está erada/achávamos que estava certo/");
        sedResponse.process(messageUpdate);
        Assert.assertTrue(sedResponse.isProcessable());
        Assert.assertEquals("esta frase está erada", sedResponse.getOldString());
        Assert.assertEquals("achávamos que estava certo", sedResponse.getNewString());
        Assert.assertEquals(23, sedResponse.getMiddlePosition());
    }

    @Test
    public void testSpecialCharacter() {
        message.setText("s/trocar $ por &/alguns $#%$/");
        sedResponse.process(messageUpdate);
        Assert.assertTrue(sedResponse.isProcessable());
        Assert.assertEquals("trocar $ por &", sedResponse.getOldString());
        Assert.assertEquals("alguns $#%$", sedResponse.getNewString());
        Assert.assertEquals(16, sedResponse.getMiddlePosition());
    }

    @Test
    public void testReplaceProcessNormalMessage() {
        cache.put(123L, "uma string aqualquer");
        message.setText("s/aqualquer/qualquer/");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("uma string qualquer", cache.get(123L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }

    @Test
    public void testReplaceProcessWithSpecialCharachters() {
        cache.put(123L, "um@ string aqualquer!?$");
        message.setText("s/aqualquer!?$/qualquer/");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("um@ string qualquer", cache.get(123L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }

    @Test
    public void testReplaceProcessWithBackSlash() {
        cache.put(123L, "///");
        message.setText("s/\\/\\/\\//1/");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("1", cache.get(123L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }

    @Test
    public void testReplaceAllBackSlash() {
        cache.put(123L, "///");
        message.setText("s/\\//1/");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("111", cache.get(123L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }

    @Test
    public void testReplaceSomethingToNothing() {
        cache.put(123L, "caramba, tira depois da vírgula!");
        message.setText("s/, tira depois da vírgula//");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("caramba!", cache.get(123L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }

    @Test
    public void testFullReplaceSomethingToNothing() {
        cache.put(1234L, "caramba, tira depois da vírgula!");
        message.setText("s/, tira depois da vírgula//g");
        sedResponse.process(messageUpdate);
        Assert.assertEquals("caramba!", cache.get(1234L).replace(sedResponse.getOldString(), sedResponse.getNewString()));
    }
}