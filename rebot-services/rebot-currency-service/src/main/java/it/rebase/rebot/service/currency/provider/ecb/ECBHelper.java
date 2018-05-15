package it.rebase.rebot.service.currency.provider.ecb;

import it.rebase.rebot.service.persistence.pojo.Cube;

import javax.inject.Inject;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;

public class ECBHelper {

    public static final String DEFAULT_BASE_CURRENCY = "USD";
    public static final String DEFAULT_SYMBOLS = "BRL,USD,GBP,EUR";

    public static double calculateRateConversion(Cube baseCurrency, Optional<Cube> targetCurrency, double targetExrate) {
        double baseRate = 0;
        if (null == baseCurrency) {
            return formatNumber(targetCurrency.get().getRate());
        } else {
            baseRate = (1 * targetExrate) / baseCurrency.getRate();
        }

        if (!targetCurrency.isPresent()) {
            return formatNumber(baseRate);
        } else {
            Double base = formatNumber(baseRate);
            Double finalConversion = formatNumber(targetCurrency.get().getRate() * base);
            return finalConversion;
        }

    }

    private static double formatNumber(Double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return Double.parseDouble(decimalFormat.format(number));
    }

}