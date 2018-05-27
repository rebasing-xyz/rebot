package it.rebase.rebot.plugin.urbandictionary.test;

import it.rebase.rebot.plugin.helper.Helper;
import org.junit.Assert;
import org.junit.Test;

public class CommandTest {


    @Test
    public void testNegativeParameter(){
        Helper helper = new Helper();
        Assert.assertEquals(helper.query("/urban -c -1 lol"), "Only positive values are valid: -1");
    }

    @Test
    public void testNonIntegerParameter(){
        Helper helper = new Helper();
        Assert.assertEquals(helper.query("/urban -c lol"), "Parameter lol is not valid.");
    }
}