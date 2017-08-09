package pl.sparkbit.commons.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CollectionsUtils {

    private CollectionsUtils() {
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        return list;
    }

    public static <T> Set<T> emptyIfNull(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        }

        return set;
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return map;
    }

}
