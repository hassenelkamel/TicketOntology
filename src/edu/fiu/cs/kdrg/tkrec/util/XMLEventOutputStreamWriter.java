package edu.fiu.cs.kdrg.tkrec.util;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import edu.fiu.cs.kdrg.tkrec.core.Event;


public class XMLEventOutputStreamWriter implements EventOutputStreamWriter{
	
	protected XMLWriter out;
	
	protected Element root = null;

	public XMLEventOutputStreamWriter(String fileName) throws SAXException, IOException  {
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding("UTF-8"); 
		out = new XMLWriter(new FileOutputStream(fileName), outputFormat);
		out.startDocument();
		root = DocumentHelper.createElement("data");
		out.writeOpen(root);
	}
	
	@Override
	public void write(Event event) throws Exception {
		// TODO Auto-generated method stub
		Element elem = toXML(event);
		out.write(elem);
	}
	
	public void write(List<Event> events) throws Exception {
		Element elem = toXML(events);
		out.write(elem);
	}
	
	public void close() throws Exception {
		// TODO Auto-generated method stub
		out.writeClose(root);
		out.endDocument();
		out.close();
	}
	
	public static Element toXML(Event event) {
		Element elem = DocumentHelper.createElement("event");
		for (String attrName: event.getAttrNames()) {
			Element attrElem = elem.addElement(attrName);
			String value = event.getValue(attrName);
			if (value == null) {
				throw new Error("Attribute "+attrName+" is null, cannot export to XML");
			}
			attrElem.setText(value);
		}
		return elem;
	}
	
	public static Element toXML(List<Event> events) {
		Element elem = DocumentHelper.createElement("events");
		for (Event event : events) {
			elem.add(toXML(event));
		}
		return elem;		
	}
	
}
