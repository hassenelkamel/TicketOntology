package edu.fiu.cs.kdrg.tkrec;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


import edu.fiu.cs.kdrg.tkrec.classification.WeightedKNN;
import edu.fiu.cs.kdrg.tkrec.core.CostFunction;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.core.EventSimilarity;
import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.core.TextTermSimilarity;
import edu.fiu.cs.kdrg.tkrec.util.Pair;


public class FusionKNNRec implements Recommender {
	
	WeightedKNN<Event, Boolean> alertTypeKNN;
	
	WeightedKNN<Event, String> eventLabelKNN;
		
	String labelAttrName;
	
	String alertTypeAttrName;
	
	Set<String> prohitAttrNames = new HashSet<String>();
	
	SimilarityFunction<String> labelSimFunc = new TextTermSimilarity();
	
	double realAlertImportance;
	
	double falseAlertProb;
	
	public final static String IDENTIFIER = "FusionKNN";
	
	public FusionKNNRec(List<Event> trainEvents, String labelAttrName, String alertTypeAttrName, 
			String[] prohitAttrNames, int k, double realImportance) {
		this.labelAttrName = labelAttrName;
		this.alertTypeAttrName = alertTypeAttrName;
		this.prohitAttrNames.addAll(Arrays.asList(prohitAttrNames));
		this.realAlertImportance = realImportance;
		
		// trainEvents = filterRareLabels(trainEvents);
		
		alertTypeKNN = new WeightedKNN<Event, Boolean>(new EventSimilarity(prohitAttrNames), k);
		eventLabelKNN = new WeightedKNN<Event, String>(new EventSimilarity(prohitAttrNames), k, labelSimFunc, 0.5, 
				new ExpectedCostFunction());
		
		
		List<Pair<Event, Boolean>> eventWithTypes = new ArrayList<Pair<Event, Boolean>>(trainEvents.size());
		List<Pair<Event, String>> eventWithLabels = new ArrayList<Pair<Event,String>>();
		for (Event event: trainEvents) {
			boolean isFalseTicket = event.getValue(alertTypeAttrName).equals("1");
			eventWithTypes.add(new Pair<Event,Boolean>(event, isFalseTicket));			
			eventWithLabels.add(new Pair<Event, String>(event, event.getValue(labelAttrName)));
		}
		alertTypeKNN.build(eventWithTypes);
		eventLabelKNN.build(eventWithLabels);
	}

	@Override
	public void addLabeledEvent(Event event) {
		// TODO Auto-generated method stub
		boolean isFalseTicket = event.getValue(alertTypeAttrName).equals("1");
		alertTypeKNN.addLabel(new Pair<Event,Boolean>(event, isFalseTicket));
		eventLabelKNN.addLabel(new Pair<Event, String>(event, event.getValue(labelAttrName)));
	}

	@Override
	public List<String> recommend(Event event, int topK) {
		// TODO Auto-generated method stub
		this.falseAlertProb = getFalseTicketProbability(event);
		return eventLabelKNN.classify(event, topK);
	}
	
	private double getFalseTicketProbability(Event event) {
		List<Pair<Boolean, Double>> alertTypeScores = alertTypeKNN.classifyWithScore(event, 2);
		double falseScore = 0;
		double totalScore = 0;
		for (Pair<Boolean, Double> pair: alertTypeScores) {
			if (pair.getFirst()) { // false alert probability
				falseScore = pair.getSecond();
			}
			totalScore += pair.getSecond();
		}
		return falseScore / totalScore;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return IDENTIFIER;
	}
	
	
	class ExpectedCostFunction implements CostFunction<Event> {
		@Override
		public double cost(Event e) {
			// TODO Auto-generated method stub
			double queryRealTicketProb = 1 - falseAlertProb;
			double queryFalseTicketProb = falseAlertProb;
			if (e.getValue(alertTypeAttrName).equals("1")) { // e is a false ticket
				return 1.0 - queryRealTicketProb * realAlertImportance;
			}
			else { // e is a real ticket
				return 1.0 - queryFalseTicketProb * (1-realAlertImportance);
			}
		}
		
	}

}
