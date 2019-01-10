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

package it.rebase.rebot.plugin.currency.ecb;

import it.rebase.rebot.service.persistence.pojo.Cube;
import it.rebase.rebot.service.persistence.pojo.Cubes;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.enterprise.context.ApplicationScoped;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@ApplicationScoped
public class EcbSaxHandler extends DefaultHandler {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Cubes cubes = new Cubes();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        Cube cube = new Cube();
        if ("cube".equalsIgnoreCase(qName)) {
            if (null != attributes.getValue("time")) {
                cubes.setTime(attributes.getValue("time"));
                log.fine("time: " + attributes.getValue("time"));
            } else if (null != attributes.getValue("currency") && null != attributes.getValue("rate")) {
                cube.setCurrency(attributes.getValue("currency"));
                cube.setRate(Float.parseFloat(attributes.getValue("rate")));
                log.fine("Parsing [Currency: " + attributes.getValue("currency") + " - rate: " + attributes.getValue("rate") + "]");
                cubes.addCube(cube);
            }
        }
    }

    public Cubes cubes() {
        return this.cubes;
    }

    public void clean() {this.cubes = new Cubes();}

}