package net.ccbluex.liquidbounce.utils.timer;

public final class MSTimer {

    private long time = -1L;

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }

    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }

    public void reset() {
        time = System.currentTimeMillis();
    }
}
