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

import edu.fiu.cs.kdrg.tkrec.core.CostFunction;
import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.util.Pair;
import edu.fiu.cs.kdrg.tkrec.util.WeightedCountMap;



public class WeightedKNN<T,L> extends Classifier<T,L>{
	
	protected int k = 5;
	
	protected List<Pair<T,L>> labeledInsts = null;
	
	protected SimilarityFunction<T> simFunc = null;
	
	protected SimilarityFunction<L> labelSimFunc = null;
	
	protected CostFunction<T> costFunc = null;
	
	protected double labelRedundantSimThreshold;
	
	public WeightedKNN(SimilarityFunction<T> simFunc) {
		this.simFunc = simFunc;
	}
	
	public WeightedKNN(SimilarityFunction<T> simFunc, int k) {
		this(simFunc);
		this.k = k;
	}
	
	public WeightedKNN(SimilarityFunction<T> simFunc, int k, SimilarityFunction<L> labelSimFunc, double maxLabelSim) {
		this(simFunc);
		this.k = k;
		this.simFunc = simFunc;
		this.labelSimFunc = labelSimFunc;
		this.labelRedundantSimThreshold = maxLabelSim;
	} 
	
	public WeightedKNN(SimilarityFunction<T> simFunc, int k, SimilarityFunction<L> labelSimFunc, double maxLabelSim, CostFunction<T> costFunc) {
		this(simFunc);
		this.k = k;
		this.simFunc = simFunc;
		this.labelSimFunc = labelSimFunc;
		this.labelRedundantSimThreshold = maxLabelSim;
		this.costFunc = costFunc;
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
		List<Pair<L,Double>> resultPairs = classifyWithScore(data, numCandidates);
		List<L> results = new ArrayList<L>();
		for (Pair<L,Double> pair : resultPairs) {
			results.add(pair.getFirst());
		}
		return results;
	}
	
	
	
	public List<Pair<L,Double>> classifyWithScore(T data, int numCandidates) {
		if (numCandidates == 0) {
			return new ArrayList<Pair<L,Double>>(1);
		}
		// Find k nearest neighbors
		PriorityQueue<Pair<Double,Integer>> topKSimScores = findKNearest(data);
		
		// Normalize the weights
		WeightedCountMap<L> labelWeights = new WeightedCountMap<L>();
		double totalSumWeight = 0;
		for (Pair<Double, Integer> simInst: topKSimScores) {
			int instIndex = simInst.getSecond();
			labelWeights.add(labeledInsts.get(instIndex).getSecond(),  simInst.getFirst());
			totalSumWeight += simInst.getFirst();
		}
		for (L label: labelWeights.keySet()) {
			labelWeights.setWeight(label, labelWeights.getWeight(label)/totalSumWeight);
		}
		
		PriorityQueue<Pair<L,Double>> topLabels = new PriorityQueue<Pair<L, Double>>(numCandidates, 
				new Comparator<Pair<L, Double>>() {
					@Override
					public int compare(Pair<L, Double> o1, Pair<L, Double> o2) {
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
		
		
		for (L label : labelWeights.keySet()) {			
			// Check redundancy of the results
			if (labelSimFunc != null) {
				boolean isRedudant = false;
				for (Pair<L,Double> topLabel : topLabels) {
					if (labelSimFunc.sim(label, topLabel.getFirst()) >= this.labelRedundantSimThreshold) {
						isRedudant = true;
						break;
					}
				}
				if (isRedudant) {
					continue;
				}
			}
			
			topLabels.add(new Pair<L, Double>(label, labelWeights.getWeight(label)));
			if (topLabels.size() >  numCandidates) {
				topLabels.poll();
			}
		}
		
		int actualNumResults = Math.min(numCandidates, topLabels.size());
		ArrayList<Pair<L,Double>> candidateLabels = new ArrayList<Pair<L,Double>>(actualNumResults);
		for (int i=0; i<actualNumResults; i++) {
			Pair<L, Double> label = topLabels.peek();
			candidateLabels.add(label);
		}
		for (int i=0; i<actualNumResults; i++) {
			Pair<L, Double> label = topLabels.poll();
			candidateLabels.set(actualNumResults-i-1, label);
		}
		return candidateLabels;
	}
	
	protected PriorityQueue<Pair<Double, Integer>> findKNearest(T data) {
		// Find k nearest neighbors
		PriorityQueue<Pair<Double, Integer>> topKSimScores = new PriorityQueue<Pair<Double, Integer>>(
				this.labeledInsts.size(),
				new Comparator<Pair<Double, Integer>>() {
					@Override
					public int compare(Pair<Double, Integer> o1,
							Pair<Double, Integer> o2) {
						// TODO Auto-generated method stub
						if (o1.getFirst() > o2.getFirst()) {
							return 1;
						} else if (o1.getFirst() < o2.getFirst()) {
							return -1;
						} else {
							return 0;
						}
					}
				});

		for (int instIndex = 0; instIndex < this.labeledInsts.size(); instIndex++) {
			Pair<T, L> labeledInst = this.labeledInsts.get(instIndex);
			double simScore = this.simFunc.sim(labeledInst.getFirst(), data);
			if (costFunc != null) {
				simScore *= costFunc.cost(labeledInst.getFirst());
			}
			topKSimScores.add(new Pair<Double, Integer>(simScore, instIndex));
			if (topKSimScores.size() > k) {
				topKSimScores.poll();
			}
		}
		return topKSimScores;
	}

	@Override
	public L classify(T data) {
		return classify(data, 1).get(0);
	}
	
	

}
