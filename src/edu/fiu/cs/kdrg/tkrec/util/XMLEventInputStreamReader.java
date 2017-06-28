package edu.fiu.cs.kdrg.tkrec.util;


import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;


import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.STAXEventReader;

import edu.fiu.cs.kdrg.tkrec.core.Event;




public class XMLEventInputStreamReader implements EventInputStreamReader{
	XMLEventReader  reader;
	STAXEventReader  staxReader;

	public XMLEventInputStreamReader(String fileName) throws IOException, XMLStreamException, DocumentException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		// Reader fileReader = new FileReader(fileName);
		reader = factory.createXMLEventReader(new FileInputStream(fileName), "UTF-8");
		StartDocument event = (StartDocument) reader.nextEvent();
		while(reader.peek().isStartElement() == false) {
			reader.nextEvent();
		}
		reader.nextEvent();
		staxReader = new STAXEventReader();
	}
	
	public Element nextElem() throws XMLStreamException {
_error_loop:
		while(true) {
			if (reader.hasNext() == false) {
				return null;
			}
			XMLEvent xmlEvent = reader.peek();
			while (xmlEvent.isCharacters()) {
				reader.nextEvent();
				xmlEvent = reader.peek();
			}
			String elemName;
			if (xmlEvent.isEndElement()) {
				elemName = xmlEvent.asEndElement().getName().toString();
				if (elemName.equals("data")) {
					return null;
				}
			}
			
			if (xmlEvent.isStartElement() == false){
				reader.nextEvent();
				continue _error_loop;
				// throw new Error("Error in XML file : "+xmlEvent.getLocation());
			}
			elemName = xmlEvent.asStartElement().getName().toString();
			if (elemName.equals("event") == false) {
				// throw new Error("Error in XML file : "+xmlEvent.getLocation());
				reader.nextEvent();
				continue _error_loop;
			}
			Element eventElem = null;
			elemName = null;
			
			while(reader.hasNext()) {
				try {
					xmlEvent = reader.peek();
					if (xmlEvent.isStartElement()) {
						eventElem = staxReader.readElement(reader);
						break;
					}
					else if (xmlEvent.isEndDocument()) {
						reader.nextEvent();
						break;
					}
					else {
						reader.nextEvent();
					}
				}catch(Exception e) {
					e.printStackTrace();
					continue _error_loop;
				}
			}
			if (eventElem == null) {
				return null;
			}
			else {
				return eventElem;
			}
		}
	}

	
	@Override
	public Event readNext() throws XMLStreamException {		
		Element eventElem = nextElem();
		if (eventElem == null) {
			return null;
		}
		else {
			return createEvent(eventElem);
		}
	}
	
	public static Event createEvent(Element elem) {
		Event event = new Event();
		List<Element> attrElems = elem.elements();
		for (Element attrElem : attrElems) {
			event.setValue(attrElem.getName().toString(), attrElem.getTextTrim());
		}
		return event;
	}
	
	public void close() throws XMLStreamException {
		reader.close();
	}
	
}
