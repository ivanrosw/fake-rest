package io.github.ivanrosw.fakerest.core.utils;

import org.springframework.stereotype.Component;

/**
 * Class with system utils
 * It's exist mostly for coverage... :c
 */
@Component
public class SystemUtils {

    public void sleep(long sleepMs) throws InterruptedException {
        Thread.sleep(sleepMs);
    }
}
