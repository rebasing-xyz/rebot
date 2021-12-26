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

package xyz.rebasing.rebot.plugin.postalcode.domain;

public class PostalCode {

    private String codIBGE;
    private String uf;
    private String county;
    private String nationalCode;

    public PostalCode(String codIBGE, String uf, String county, String nationalCode) {
        this.codIBGE = codIBGE;
        this.uf = uf;
        this.county = county;
        this.nationalCode = nationalCode;
    }

    public String getCodIBGE() {
        return codIBGE;
    }

    public void setCodIBGE(String codIBGE) {
        this.codIBGE = codIBGE;
    }

    public String getUF() {
        return uf;
    }

    public void setUF(String UF) {
        this.uf = uf;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Override
    public String toString() {
        return "PostalCode found: {" +
                "uf='" + uf + '\'' +
                ", county='" + county + '\'' +
                ", nationalCode='" + nationalCode + '\'' +
                '}';
    }
}
