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

package xyz.rebasing.rebot.api.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelegramResponse<T> implements Serializable {

    private final static long serialVersionUID = 939578928398485021L;

    static final String OK_FIELD = "ok";
    static final String ERROR_CODE_FIELD = "error_code";
    static final String DESCRIPTION_CODE_FIELD = "description";
    static final String PARAMETERS_FIELD = "parameters";
    static final String RESULT_FIELD = "result";
    @JsonProperty("ok")
    private Boolean ok;
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("description")
    private String errorDescription;
    @JsonProperty("result")
    private T result;

    public Boolean getOk() {
        return this.ok;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public T getResult() {
        return this.result;
    }

    public String toString() {
        return this.ok.booleanValue() ? "ApiResponse{ok=" + this.ok + ", result=" + this.result + '}' :
                "ApiResponse{ok=" + this.ok + ", errorCode=" + this.errorCode + ", errorDescription='" + this.errorDescription + '\'' + '}';
    }

    public boolean hasError() {
        return null != this.errorDescription;
    }
}