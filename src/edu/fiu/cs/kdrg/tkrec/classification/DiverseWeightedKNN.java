package edu.fiu.cs.kdrg.tkrec.classification;

import java.util.ArrayList;


import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;
import edu.fiu.cs.kdrg.tkrec.util.CountMap;
import edu.fiu.cs.kdrg.tkrec.util.Pair;




public class DiverseWeightedKNN<T,L> extends WeightedKNN<T,L>{
	
	public DiverseWeightedKNN(SimilarityFunction<T> simFunc, int k, List<Set<L>> groups) {
		super(simFunc, k, new GroupSimilarityFunc<L>(groups), 1.0);
	} 
	
	static class GroupSimilarityFunc<L> implements SimilarityFunction<L> {
		Map<L, Integer> groupMap = new HashMap<L, Integer>();
		public GroupSimilarityFunc(List<Set<L>> groups) {
			for (int i=0; i<groups.size(); i++) {
				Set<L> group = groups.get(i);
				for (L o : group) {
					groupMap.put(o, i);
				}
			}
		}

		@Override
		public double sim(L o1, L o2) {
			// TODO Auto-generated method stub
			Integer groupId1 = groupMap.get(o1);
			Integer groupId2 = groupMap.get(o2);
			if (groupId1 != null && groupId2 != null) {
				if (groupId1 == groupId2) {
					return 1.0;
				}
				else {
					return 0;
				}
			}
			else {
				return 0;
			}
		}

		@Override
		public double maxValue() {
			// TODO Auto-generated method stub
			return 1.0;
		}

		@Override
		public double minValue() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
}
