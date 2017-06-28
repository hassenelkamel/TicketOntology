package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.*;

public class MapValueSortComparatorUtil {
   
	/**
	 * Sorted the statistics result by total appearance
	 * @param statisticTaggedPhrasesMap
	 * @return
	 */
	public static Map<String, Integer> outputSortedMap(Map<String, Integer> statisticTaggedPhrasesMap) {
        ValueComparator bvc = new ValueComparator(statisticTaggedPhrasesMap);
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);

        sortedMap.putAll(statisticTaggedPhrasesMap);
        return sortedMap;
    }
}

class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}