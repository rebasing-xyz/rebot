/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.rebasing.rebot.plugin.postal.utils.tests;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import xyz.rebasing.rebot.plugin.postal.utils.BrazilPostalCodeUtils;

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
