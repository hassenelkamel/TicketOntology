package edu.fiu.cs.kdrg.tkrec.util;

import java.util.Collection;
import java.util.List;

import edu.fiu.cs.kdrg.tkrec.core.Event;




public class XMLEventWriter {
	
	public static void write(List<Event> events, String fileName) throws Exception {
		XMLEventOutputStreamWriter output = new XMLEventOutputStreamWriter(fileName);
		for (Event event : events) {
			output.write(event);
		}
		output.close();
	}
	
	public static void write(Collection<List<Event>> eventSets, String fileName) throws Exception {
		XMLEventOutputStreamWriter output = new XMLEventOutputStreamWriter(fileName);
		for (List<Event> events : eventSets) {
			output.write(events);
		}
		output.close();
	}

}
