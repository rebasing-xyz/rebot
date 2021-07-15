package xyz.rebasing.rebot.api.i18n;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jboss.logging.Logger;

public class I18nHelper {

    private static Logger log = org.jboss.logging.Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static String resource(String baseName, String locale, String messageKey) {
        Locale preparedlocale = prepareLocale(locale);
        log.tracev("configured Locale {0}", locale);
        try {
            return ResourceBundle.getBundle(baseName, preparedlocale, new BundleUTF8Control()).getString(messageKey);
        } catch (final Exception e) {
            return e.getMessage();
        }
    }

    /**
     * language set by Telegram is on the following patter en-us, pt-br, etc.
     *
     * @param value
     * @return the locale, if the given locale is not valid, defaults to en_US
     */
    private static Locale prepareLocale(String value) {
        try {
            if (value.contains("_")) {
                return new Locale(value.split("_")[0], value.split("_")[1].toUpperCase());
            }
            return new Locale(value);
        } catch (final Exception e) {
            log.debugv("Failed to retrieve locale: {0}", e.getMessage());
            return new Locale("en", "US");
        }
    }
}
