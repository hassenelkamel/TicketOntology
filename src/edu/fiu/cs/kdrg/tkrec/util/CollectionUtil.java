package edu.fiu.cs.kdrg.tkrec.util;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Liang Tang
 * @email tangl99@gmail.com
 * Updated by Nov 15, 2012 3:04:31 PM
 *
 */
public class CollectionUtil {
	
	public static List<Integer> asList(int[] array) {
		List<Integer> list = new ArrayList<Integer>(array.length);
		for (int v: array) {
			list.add(v);
		}
		return list;
	}
	
	public static List<Double> asDoubleList(Collection<Long> l) {
		List<Double> dl = new ArrayList<Double>(l.size());
		for (long v: l) {
			dl.add((double) v);
		}
		return dl;
	}
	
	public static<T> List<T> asList(Collection<T> c) {
		List<T> l = new ArrayList<T>(c.size());
		for (T e: c) {
			l.add(e);
		}
		return l;
	}
	
	public static List<Long> asList(long[] array) {
		List<Long> list = new ArrayList<Long>(array.length);
		for (long v: array) {
			list.add(v);
		}
		return list;
	}
	
	public static List<Double> asList(double[] array) {
		List<Double> list = new ArrayList<Double>(array.length);
		for (double v: array) {
			list.add(v);
		}
		return list;
	}
	
	public static<T> List<T> asList(Set<T> set) {
		if (set == null) {
			return null;
		}
		List<T> list = new ArrayList<T>(set.size());
		for (T e: set) {
			list.add(e);
		}
		return list;
	}
	
	public static<T> boolean isSubList(List<T> bigList, List<T> smallList) {
		for (int i=0; i<bigList.size(); i++) {
			if (i + smallList.size() > bigList.size()) {
				break;
			}
			if (bigList.get(i).equals(smallList.get(0))) {
				int numIdenticals = 0;
				for (int j=0; j<smallList.size(); j++) {
					if (bigList.get(i+j).equals(smallList.get(j)) == false) {
						break;
					}
					numIdenticals++;
				}
				if (numIdenticals == smallList.size()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static int[] concatenate(int[] arr1, int[] arr2) {
		int[] arr = new int[arr1.length+arr2.length];
		System.arraycopy(arr1, 0, arr, 0, arr1.length);
		System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
		return arr;
	}
	
	public static int[] asIntegerArray(Collection<Integer> c) {
		if (c == null) {
			return null;
		}
		int[] array = new int[c.size()];
		int index=0;
		for (Integer val: c) {
			array[index] = val;
			index++;
		}
		return array;
	}
	
	public static Set<Integer> asSet(int[] array) {
		if (array == null) {
			return null;
		}
		Set<Integer> set = new HashSet<Integer>();
		for (int val: array) {
			set.add(val);
		}
		return set;
	}
	
	public static<T> Set<T> asSet(Collection<T> c) {
		Set<T> set = new HashSet<T>();
		set.addAll(c);
		return set;
	}
	
	public static<T> Set<T> intersect(Set<T> s1, Set<T> s2) {
		Set<T> ret = new HashSet<T>();
		for (T e: s1) {
			if (s2.contains(e)) {
				ret.add(e);
			}
		}
		return ret;
	}
	
	public static<T extends Comparable<? super T>> void mergeSortedArrays(T[] mergedArr, T[] s1, T[] s2) {
		int i1=0;
		int i2=0;
		int i = 0;
		T v1 = null;
		T v2 = null;
		if (i1 < s1.length) {
			v1 = s1[i1];
		}
		if (i2 < s2.length) {
			v2 = s2[i2];
		}
		while(v1 != null || v2 != null) {
			if (v1 == null) {
				System.arraycopy(s2, i2, mergedArr, i, s2.length - i2);
				break;
			}
			else if (v2 == null) {
				System.arraycopy(s1, i1, mergedArr, i, s1.length - i1);
				break;
			}
			else {
				int cmp = v1.compareTo(v2);
				if (cmp <0) {
					i1++;
					mergedArr[i] = v1;
					if (i1>= s1.length) {
						v1 = null;
					}
					else {
						v1 = s1[i1];
					}
					i++;
				}
				else if (cmp > 0) {
					i2++;
					mergedArr[i] = v2;
					if (i2>= s2.length) {
						v2 = null;
					}
					else {
						v2 = s2[i2];
					}
					i++;
				}
				else {
					i1++;
					mergedArr[i] = v1;
					if (i1>= s1.length) {
						v1 = null;
					}
					else {
						v1 = s1[i1];
					}	
					i++;
					i2++;
					mergedArr[i] = v2;
					if (i2>= s2.length) {
						v2 = null;
					}
					else {
						v2 = s2[i2];
					}
					i++;					
				}
			}
		}
	}
	
	public static<T extends Comparable<? super T>> List<T> intersectByMerge(List<T> s1, List<T> s2) {
		List<T> merged = new ArrayList<T>(Math.min(s1.size(), s2.size()));
		if (s1.size() == 0 || s2.size() == 0) {
			return merged;
		}
		int i1 = 0;
		int i2 = 0;
		T v1 = s1.get(i1);
		T v2 = s2.get(i2);
		while(true) {
			int cmp = v1.compareTo(v2);
			if (cmp < 0) {
				i1++;
				if (i1 >= s1.size()) {
					break;
				}
				v1 = s1.get(i1);
			}
			else if (cmp > 0) {
				i2++;
				if (i2 >= s2.size()) {
					break;
				}
				v2 = s2.get(i2);
			}
			else {
				merged.add(v1);
				i1++;
				if (i1 >= s1.size()) {
					break;
				}
				v1 = s1.get(i1);
				i2++;
				if (i2 >= s2.size()) {
					break;
				}
				v2 = s2.get(i2);
			}
		}
		return merged;
	}
	
	public static<T> boolean hasCommon(Set<T> s1, Set<T> s2) {
		Set<T> largeSet;
		Set<T> smallSet;
		if (s1.size() > s2.size()) {
			largeSet = s1;
			smallSet = s2;
		}
		else {
			largeSet = s2;
			smallSet = s1;
		}
		for (T e: smallSet) {
			if (largeSet.contains(e)) {
				return true;
			}
		}
		return false;
	}
	
	public static Set<Integer> range(int start, int end) {
		Set<Integer> s = new HashSet<Integer>();
		for (int i=start; i<end;i++) {
			s.add(i);
		}
		return s;
	}
	
	
	
	public static<T extends Comparable<T>> int findFirstPos(List<T> list, T key) {
		int pos = Collections.binarySearch(list, key);
		if (pos < 0) {
			pos= - pos - 1;
		}
		while(pos >= 0) {
			if (list.get(pos).compareTo(key) == 0) {
				pos--;
			}
			else {
				break;
			}
		}
		return pos+1;
	}
	
	public static<T extends Comparable<T>> int findLastPos(List<T> list, T key) {
		int pos = Collections.binarySearch(list, key);
		if (pos < 0) {
			pos= - pos - 1;
		}
		while(pos < list.size()) {
			if (list.get(pos).compareTo(key) == 0) {
				pos++;
			}
			else {
				break;
			}
		}
		return pos-1;
	}
	
	
}
