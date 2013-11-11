package com.feigdev.loadtester;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Provided by Square under the Apache License
 */
public enum BusProvider {
    INSTANCE;

    private final Bus BUS;

    public Bus bus() {
        return BUS;
    }

    private BusProvider() {
        BUS = new Bus(ThreadEnforcer.ANY);
    }
}
