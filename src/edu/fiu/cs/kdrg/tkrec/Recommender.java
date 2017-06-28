package edu.fiu.cs.kdrg.tkrec;

import java.util.List;

import edu.fiu.cs.kdrg.tkrec.core.Event;


public interface Recommender {
	
	void addLabeledEvent(Event event);
	
	List<String> recommend(Event event, int topK);
	
	String getIdentifier();
}
