package pl.sparkbit.commons.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.status.WarnStatus
import java.util.concurrent.atomic.AtomicLong

/**
 * Logback [Filter] which counts the output size of all messages, and beyond [maxBytes], drops new messages.
 * The counter is restarted every [epochLengthMillis].
 */
@Suppress("unused")
class OutputSizeCappingFilter : Filter<ILoggingEvent>() {
    var maxBytes: Long = 60_000_000
    var epochLengthMillis: Long = 60_000
    private var latestEpochCounter: EpochBytesCounter = EpochBytesCounter(0)

    private inner class EpochBytesCounter(
        val epochIdentifier: Long
    ) {
        private val bytesCounter: AtomicLong = AtomicLong(0)
        private var reported = false

        fun incrementCounter(newBytes: Long): Long {
            return bytesCounter.addAndGet(newBytes)
        }

        fun reportOnce() {
            synchronized(this) {
                if (reported) {
                    return
                }
                reported = true
            }
            context.statusManager
                .add(
                    WarnStatus(
                        "Log size limit reached  - $maxBytes bytes. New messages will not be appended for a while.",
                        this@OutputSizeCappingFilter
                    )
                )
        }
    }

    override fun decide(event: ILoggingEvent): FilterReply {
        val epoch = currentEpoch()

        val newBytes = event.estimateMessageSizeInBytes()

        val totalSize = epoch.incrementCounter(newBytes)
        return if (totalSize > maxBytes) {
            epoch.reportOnce()
            FilterReply.DENY
        } else {
            FilterReply.NEUTRAL
        }
    }

    private fun currentEpoch(): EpochBytesCounter {
        val newEpochIdentifier = System.currentTimeMillis() / epochLengthMillis
        synchronized(this) {
            if (newEpochIdentifier > latestEpochCounter.epochIdentifier) {
                latestEpochCounter = EpochBytesCounter(newEpochIdentifier)
            }
            return latestEpochCounter
        }
    }

    private fun ILoggingEvent.estimateMessageSizeInBytes(): Long {
        var sum: Long = 0

        message?.let { sum += it.length * 2 }
        argumentArray?.let { sum += it.size * 50 }
        loggerName?.let { sum += it.length * 2 }

        var t = throwableProxy
        while (t != null) {
            sum += t.className.length * 2
            t.message?.let { sum += it.length * 2 }
            t.stackTraceElementProxyArray?.let { sum += it.size * 80 }
            t = t.cause
        }

        return sum
    }

    override fun toString(): String {
        return "OutputSizeCappingFilter$name"
    }
}
