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

package xyz.rebasing.rebot.service.persistence.domain;

import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import xyz.rebasing.rebot.api.domain.From;

@Entity
@Table(name = "BOT_STATUS")
@Cacheable
public class BotStatus {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "isEnabled", nullable = false)
    private boolean isEnabled;

    @Embedded
    private From from;

    @Column(name = "timestamp", nullable = false)
    private String timestamp;

    public BotStatus(boolean isEnabled, From from, long chatId) {
        this.id = chatId;
        this.isEnabled = isEnabled;
        this.from = from;
        this.timestamp = Instant.now().toString();
    }

    public BotStatus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public From getRequester() {
        return from;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "BotStatus{" +
                "id=" + id +
                ", isEnabled=" + isEnabled +
                ", requester=" + from.toString() +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}