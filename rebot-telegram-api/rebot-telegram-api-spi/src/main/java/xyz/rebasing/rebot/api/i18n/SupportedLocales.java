package xyz.rebasing.rebot.api.i18n;

public enum SupportedLocales {

    pt_br("pt_BR"),
    en_us("en_US");

    private final String localeName;

    SupportedLocales(String localeName){
        this.localeName = localeName;
    }

    public String localeName() {
        return localeName;
    }

}
