package edu.fiu.cs.kdrg.tkrec.classification;

import java.util.Collection;

import edu.fiu.cs.kdrg.tkrec.util.Pair;

public abstract class Classifier<T,L> {
	
	public Classifier() {}
	
	public abstract void build(Collection<Pair<T,L>> trainingData);
	
	public abstract L classify(T data);

}
