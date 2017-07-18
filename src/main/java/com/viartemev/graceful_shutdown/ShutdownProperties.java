package com.viartemev.graceful_shutdown;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(value = "endpoints.shutdown")
public class ShutdownProperties {

    private long timeout = 3000;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}