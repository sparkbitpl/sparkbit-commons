package pl.sparkbit.commons.json;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JsonField<T> {

    public abstract <D> D ifPresent(D defResponse, Function<T, D> callback);

    public void ifPresent(Consumer<T> callback) {
        ifPresent(null, v -> {
                callback.accept(v);
                return null;
            }
        );
    }

    public static <T> JsonField<T> absent() {
        //noinspection unchecked
        return (JsonFieldAbsent<T>) JsonFieldAbsent.ABSENT_FIELD;
    }

    public static <T> JsonField<T> wrap(T value) {
        return new JsonFieldPresent<>(value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class JsonFieldPresent<T> extends JsonField<T> {

        private final T value;

        public T getValue() {
            return value;
        }

        @Override
        public <D> D ifPresent(D defResponse, Function<T, D> callback) {
            return callback.apply(value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class JsonFieldAbsent<T> extends JsonField<T> {
        static final JsonFieldAbsent<?> ABSENT_FIELD = new JsonFieldAbsent<>();

        @Override
        public <D> D ifPresent(D defResponse, Function<T, D> callback) {
            return defResponse;
        }
    }
}
