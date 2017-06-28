package edu.fiu.cs.kdrg.tkrec.classification;

import java.util.ArrayList;


import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.util.CountMap;
import edu.fiu.cs.kdrg.tkrec.util.Pair;


public class KNN<T,L> extends Classifier<T,L>{
	
	protected int k = 5;
	
	protected List<Pair<T,L>> labeledInsts = null;
	
	protected SimilarityFunction<T> simFunc = null;
	
	protected SimilarityFunction<L> labelSimFunc = null;
	
	protected double labelRedundantSim;
	
	public KNN(SimilarityFunction<T> simFunc) {
		this.simFunc = simFunc;
	}
	
	public KNN(SimilarityFunction<T> simFunc, int k) {
		this(simFunc);
		this.k = k;
	}
	
	public KNN(SimilarityFunction<T> simFunc, int k, SimilarityFunction<L> labelSimFunc, double maxLabelSim) {
		this(simFunc);
		this.k = k;
		this.simFunc = simFunc;
		this.labelSimFunc = labelSimFunc;
		this.labelRedundantSim = maxLabelSim;
	} 

	@Override
	public void build(Collection<Pair<T, L>> trainingData) {
		// TODO Auto-generated method stub
		this.labeledInsts = new ArrayList<Pair<T,L>>();
		for (Pair<T,L> inst : trainingData) {
			this.labeledInsts.add(inst);
		}
	}
	
	public void addLabel(Pair<T,L> inst) {
		this.labeledInsts.add(inst);
	}
	
	public List<L> classify(T data, int numCandidates) {
		List<Pair<L,Integer>> resultPairs = classifyWithScore(data, numCandidates);
		List<L> results = new ArrayList<L>();
		for (Pair<L,Integer> pair : resultPairs) {
			results.add(pair.getFirst());
		}
		return results;
	}
	
	public List<Pair<L,Integer>> classifyWithScore(T data, int numCandidates) {
		if (numCandidates == 0) {
			return new ArrayList<Pair<L,Integer>>(1);
		}
		// Find k nearest neighbors
		PriorityQueue<Pair<Double,Integer>> topKSimScores = new PriorityQueue<Pair<Double,Integer>>(
				this.labeledInsts.size(), new Comparator<Pair<Double,Integer>>() {
			@Override
			public int compare(Pair<Double,Integer> o1, Pair<Double,Integer> o2) {
				// TODO Auto-generated method stub
				if (o1.getFirst() >  o2.getFirst()) {
					return 1;
				}
				else if (o1.getFirst() < o2.getFirst()) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		
		for (int instIndex = 0; instIndex < this.labeledInsts.size(); instIndex++) {
			Pair<T, L> labeledInst = this.labeledInsts.get(instIndex);
			double simScore = this.simFunc.sim(labeledInst.getFirst(), data);
			topKSimScores.add(new Pair<Double, Integer>(simScore, instIndex));
			if (topKSimScores.size() > k) {
				topKSimScores.poll();
			}
		}

		CountMap<L> labelCounts = new CountMap<L>();
		for (Pair<Double, Integer> simInst: topKSimScores) {
			int instIndex = simInst.getSecond();
			labelCounts.addCount(labeledInsts.get(instIndex).getSecond());
		}
		
		PriorityQueue<Pair<L,Integer>> topLabels = new PriorityQueue<Pair<L, Integer>>(numCandidates, 
				new Comparator<Pair<L, Integer>>() {
					@Override
					public int compare(Pair<L, Integer> o1, Pair<L, Integer> o2) {
						// TODO Auto-generated method stub
						if (o1.getSecond() > o2.getSecond()) {
							return 1;
						}
						else if (o1.getSecond() < o2.getSecond()) {
							return -1;
						}
						else {
							return 0;
						}
					}
		});
		
		
		for (L label : labelCounts.keySet()) {			
			// Check redundancy of the results
			if (labelSimFunc != null) {
				boolean isRedudant = false;
				for (Pair<L,Integer> topLabel : topLabels) {
					if (labelSimFunc.sim(label, topLabel.getFirst()) >= this.labelRedundantSim) {
						isRedudant = true;
						break;
					}
				}
				if (isRedudant) {
					continue;
				}
			}
			
			topLabels.add(new Pair<L, Integer>(label, labelCounts.getCount(label)));
			if (topLabels.size() >  numCandidates) {
				topLabels.poll();
			}
		}
		
		int actualNumResults = Math.min(numCandidates, topLabels.size());
		ArrayList<Pair<L,Integer>> candidateLabels = new ArrayList<Pair<L,Integer>>(actualNumResults);
		for (int i=0; i<actualNumResults; i++) {
			Pair<L, Integer> label = topLabels.peek();
			candidateLabels.add(label);
		}
		for (int i=0; i<actualNumResults; i++) {
			Pair<L, Integer> label = topLabels.poll();
			candidateLabels.set(actualNumResults-i-1, label);
		}
		return candidateLabels;
	}

	@Override
	public L classify(T data) {
		return classify(data, 1).get(0);
	}
	
	

}
