package edu.fiu.cs.kdrg.tkrec.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CountMap<K> {
	
	private Map<K, Integer> map = new HashMap<K, Integer>();
	
	public CountMap() {
		
	}
	
	public void addAll(Collection<K> c) {
		for (K key: c) {
			add(key);
		}
	}
	
	public void add(K key) {
		add(key, 1);
	}
	
	public void addCount(K key) {
		add(key, 1);
	}
	
	
	public void add(K key, int countToAdd) {
		Integer count = map.get(key);
		if (count == null) {
			count = 0;
		}
		count += countToAdd;
		map.put(key, count);
	}
	
	public void remove(K key) {
		remove(key, 1);
	}
	
	public void remove(K key, int countToRemove) {
		Integer count = map.get(key);
		if (count == null) {
			throw new Error("Not contain this key : "+key);
		}
		count -=countToRemove;
		if (count == 0) {
			map.remove(key);
		}
		else {
			map.put(key, count);
		}
	}
	
	public int get(K key) {
		Integer count = map.get(key);
		if (count == null) {
			return 0;
		}
		else {
			return count;
		}
	}
		
	public int getCount(K key) {
		Integer count = map.get(key);
		if (count == null) {
			return 0;
		}
		else {
			return count;
		}
	}
	
	public int size() {
		return map.size();
	}
	
	public Set<K> keySet() {
		return (Set<K>) map.keySet();
	}
	
	public Set<Map.Entry<K, Integer>> entrySet() {
		return map.entrySet();
	}
	
	public List<Pair<K, Integer>> sortByCount(final boolean bAscendingOrder) {
		List<Pair<K, Integer>> pairs = new ArrayList<Pair<K, Integer>>(); 
		for (K key : map.keySet()) {
			pairs.add(new Pair<K, Integer>(key, getCount(key)));
		}
		Collections.sort(pairs, new Comparator<Pair<K, Integer>>(){
			@Override
			public int compare(Pair<K, Integer> o1, Pair<K, Integer> o2) {
				// TODO Auto-generated method stub
				if (bAscendingOrder) {
					if (o1.getSecond() > o2.getSecond()) {
						return 1;
					}
					else if (o1.getSecond() < o2.getSecond()) {
						return -1;
					}
					else {
						return 0;
					}
				}
				else {
					if (o1.getSecond() > o2.getSecond()) {
						return -1;
					}
					else if (o1.getSecond() < o2.getSecond()) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
		});
		return pairs;
	}

	@Override
	public String toString() {
		return map.toString();
	}
	
	

}
