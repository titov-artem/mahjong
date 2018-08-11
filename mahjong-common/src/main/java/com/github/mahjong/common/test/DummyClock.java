package com.github.mahjong.common.test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Fixed time clock, used for tests.
 */
public class DummyClock extends Clock {

    private final Instant instant;
    private final ZoneId zoneId;

    public DummyClock() {
        this(Instant.parse("2000-01-01T12:00:00.00Z"));
    }

    public DummyClock(Instant instant) {
        this(instant, ZoneOffset.UTC.normalized());
    }

    public DummyClock(Instant instant, ZoneId zoneId) {
        this.instant = instant;
        this.zoneId = zoneId;
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new DummyClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

}
