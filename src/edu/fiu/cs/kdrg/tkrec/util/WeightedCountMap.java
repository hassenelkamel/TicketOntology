package edu.fiu.cs.kdrg.tkrec.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

public class WeightedCountMap<K> {
	
	private Map<K, Double> map = new HashMap<K, Double>();
	
	public WeightedCountMap() {
		
	}
	
	public void addAll(Map<K, Double> c) {
		for (K key: c.keySet()) {
			add(key, c.get(key));
		}
	}
	
	public void add(K key, double weight) {
		Double weightedCount = map.get(key);
		if (weightedCount == null) {
			weightedCount = 0.0;
		}
		weightedCount += weight;
		map.put(key, weightedCount);
	}
	
	public void remove(K key, double weight) {
		Double weightedCount = map.get(key);
		if (weightedCount == null) {
			throw new Error("Not contain this key : "+key);
		}
		weightedCount -=weight;
		if (weightedCount <= 0) {
			map.remove(key);
		}
		else {
			map.put(key, weightedCount);
		}
	}
		
	public double getWeight(K key) {
		Double weight = map.get(key);
		if (weight == null) {
			return 0;
		}
		else {
			return weight;
		}
	}
	
	public void setWeight(K key, double weight) {
		map.put(key, weight);
	}
	
	public int size() {
		return map.size();
	}
	
	public Set<K> keySet() {
		return (Set<K>) map.keySet();
	}

	@Override
	public String toString() {
		return map.toString();
	}
	
	

}
