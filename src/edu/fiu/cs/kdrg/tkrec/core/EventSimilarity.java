package edu.fiu.cs.kdrg.tkrec.core;

import java.util.Arrays;

import java.util.HashSet;
import java.util.Set;



public class EventSimilarity implements SimilarityFunction<Event> {
	
	Set<String> ignoreAttrNames = new HashSet<String>();
	
	public EventSimilarity() {
	}

	
	public EventSimilarity(String[] ignoreAttrNames) {
		this.ignoreAttrNames.addAll(Arrays.asList(ignoreAttrNames));
	}


	@Override
	public double sim(Event o1, Event o2) {
		// TODO Auto-generated method stub
		Set<String> attrNames1 = o1.getAttrNames();
		Set<String> attrNames2 = o2.getAttrNames();
		int comValPair = 0;
		int comAttrs = 0;
		int numProhbit = 0;
		for (String attrName : attrNames1) {
			if (ignoreAttrNames.contains(attrName)) {
				numProhbit++;
				continue;
			}
			if (attrNames2.contains(attrName)) {
				if (o1.getValue(attrName).equals(o2.getValue(attrName))) {
					comValPair++;
				}
				comAttrs++;
			}
		}
		for (String attrName: attrNames2) {
			if (ignoreAttrNames.contains(attrName)) {
				numProhbit++;
			}
		}
		double sim = ((double)comValPair)/ (attrNames1.size() + attrNames2.size() - numProhbit - comAttrs);
		return sim;
	}

	@Override
	public double maxValue() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public double minValue() {
		// TODO Auto-generated method stub
		return 0.0;
	}		
}
