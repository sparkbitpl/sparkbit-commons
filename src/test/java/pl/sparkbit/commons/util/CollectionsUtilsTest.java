package pl.sparkbit.commons.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsUtilsTest {

    @Test
    public void shouldReturnEmptyList() {
        List<String> inputList = null;

        List<String> returnedList = CollectionsUtils.emptyIfNull(inputList);

        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    @Test
    public void shouldReturnInputList() {
        List<String> inputList = new ArrayList<>();
        inputList.add("Test");

        List<String> returnedList = CollectionsUtils.emptyIfNull(inputList);

        assertNotNull(returnedList);
        assertEquals(inputList, returnedList);
    }

    @Test
    public void shouldReturnEmptySet() {
        Set<String> inputSet = null;

        Set<String> returnedSet = CollectionsUtils.emptyIfNull(inputSet);

        assertNotNull(returnedSet);
        assertTrue(returnedSet.isEmpty());
    }

    @Test
    public void shouldReturnInputSet() {
        Set<String> inputSet = new HashSet<>();
        inputSet.add("Test");

        Set<String> returnedSet = CollectionsUtils.emptyIfNull(inputSet);

        assertNotNull(returnedSet);
        assertEquals(inputSet, returnedSet);
    }

    @Test
    public void shouldReturnEmptyMap() {
        Map<String, Boolean> inputMap = null;

        Map<String, Boolean> returnedMap = CollectionsUtils.emptyIfNull(inputMap);

        assertNotNull(returnedMap);
        assertTrue(returnedMap.isEmpty());
    }

    @Test
    public void shouldReturnInputMap() {
        Map<String, Boolean> inputMap = new HashMap<>();
        inputMap.put("Test", true);

        Map<String, Boolean> returnedMap = CollectionsUtils.emptyIfNull(inputMap);

        assertNotNull(returnedMap);
        assertEquals(inputMap, returnedMap);
    }

}
