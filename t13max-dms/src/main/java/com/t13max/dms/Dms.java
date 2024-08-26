package com.t13max.dms;

/**
 * @author t13max
 * @since 13:43 2024/8/26
 */
public final class Dms {

    private final static Dms dms = new Dms();

    private volatile boolean isOpen = false;

    private Dms() {
    }

    public static Dms getInstance() {
        return dms;
    }

    public synchronized boolean start() {
        if (isOpen) {
            return false;
        }

        return true;
    }
}
