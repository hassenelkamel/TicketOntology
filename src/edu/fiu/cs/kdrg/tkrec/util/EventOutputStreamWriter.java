package edu.fiu.cs.kdrg.tkrec.util;

import edu.fiu.cs.kdrg.tkrec.core.Event;

public interface EventOutputStreamWriter {
	
	public void write(Event event) throws Exception;
}
