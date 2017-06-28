package edu.fiu.cs.kdrg.tkrec;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import edu.fiu.cs.kdrg.tkrec.classification.WeightedKNN;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.core.EventSimilarity;
import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.core.TextTermSimilarity;
import edu.fiu.cs.kdrg.tkrec.util.Pair;


public class DivideKNNRec implements Recommender {
	
	WeightedKNN<Event, Boolean> alertTypeKNN;
	
	WeightedKNN<Event, String> realEventKNN;
	
	WeightedKNN<Event, String> falseEventKNN;
	
	String labelAttrName;
	
	String alertTypeAttrName;
	
	Set<String> prohitAttrNames = new HashSet<String>();
	
	SimilarityFunction<String> labelSimFunc = new TextTermSimilarity();
	
	double realImportance;
	
	public final static String IDENTIFIER = "DivideKNN";
	
	public DivideKNNRec(List<Event> trainEvents, String labelAttrName, String alertTypeAttrName, 
			String[] prohitAttrNames, int k, double realImportance) {
		this.labelAttrName = labelAttrName;
		this.alertTypeAttrName = alertTypeAttrName;
		this.prohitAttrNames.addAll(Arrays.asList(prohitAttrNames));
		this.realImportance = realImportance;
		
		alertTypeKNN = new WeightedKNN<Event, Boolean>(new EventSimilarity(prohitAttrNames), k);
		realEventKNN = new WeightedKNN<Event, String>(new EventSimilarity(prohitAttrNames), k, labelSimFunc, 0.5);
		falseEventKNN = new WeightedKNN<Event, String>(new EventSimilarity(prohitAttrNames), k, labelSimFunc, 0.5);
		List<Pair<Event, Boolean>> trainData = new ArrayList<Pair<Event, Boolean>>(trainEvents.size());
		List<Pair<Event, String>> realAlertData = new ArrayList<Pair<Event,String>>();
		List<Pair<Event, String>> falseAlertData = new ArrayList<Pair<Event,String>>();
		for (Event event: trainEvents) {
			boolean isFalseTicket = event.getValue(alertTypeAttrName).equals("1");
			Pair<Event, Boolean>  labelEvent = new Pair<Event,Boolean>(event, isFalseTicket);
			trainData.add(labelEvent);
			if (isFalseTicket) {
				falseAlertData.add(new Pair<Event, String>(event, event.getValue(labelAttrName)));
			}
			else {
				realAlertData.add(new Pair<Event,String>(event, event.getValue(labelAttrName)));
			}
		}
		alertTypeKNN.build(trainData);
		realEventKNN.build(realAlertData);
		falseEventKNN.build(falseAlertData);
	}

	@Override
	public void addLabeledEvent(Event event) {
		// TODO Auto-generated method stub
		boolean isFalseTicket = event.getValue(alertTypeAttrName).equals("1");
		Pair<Event, Boolean>  labelEvent = new Pair<Event,Boolean>(event, isFalseTicket);
		alertTypeKNN.addLabel(labelEvent);
		if (isFalseTicket) {
			falseEventKNN.addLabel(new Pair<Event, String>(event, event.getValue(labelAttrName)));
		}
		else {
			realEventKNN.addLabel(new Pair<Event,String>(event, event.getValue(labelAttrName)));
		}
	}
	
	@Override
	public List<String> recommend(Event event, int topK) {
		// TODO Auto-generated method stub
		double falseProb = getFalseTicketProbability(event);
		boolean isFalse = falseProb > 0.5;
		if (isFalse) {
			return falseEventKNN.classify(event, topK);
		}
		else {
			return realEventKNN.classify(event, topK);
		}
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
	
	
}
