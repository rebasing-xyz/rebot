package it.rebase.rebot.api.i18n;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class I18nHelper {

    private static Logger log = java.util.logging.Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static String resource(String baseName, String telegramLocale, String messageKey) {
        Locale locale = prepareLocale(telegramLocale);
        log.finest("configured Locale " + locale);
        try {
            return ResourceBundle.getBundle(baseName, locale, new BundleUTF8Control()).getString(messageKey);
        } catch (final Exception e) {
            return e.getMessage();
        }
    }

    /**
     * language set by Telegram is on the following patter en-us, pt-br, etc.
     * @param value
     * @return the locale, if the given locale is not valid, defaults to en_US
     */
    private static Locale prepareLocale(String value) {
        try {
            if (value.contains("-")) {
                return new Locale(value.split("-")[0], value.split("-")[1].toUpperCase());
            }
            return new Locale(value.split("-")[0]);
        } catch (final Exception e) {
            log.fine("Failed to retrieve locale: " + e.getMessage());
            return new Locale("en", "US");
        }
    }
}
