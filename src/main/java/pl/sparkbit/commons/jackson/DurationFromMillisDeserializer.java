package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.Duration;

import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;
import static com.fasterxml.jackson.core.JsonTokenId.ID_NUMBER_INT;

@SuppressWarnings("unused")
public class DurationFromMillisDeserializer extends StdScalarDeserializer<Duration> {

    public DurationFromMillisDeserializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        int currentToken = jsonParser.currentTokenId();
        if (currentToken != ID_NUMBER_INT) {
            deserializationContext.reportWrongTokenException(Duration.class, VALUE_NUMBER_INT, "expected integer");
        }
        return Duration.ofMillis(jsonParser.getLongValue());
    }
}
