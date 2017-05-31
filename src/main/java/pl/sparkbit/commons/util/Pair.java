package pl.sparkbit.commons.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public final class Pair<A, B> {

    private final A first;
    private final B second;
}
