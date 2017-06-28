package edu.fiu.cs.kdrg.tkrec.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.core.Event;



public class XMLEventLoader {
	
//	public static VectorEvents loadVectorEvents(String xmlFileName) throws DocumentException {
//		
//		// Read all attribute names in the first pass
//		SAXReader reader = new SAXReader();
//		EventAttributeReaderHandler attrHandler = new EventAttributeReaderHandler();
//		reader.addHandler("/data/event", attrHandler);
//		reader.read(xmlFileName);
//		Set<String> attrSet = attrHandler.attrSet;
//		String[] attrNames = new String[attrSet.size()];
//		attrSet.toArray(attrNames);
//		DataType[] attrTypes = new DataType[attrNames.length];
//		Arrays.fill(attrTypes, DataType.STR);
//		
//		// Read all events in the second pass
//		VectorEvents events = new VectorEvents(attrNames, attrTypes);
//		reader = new SAXReader();
//		EventReaderHandler eventHandler = new EventReaderHandler(events);
//		reader.addHandler("/data/event", eventHandler);
//		reader.read(xmlFileName);
//		
//		return events;
//	}
	
	public static List<Event> loadEvents(String xmlFileName) throws IOException, XMLStreamException, DocumentException  {
		return loadEvents(xmlFileName, 0);
	}
	
	public static List<Event> loadEvents(String xmlFileName, int numEvents) throws IOException, XMLStreamException, DocumentException  {
		List<Event> events = new ArrayList<Event>();
		XMLEventInputStreamReader reader = new XMLEventInputStreamReader(xmlFileName);
		Event event = null;
		while((event = reader.readNext()) != null ) {			
			events.add(event);
			if (numEvents > 0 && events.size() >= numEvents) {
				break;
			}
		}
		reader.close();
		return events;
	}
	
//	static class EventAttributeReaderHandler implements ElementHandler {
//		public Set<String> attrSet;
//
//		public EventAttributeReaderHandler() {
//			this.attrSet = new HashSet<String>();
//		}
//
//		@Override
//		public void onEnd(ElementPath path) {
//			// TODO Auto-generated method stub
//			// process a ROW element
//			Element row = path.getCurrent();
//			List<Element> attrElems = row.elements();
//			for (Element attrElem : attrElems) {
//				this.attrSet.add(attrElem.getName());
//			}
//			row.detach();
//		}
//		
//		@Override
//		public void onStart(ElementPath path) {
//			// TODO Auto-generated method stub
//		}
//	}
//	
//	static class EventReaderHandler implements ElementHandler {
//	    public VectorEvents events;
//
//		public EventReaderHandler(VectorEvents events) {
//			this.events = events;
//		}
//
//		@Override
//		public void onEnd(ElementPath path) {
//			// TODO Auto-generated method stub
//			// process a ROW element
//			Element row = path.getCurrent();
//			List<Element> attrElems = row.elements();
//			Event event = events.createEvent();
//			for (Element attrElem : attrElems) {
//				event.setValue(attrElem.getName(), attrElem.getTextTrim());
//			}
//			this.events.addEvent(event);
//			row.detach();
//		}
//		
//		@Override
//		public void onStart(ElementPath path) {
//			// TODO Auto-generated method stub
//		}
//	}
}
