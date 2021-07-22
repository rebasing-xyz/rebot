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

package xyz.rebasing.rebot.api.conf.systemproperties;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

/**
 * {@link BotProperty} String producer
 */
@ApplicationScoped
public class BotPropertyProducerBean {

    Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private String PROPERTIES_FILE = "META-INF/microprofile-config.properties";
    final Properties prop = new Properties();

    @Produces
    @Dependent
    @BotProperty(name = "")
    public String findBotStringProperty(InjectionPoint injectionPoint) {
        BotProperty prop = injectionPoint.getAnnotated().getAnnotation(BotProperty.class);
        String property = readSysProperty(prop.name(), prop.value());
        log.debugv("Injecting String Property name: [{0}] value: [{1}] required [{2}]",
                   prop.name(), property, prop.required());
        if (prop.required() && (null == property) || property.equals("")) {
            throw new IllegalStateException("The parameter " + prop.name() + " is required!");
        }
        return property;
    }

    @Produces
    @Dependent
    @BotProperty(name = "")
    public boolean findBotBoolProperty(InjectionPoint injectionPoint) {
        BotProperty prop = injectionPoint.getAnnotated().getAnnotation(BotProperty.class);
        boolean property = Boolean.parseBoolean(readSysProperty(prop.name(), prop.value()));
        log.debugv("Injecting boolean Property name: [{0}] value: [{1}] required [{2}]",
                   prop.name(),property, prop.required());
        if (prop.required()) {
            throw new IllegalStateException("The parameter " + prop.name() + " is required!");
        }
        return property;
    }

    @Produces
    @Dependent
    @BotProperty(name = "")
    public int findBotIntProperty(InjectionPoint injectionPoint) {
        BotProperty prop = injectionPoint.getAnnotated().getAnnotation(BotProperty.class);
        String property = readSysProperty(prop.name(), prop.value());
        try {
            if (null == property || prop.value().isEmpty()) {
                log.debugv("No value for [{0}], defaulting to -1", prop.name());
                return -1;
            }
            int parsedProperty = Integer.parseInt(property);
            log.debugv("Injecting integer Property name: [{0}] value: [{1}] required [{2}]",
                       prop.name(), parsedProperty, prop.required());

            if (prop.required()) {
                throw new IllegalStateException("The parameter " + prop.name() + " is required!");
            }

            return parsedProperty;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("System Property [" + prop.name() + "] requires a integer value, provided is [" + prop.value() + "].");
        }
    }

    /**
     * Read System Properties from
     * - system properties from command line
     * - properties file located on classpath
     * <p>
     * Supports environment variable substitution on the properties file.
     */
    private String readSysProperty(String propName, String defaultValue) {
        Pattern pattern = Pattern.compile("\\$\\{.*?\\}");

        String property = null;
        String value = System.getProperty(propName);
        if (null != value) {
            return value;
        } else if (defaultValue != "") {
            return defaultValue;
        } else {

            try (final InputStream stream = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE)) {
                prop.load(stream);
                Matcher matcher = pattern.matcher(prop.getProperty(propName));
                if (matcher.find()) {
                    String envVar = prop.getProperty(propName).substring(matcher.start() + 2, matcher.end() - 1);
                    property = System.getenv(envVar);
                    log.tracev("Read environment variable [{0}] from properties file, new value [{1}]", envVar, property);
                    log.trace("Command line System properties takes precedence.");
                    return property;
                } else {
                    return System.getProperty(propName, prop.getProperty(propName));
                }
            } catch (final Exception e) {
                log.warnv("Loading props file failed: {0}", e.getMessage());
                return System.getProperty(propName, prop.getProperty(propName));
            }
        }
    }
}