package pl.sparkbit.commons.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import org.slf4j.Marker;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Logback [Filter] which counts the output size of all messages, and beyond [maxBytes], drops new messages.
 * The counter is restarted every [epochLengthMillis].
 */
public class OutputSizeCappingFilter extends TurboFilter {

    private int maxBytes = 60_000_000;
    private long epochLengthMillis = 60_000;
    private EpochBytesCounter latestEpochCounter = new EpochBytesCounter(0);

    @Override
    public void start() {
        long epochIdentifier = System.currentTimeMillis() / epochLengthMillis;
        synchronized (this) {
            latestEpochCounter = new EpochBytesCounter(epochIdentifier);
        }

        super.start();
    }

    @Override
    public void stop() {
        synchronized (this) {
            latestEpochCounter = null;
        }

        super.stop();
    }

    private EpochBytesCounter currentEpoch() {
        long newEpochIdentifier = System.currentTimeMillis() / epochLengthMillis;
        synchronized (this) {
            if (newEpochIdentifier > latestEpochCounter.epochIdentifier) {
                latestEpochCounter = new EpochBytesCounter(newEpochIdentifier);
            }
            return latestEpochCounter;
        }
    }

    private final class EpochBytesCounter {
        private final long epochIdentifier;
        private AtomicLong bytesCounter;
        private boolean reporterd = false;

        private EpochBytesCounter(long epochIdentifier) {
            this.epochIdentifier = epochIdentifier;
            this.bytesCounter = new AtomicLong(0L);
        }

        public long incrementCounter(long newBytes) {
            return bytesCounter.addAndGet(newBytes);
        }

        public void reportOnce() {
            synchronized (this) {
                if (reporterd) {
                    return;
                }
                reporterd = true;
            }

            addStatus(
                    new WarnStatus(
                            "Log size limit reached - " + maxBytes +
                                    " bytes. New messages wil not be appended for a while.",
                            OutputSizeCappingFilter.this
                    )
            );
        }
    }

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level,
                              final String format, final Object[] params, final Throwable t) {
        EpochBytesCounter epoch = currentEpoch();

        long newBytesEstimate = logger.getName().length() * 2
                + ((level != null) ? level.toString().length() * 2 : 0)
                + ((format != null) ? format.length() : 0)
                + ((params != null) ? Arrays.stream(params)
                    .filter(s -> s != null)
                    .mapToInt(o -> o.toString().length())
                    .sum() : 0)
                + Arrays.stream(t.getStackTrace()).filter(s -> s != null).mapToInt(o -> o.toString().length()).sum();
        long totalSize = epoch.incrementCounter(newBytesEstimate);
        if (totalSize > maxBytes) {
            epoch.reportOnce();
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }
}
