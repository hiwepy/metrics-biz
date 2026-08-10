package com.codahale.metrics.biz.utils;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * High-performance clock that caches {@link System#currentTimeMillis()} in an
 * {@link AtomicLong} and refreshes it on a background daemon thread.
 *
 * <p>In high-concurrency scenarios, calling {@code System.currentTimeMillis()}
 * directly is significantly more expensive than reading an in-memory value
 * because it performs a system call. This class eliminates that overhead by
 * maintaining a cached timestamp that is updated at a configurable interval
 * (default: 1 ms). The daemon thread is automatically reclaimed when the JVM
 * exits.</p>
 *
 * <p>Typical benchmark results (cached vs. direct calls):
 * <ul>
 *   <li>1 billion calls: ~210x faster</li>
 *   <li>100 million calls: ~162x faster</li>
 *   <li>10 million calls: ~40x faster</li>
 *   <li>1 million calls: ~5x faster</li>
 * </ul>
 * </p>
 *
 * @author lry
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see System#currentTimeMillis()
 * @see <a href="http://git.oschina.net/yu120/sequence">Original project</a>
 */
public class SystemClock {

    /**
     * The interval in milliseconds between clock refreshes.
     */
    private final long period;

    /**
     * Cached current time in milliseconds since the epoch.
     */
    private final AtomicLong now;

    /**
     * Creates a new system clock that refreshes every {@code period}
     * milliseconds and immediately schedules a background daemon thread
     * to keep the cached value current.
     *
     * @param period the refresh interval in milliseconds.
     */
    private SystemClock(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    /**
     * Lazy-initialised singleton holder that creates the
     * {@link SystemClock} instance on first access.
     */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

    /**
     * Returns the singleton {@link SystemClock} instance.
     *
     * @return the singleton instance.
     */
    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Starts a single-threaded daemon scheduler that refreshes the
     * cached timestamp at a fixed rate defined by {@link #period}.
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "System Clock");
                thread.setDaemon(true);
                return thread;
            }
        });
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                now.set(System.currentTimeMillis());
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the cached current time in milliseconds.
     *
     * @return the cached timestamp in milliseconds since the epoch.
     */
    private long currentTimeMillis() {
        return now.get();
    }

    /**
     * Returns the current time in milliseconds, using the cached
     * value maintained by the background daemon thread.
     *
     * @return the current time in milliseconds since the epoch.
     */
    public static long now() {
        return instance().currentTimeMillis();
    }

    /**
     * Returns the current time as a human-readable timestamp string
     * backed by the cached value.
     *
     * @return a {@link Timestamp#toString()} representation of the
     *         current time.
     */
    public static String nowDate() {
        return new Timestamp(instance().currentTimeMillis()).toString();
    }

}
