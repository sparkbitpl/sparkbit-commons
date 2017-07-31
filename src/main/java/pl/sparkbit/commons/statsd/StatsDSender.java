package pl.sparkbit.commons.statsd;

import com.timgroup.statsd.StatsDClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsDSender {

    private final StatsDClient client;

    void addToCounter(String counter, long value) {
        log.trace("Set counter {} to {}", counter, value);
        client.count(counter, value);
    }

    void incrementCounter(String counter) {
        log.trace("Incrementing counter {}", counter);
        client.incrementCounter(counter);
    }

    void setGauge(String gauge, long value) {
        log.trace("Set gauge {} to {}", gauge, value);
        client.recordGaugeValue(gauge, value);
    }

    void incrementGauge(String gauge) {
        log.trace("Increment gauge {}", gauge);
        client.recordGaugeDelta(gauge, 1L);
    }

    void decrementGauge(String gauge) {
        log.trace("Decrement gauge {}", gauge);
        client.recordGaugeDelta(gauge, -1L);
    }

    void recordEventDuration(String event, Duration duration) {
        log.trace("Recording duration {}: {}", event, duration);
        client.recordExecutionTime(event, duration.toMillis());
    }
}
