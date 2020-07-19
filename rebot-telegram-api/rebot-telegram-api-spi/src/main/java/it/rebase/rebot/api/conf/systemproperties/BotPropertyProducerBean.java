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

package it.rebase.rebot.api.conf.systemproperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        log.fine("Injecting String Property name: [" + prop.name() + "] value: [" + property + "] required [" + prop.required() + "]");
        if (prop.required() && (null == property) || property == "") {
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
        log.fine("Injecting boolean Property name: [" + prop.name() + "] value: [" + property + "] required [" + prop.required() + "]");
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
            if (null == property) {
                return -1;
            }
            int parsedProperty = Integer.parseInt(property);
            log.fine("Injecting integer Property name: [" + prop.name() + "] value: [" + parsedProperty + "] required [" + prop.required() + "]");

            if (prop.required()) {
                throw new IllegalStateException("The parameter " + prop.name() + " is required!");
            }

            return parsedProperty;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("System Property [" + prop.name() + "] requires a integer value.");
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
                    log.finest("Read environment variable [" + envVar + "] from properties file, new value [" + property + "]");
                    log.finest("Command line System properties takes precedence.");
                    return property;
                } else {
                    return System.getProperty(propName, prop.getProperty(propName));
                }
            } catch (final Exception e) {
                log.warning("Loading props file failed: " + e.getMessage());
                return System.getProperty(propName, prop.getProperty(propName));
            }
        }
    }
}