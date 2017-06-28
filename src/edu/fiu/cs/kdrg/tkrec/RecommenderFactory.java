package edu.fiu.cs.kdrg.tkrec;

import java.util.List;

import edu.fiu.cs.kdrg.tkrec.core.Event;

public class RecommenderFactory {
	
//	public static String labelAttrName = "SOLUTION_DESC";
	public static String labelAttrName = "RESOLUTION";
	
	public static String alertTypeAttrName = "IS_FALSE";
	
	public static String[] prohitAttrNames = {"RESOLVER_GROUP_CD","SOLUTION_DESC","IS_FALSE", "TicketID"};
	
	public static Recommender create(String algorithmDesc, List<Event> trainEvents, int k) {
		String[] algorithmTerms = algorithmDesc.split(":");
		String algorithmName = algorithmTerms[0];
		String parameterStr = null;
		if (algorithmTerms.length >= 2) {
			parameterStr = algorithmTerms[1];
		}
		
		if (algorithmName.equalsIgnoreCase(KNNRec.IDENTIFIER)) {
			return new KNNRec(trainEvents,labelAttrName, prohitAttrNames, k);
		}
		else if (algorithmName.equalsIgnoreCase(WeightedKNNRec.IDENTIFIER)) {
			return new WeightedKNNRec(trainEvents,labelAttrName, prohitAttrNames, k);
		}
		else if (algorithmName.equalsIgnoreCase(DivideKNNRec.IDENTIFIER)) {
			double realAlertImportance = Double.parseDouble(parameterStr);
			return new DivideKNNRec(trainEvents,labelAttrName, alertTypeAttrName, prohitAttrNames, k, 
					realAlertImportance);
		}
		else if (algorithmName.equalsIgnoreCase(FusionKNNRec.IDENTIFIER)) {
			double realAlertImportance = Double.parseDouble(parameterStr);
			return new FusionKNNRec(trainEvents,labelAttrName, alertTypeAttrName, prohitAttrNames, k, 
					realAlertImportance);
		}
		else {
			throw new Error("Unknown algorithm name : "+algorithmName);
		}
	}

}
