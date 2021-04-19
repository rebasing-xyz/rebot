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

package xyz.rebasing.rebot.plugin.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "productId",
        "availableFrom",
        "expiresAt",
        "limitedAmount",
        "amountAvailable",
        "details",
        "priority",
        "createdAt",
        "updatedAt",
        "deletedAt"
})
public class Datum {

    @JsonProperty("id")
    private String id;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("availableFrom")
    private String availableFrom;
    @JsonProperty("expiresAt")
    private String expiresAt;
    @JsonProperty("limitedAmount")
    private Boolean limitedAmount;
    @JsonProperty("amountAvailable")
    private Object amountAvailable;
    @JsonProperty("details")
    private Object details;
    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("deletedAt")
    private Object deletedAt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("availableFrom")
    public String getAvailableFrom() {
        return availableFrom;
    }

    @JsonProperty("availableFrom")
    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    @JsonProperty("expiresAt")
    public String getExpiresAt() {
        return expiresAt;
    }

    @JsonProperty("expiresAt")
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @JsonProperty("limitedAmount")
    public Boolean getLimitedAmount() {
        return limitedAmount;
    }

    @JsonProperty("limitedAmount")
    public void setLimitedAmount(Boolean limitedAmount) {
        this.limitedAmount = limitedAmount;
    }

    @JsonProperty("amountAvailable")
    public Object getAmountAvailable() {
        return amountAvailable;
    }

    @JsonProperty("amountAvailable")
    public void setAmountAvailable(Object amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    @JsonProperty("details")
    public Object getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(Object details) {
        this.details = details;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("deletedAt")
    public Object getDeletedAt() {
        return deletedAt;
    }

    @JsonProperty("deletedAt")
    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "Datum{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", availableFrom='" + availableFrom + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", limitedAmount=" + limitedAmount +
                ", amountAvailable=" + amountAvailable +
                ", details=" + details +
                ", priority=" + priority +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", deletedAt=" + deletedAt +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}