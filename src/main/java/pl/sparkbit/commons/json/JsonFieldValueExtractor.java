package pl.sparkbit.commons.json;


import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

/**
 * This class cannot be written in Kotlin (cannot annotate wildcard type:
 * https://stackoverflow.com/questions/61380000/annotations-on-wildcard-type-parameters-in-kotlin)
 */
public class JsonFieldValueExtractor implements ValueExtractor<JsonField<@ExtractedValue ?>> {

    @Override
    public void extractValues(JsonField<?> originalValue, ValueReceiver receiver) {
        if (originalValue instanceof JsonField.JsonFieldPresent) {
            receiver.value(null, ((JsonField.JsonFieldPresent<?>) originalValue).getValue());
        }
    }
}
