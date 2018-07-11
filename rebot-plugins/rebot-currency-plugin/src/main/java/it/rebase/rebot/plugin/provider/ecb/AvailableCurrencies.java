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

package it.rebase.rebot.plugin.provider.ecb;

public enum AvailableCurrencies {
    AUD("Australian dollar"),
    BGN("Bulgarian lev"),
    BRL("Brazilian real"),
    CAD("Canadian dollar"),
    CHF("Swiss franc"),
    CNY("Renminbi"),
    CZK("Czech Koruna"),
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
    ZAR("Rand"),
    ISK("Icelandic Krona");

    private final String fullName;

    AvailableCurrencies(String fullName){
        this.fullName = fullName;
    }

    public String fullName() {
        return fullName;
    }

}