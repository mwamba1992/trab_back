package tz.go.mof.trab.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private final Semaphore semaphore;

    public RateLimiter(int permits, TimeUnit timeUnit) {
        this.semaphore = new Semaphore(permits);
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }
}
