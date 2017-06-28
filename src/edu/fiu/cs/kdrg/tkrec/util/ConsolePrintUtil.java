package edu.fiu.cs.kdrg.tkrec.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsolePrintUtil {
	
	public static void printMap(Map<?,?> map) {
		Iterator<?> iter = map.keySet().iterator();
		
		while(iter.hasNext()) {
			Object key = iter.next();
			Object value = map.get(key);
			System.out.println("Key: " + key + " Value: " + value);
		}
	}
	
	public static void printList(List<?> list) {
		for(Object element: list) {
			System.out.println(element);
		}
	}
	
	public static void printSet(Set<?> set) {
		Iterator<?> iter = set.iterator();
		
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
}
