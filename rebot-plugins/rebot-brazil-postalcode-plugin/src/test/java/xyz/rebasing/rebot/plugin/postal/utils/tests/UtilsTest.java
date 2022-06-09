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

package xyz.rebasing.rebot.plugin.postal.utils.tests;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import xyz.rebasing.rebot.plugin.postalcode.utils.BrazilPostalCodeUtils;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UtilsTest {

    @Inject
    BrazilPostalCodeUtils utils;

    @BeforeAll
    public void prepare() {
        // call the processCSVFile method to pre load the cache
        utils.processCSVFile();
    }

    @Test
    public void testQueryKeySimulateNotFound() {
        Assertions
                .assertEquals("County/Postal Code <b>Uberlandia</b> not found! \uD83D\uDEA9",
                              utils.query("Uberlandia", 0, false, "en_US"));
    }

    @Test
    public void testQueryKey() {
        Assertions
                .assertEquals("<b>UBERLANDIA - MG </b>: national code <b>34</b>\n",
                              utils.query("Uberlandia", 1, false, "en_US"));
    }

    @Test
    public void testQueryKeyWithLimit() {
        Assertions.assertEquals("<b>DELTA - MG </b>: national code <b>34</b>\n" +
                                        "<b>PERDIZES - MG </b>: national code <b>34</b>\n" +
                                        "<b>INDIANOPOLIS - MG </b>: national code <b>34</b>\n",
                                utils.query("34", 3, false, "en_US"));
    }

    @Test
    public void testQueryKeyWithReturnUF() {
        Assertions.assertEquals("The <b>UF</b> for <b>34</b> is <b>MG</b>",
                                utils.query("34", 1, true, "en_US"));
    }
}
