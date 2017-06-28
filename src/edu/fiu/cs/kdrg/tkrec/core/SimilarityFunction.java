package edu.fiu.cs.kdrg.tkrec.core;

public interface SimilarityFunction<T> {
	
	double sim(T o1, T o2);
	
	double maxValue();
	
	double minValue();
}
