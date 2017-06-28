package edu.fiu.cs.kdrg.tkrec.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketEvent {

	static Map<String, Integer> attrIndexes = new HashMap<String, Integer>();
	static List<String> attrNames = new ArrayList<String>();
	Map<Integer, String> values = new HashMap<Integer, String>();

	public TicketEvent() {}

	public TicketEvent(TicketEvent another) {
		Set<String> attrNames = another.getAttrNames();
		for (String attrName : attrNames) {
			this.setValue(attrName, another.getStr(attrName));
		}
	}

	public Set<String> getAttrNames() {
		Set<String> attrNames = new HashSet<String>();
		// Iterator<Integer> attrIt = values.keySet().iterator();
		for (Integer attrIndex : values.keySet()) {
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

	public long getLong(String attrName) {
		// TODO Auto-generated method stub
		return Long.parseLong(getStr(attrName));
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
		} else {
			return values.get(attrIndex);
		}
	}

	public void setValue(String attrName, double value) {
		// TODO Auto-generated method stub
		setValue(attrName, value + "");
	}

	public void setValue(String attrName, int value) {
		// TODO Auto-generated method stub
		setValue(attrName, value + "");
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

	public TicketEvent clone() {
		return new TicketEvent(this);
	}

	public TicketEvent createNew() {
		// TODO Auto-generated method stub
		return new TicketEvent();
	}

	public boolean hasAttribute(String attrName) {
		// TODO Auto-generated method stub
		return this.getAttrNames().contains(attrName);
	}

	/*
	 * reg is the regular express to tokenize the sentence
	 */
	public String getTokenizedValue(String attrName, String reg) {

		String rawValue = getStr(attrName);
		if (rawValue == null)
			return null;

		return rawValue.replaceAll(reg, " ");
	}

	// default [a-zA-Z][a-zA-Z0-9]*
	public List<String> toWords(String attr, String reg) {

		String value = this.getStr(attr);
		Matcher matcher = Pattern.compile(reg).matcher(value);
		if (!matcher.matches())
			return null;

		List<String> segments = new ArrayList<String>();
		while (matcher.find()) {
			segments.add(matcher.group());
		}
		return segments;
	}

}
