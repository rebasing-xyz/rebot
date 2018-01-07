package it.rebase.rebot.service.currency.provider.fixer.io;

public enum AvailableCurrencies {
    AUD("Australian dollar"),
    BGN("Bulgarian lev"),
    BRL("Brazilian real"),
    CAD("Canadian dollar"),
    CHF("Swiss franc"),
    CNY("Renminbi"),
    CZK("Czech Koruna"),
   // EEK("Estonian kroon"),
    DKK("Danish krone"),
    EUR("Euro"),
    GBP("Pound"),
    HKD("Hong Kong Dollar"),
    HRK("Croatian kuna"),
    HUF("Hungarian forint"),
    IDR("Indonesian rupiah"),
    ILS("Israeli new shekel"),
    INR("Indian rupee"),
    JPY("Yen"),
    KRW("South Korean won"),
//    LTL("Lithuanian litas"),
//    LVL(""),
    MXN("Mexican Peso"),
    MYR("Malaysian ringgit"),
    NOK("Krona Norwegia"),
    NZD("New Zealand dollar"),
    PHP("Philippine peso"),
    PLN("Zloty"),
    RON("Romanian leu"),
    RUB("Ruble"),
    SEK("Swedish krona"),
    SGD("Singapore dollar"),
    THB("Thai baht"),
    TRY("Turkish Lira"),
    USD("Dolar"),
    ZAR("Rand");

    private final String fullName;

    AvailableCurrencies(String fullName){
        this.fullName = fullName;
    }

    public String fullName() {
        return fullName;
    }

}
