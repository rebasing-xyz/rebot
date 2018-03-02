package it.rebase.rebot.service.urbandictionary.test;

import it.rebase.rebot.service.urbandictionary.helper.Helper;
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