package edu.fiu.cs.kdrg.tkrec.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author Liang Tang
 * @date Apr 1, 2013 5:23:41 PM
 * @email tangl99@gmail.com MIT license
 */
public class SparseVector {

	protected Map<Integer, Double> valueMap = new HashMap<Integer, Double>();

	transient int hashCode = -1;

	boolean isNormalized = false;
	
	public SparseVector() {
		updateHashCode();
	}

	public void addValue(int dim, double v) {
		valueMap.put(dim, v + value(dim));
		updateHashCode();
	}

	public void setValue(int dim, double v) {
		valueMap.put(dim, v + value(dim));
		updateHashCode();
	}

	public double value(int dim) {
		Double value = valueMap.get(dim);
		if (value == null)
			return 0;
		else
			return value;
	}

	public void add(SparseVector v) {
		for (Map.Entry<Integer, Double> val : v.valueMap.entrySet()) {
			this.addValue(val.getKey(), val.getValue());
		}
		updateHashCode();
	}

	public void div(double c) {
		for (Map.Entry<Integer, Double> val : valueMap.entrySet()) {
			this.valueMap.put(val.getKey(), val.getValue() / c);
		}
		updateHashCode();
	}

	public double dotProduct(SparseVector sv) {
		double s = 0;
		Iterator<Entry<Integer, Double>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Double> entry = it.next();
			s += entry.getValue() * sv.value(entry.getKey());
		}
		return s;
	}

	public double norm2() {
		double s = 0;
		Iterator<Entry<Integer, Double>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Double> entry = it.next();
			s += entry.getValue() * entry.getValue();
		}
		return Math.sqrt(s);
	}

	public SparseVector normalize() {
		double s = 0;
		Iterator<Entry<Integer, Double>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Double> entry = it.next();
			s += entry.getValue() * entry.getValue();
		}
		SparseVector normalizedV = new SparseVector();
		double r = Math.sqrt(s);
		it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Double> entry = it.next();
			normalizedV.addValue(entry.getKey(), entry.getValue() / r);
		}
		normalizedV.isNormalized = true;
		return normalizedV;
	}

	public boolean isNormalized() {
		return isNormalized;
	}
	
	public Set<Integer> getNonZeroDimensions() {
		return this.valueMap.keySet();
	}

	private void updateHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valueMap == null) ? 0 : valueMap.hashCode());
		this.hashCode = result;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SparseVector other = (SparseVector) obj;
		if (valueMap == null) {
			if (other.valueMap != null)
				return false;
		} else if (!valueMap.equals(other.valueMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return valueMap.toString();
	}

}
