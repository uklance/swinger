package com.swinger;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final AtomicLong nextId = new AtomicLong(0);

    public String nextId(String prefix) {
        return prefix + nextId.getAndIncrement();
    }
}
