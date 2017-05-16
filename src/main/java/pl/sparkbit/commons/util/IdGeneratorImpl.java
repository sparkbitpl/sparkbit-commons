package pl.sparkbit.commons.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {

    private final TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

    @Override
    public String generate() {
        UUID uuid = generator.generate();
        return toOrderedString(uuid);
    }

    private String toOrderedString(UUID uuid) {
        String s = uuid.toString();
        // After https://www.percona.com/blog/2014/12/19/store-uuid-optimized-way/
        return s.substring(14, 18) + s.substring(9, 13) + s.substring(0, 8) + s.substring(19, 23) + s.substring(24);
    }
}
