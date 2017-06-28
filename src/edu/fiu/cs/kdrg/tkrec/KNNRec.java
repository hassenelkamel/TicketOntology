package edu.fiu.cs.kdrg.tkrec;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.fiu.cs.kdrg.tkrec.classification.KNN;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.core.EventSimilarity;
import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.core.TextTermSimilarity;
import edu.fiu.cs.kdrg.tkrec.util.Pair;

public class KNNRec implements Recommender{
	
	KNN<Event, String> knn;
	
	String labelName;
	
	Set<String> prohitAttrNames = new HashSet<String>();
	
	SimilarityFunction<String> labelSimFunc = new TextTermSimilarity();
	
	public final static String IDENTIFIER = "KNN";
	
	public KNNRec(List<Event> trainEvents, String labelName, String[] prohitAttrNames, int k) {
		this.labelName = labelName;
		this.prohitAttrNames.addAll(Arrays.asList(prohitAttrNames));
		knn = new KNN<Event, String>(new EventSimilarity(prohitAttrNames), k, labelSimFunc, 0.5);
		List<Pair<Event, String>> trainData = new ArrayList<Pair<Event, String>>(trainEvents.size());
		for (Event event: trainEvents) {
			Pair<Event, String>  labelEvent = new Pair<Event,String>(event, event.getValue(labelName));
			trainData.add(labelEvent);
		}
		knn.build(trainData);
	}
		
	public void addLabeledEvent(Event event) {
		Pair<Event, String>  labelEvent = new Pair<Event,String>(event, event.getValue(labelName));
		knn.addLabel(labelEvent);
	}
	
	public List<String> recommend(Event event, int topK) {		
		return knn.classify(event, topK);
	}
	
	public String getIdentifier() {
		return IDENTIFIER;
	}

}
