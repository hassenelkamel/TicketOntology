package edu.fiu.cs.kdrg.tkrec.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Liang Tang
 * @date Apr 1, 2013 3:50:25 PM
 * @email tangl99@gmail.com
 * MIT license
 */
public class Event  {
	
	static Map<String,Integer> attrIndexes = new HashMap<String,Integer>();
	
	static List<String> attrNames = new ArrayList<String>();
	
	Map<Integer, String> values = new HashMap<Integer,String>();
	
	public Event() {}
	
	public Event(Event another) {
		Set<String> attrNames = another.getAttrNames();
		for (String attrName: attrNames) {
			this.setValue(attrName, another.getStr(attrName));
		}
	}
	
	public Set<String> getAttrNames() {
		Set<String> attrNames = new HashSet<String>();
		for (Integer attrIndex: values.keySet()) {
			attrNames.add(Event.attrNames.get(attrIndex));
		}
		return attrNames;
	}

	
	public double getDouble(String attrName) {
		// TODO Auto-generated method stub
		return Double.parseDouble(getStr(attrName));
	}

	public int getInt(String attrName) {
		// TODO Auto-generated method stub
		return Integer.parseInt(getStr(attrName));
	}

	public String getStr(String attrName) {
		// TODO Auto-generated method stub
		return this.getValue(attrName);
	}

	public String getValue(String attrName) {
		// TODO Auto-generated method stub
		Integer attrIndex = Event.attrIndexes.get(attrName);
		if (attrIndex == null) {
			return null;
		}
		else {
			return values.get(attrIndex);
		}
	}

	public void setValue(String attrName, double value) {
		// TODO Auto-generated method stub
		setValue(attrName, value+"");
	}

	public void setValue(String attrName, int value) {
		// TODO Auto-generated method stub
		setValue(attrName, value+"");
	}

	public void setValue(String attrName, String value) {
		// TODO Auto-generated method stub
		Integer attrIndex = attrIndexes.get(attrName);
		if (attrIndex == null) {
			attrIndex = attrIndexes.keySet().size();
			attrNames.add(attrName);
			attrIndexes.put(attrName, attrIndex);
		}
		values.put(attrIndex, value);
	}
	
	public Event clone() {
		return new Event(this);
	}

	public Event createNew() {
		// TODO Auto-generated method stub
		return new Event();
	}

	public boolean hasAttribute(String attrName) {
		// TODO Auto-generated method stub
		return this.getAttrNames().contains(attrName);
	}

}
