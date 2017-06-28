package edu.fiu.cs.kdrg.tkrec.core;

import java.util.Set;

public class JaccardSimilarity<T> implements SimilarityFunction<Set<T>> {
	
	@Override
	public double sim(Set<T> o1, Set<T> o2) {
		// TODO Auto-generated method stub
		int common = 0;
		for (T o : o1) {
			if (o2.contains(o)) {
				common++;
			}
		}
		return ((double)(common))/((double)(o1.size()+o2.size() - common));		
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
