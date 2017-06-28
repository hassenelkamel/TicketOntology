package edu.fiu.cs.kdrg.tkrec.automaton.test;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.util.IOUtil;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventInputStreamReader;

public class AutomatonResolutonStatistics {
	public final static String AUTOAMTON_EVENT_FILENAME = "data2/automaton/raw_automaton.xml";
	public final static String AUTOMATON_RESOLUTION_STATISTICS_FILENAME = "data2/automaton/resolution_statistic_automaton";

	public HashMap<String, Integer> resolutionStatistics(String filename) {
		HashMap<String, Integer> statisticsMap = new HashMap<String, Integer>();
		try {
			XMLEventInputStreamReader reader = new XMLEventInputStreamReader(filename);
			String resolution = "";
			Event event = null;

			while ((event = reader.readNext()) != null) {
				resolution = event.getValue("RESOLUTION");
				if (resolution == null || resolution.equals(""))
					continue;

				resolution = resolution.toLowerCase();
				if (!statisticsMap.containsKey(resolution)) {
					statisticsMap.put(resolution, 1);
				} else {
					statisticsMap.put(resolution, statisticsMap.get(resolution) + 1);
				}

			}
		} catch (IOException | XMLStreamException | DocumentException e) {
			e.printStackTrace();
		}
		return statisticsMap;
	}
	
	// a comparator that compares Strings
	static class ValueComparator implements Comparator<String>{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		public ValueComparator(HashMap<String, Integer> map){
			this.map.putAll(map);
		}
	 
		@Override
		public int compare(String s1, String s2) {
			if(map.get(s1) >= map.get(s2)){
				return -1;
			}else{
				return 1;
			}	
		}
	}
	
	public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
		Comparator<String> comparator = new ValueComparator(map);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		map.remove(null);
		result.putAll(map);
		return result;
	}
	
	public static void main(String[] args) {
		AutomatonResolutonStatistics stat = new AutomatonResolutonStatistics();
		HashMap<String, Integer> map = stat.resolutionStatistics(AUTOAMTON_EVENT_FILENAME);
		TreeMap<String, Integer> sortedMap = sortMapByValue(map);
//		for(Map.Entry<String,Integer> entry : sortedMap.entrySet()) {
//			  String key = entry.getKey();
//			  Integer value = entry.getValue();
//
//			  System.out.println(key + " => " + value);
//		}
		IOUtil.writeTreeMap2File(sortedMap, AUTOMATON_RESOLUTION_STATISTICS_FILENAME);
	}
}
