/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.rebasing.rebot.plugin.weather.providers.openweather.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "1h"
})
public class Rain {

    @JsonProperty("1h")
    private double oneHour;

    @JsonProperty("1h")
    public double getOneHour() {
        return oneHour;
    }

    @JsonProperty("1h")
    public void setOneHour(double oneHour) {
        this.oneHour = oneHour;
    }

    @Override
    public String toString() {
        return "Rain{" +
                "1h=" + oneHour +
                '}';
    }
}
