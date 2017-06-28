package edu.fiu.cs.kdrg.tkrec.core;

import java.util.ArrayList;
import java.util.List;

public class TaxConcept extends Concept {

	private ArrayList<Event> events = new ArrayList<Event>();

	private int level = -1;

	/**
	 * @return
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * @param events
	 */
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public int getDataSize() {
		return this.events.size();
	}

	public void addEvent(Event e) {
		events.add(e);
	}

}
