package edu.fiu.cs.kdrg.tkrec.util;


import edu.fiu.cs.kdrg.tkrec.core.Event;

public interface EventInputStreamReader {
	
	Event readNext() throws Exception;
}
