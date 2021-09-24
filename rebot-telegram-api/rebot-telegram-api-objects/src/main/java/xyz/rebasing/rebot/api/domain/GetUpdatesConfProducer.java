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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetUpdatesConfProducer implements Serializable {

    private final static long serialVersionUID = 4331666208495867382L;

    @JsonProperty("offset")
    private Long offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("allowedUpdates")
    private List<String> allowedUpdates;

    public Long getOffset() {
        return offset;
    }

    public GetUpdatesConfProducer setOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public GetUpdatesConfProducer setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public GetUpdatesConfProducer setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public List<String> getAllowedUpdates() {
        return allowedUpdates;
    }

    public GetUpdatesConfProducer setAllowedUpdates(List<String> allowedUpdates) {
        this.allowedUpdates = allowedUpdates;
        return this;
    }

    @Override
    public String toString() {
        return "GetUpdatesConfProducer{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", timeout=" + timeout +
                ", allowedUpdates=" + allowedUpdates +
                '}';
    }
}
