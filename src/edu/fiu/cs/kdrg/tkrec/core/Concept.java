package edu.fiu.cs.kdrg.tkrec.core;

import java.util.HashSet;

/**
 * @author Chunqiu Zeng
 * 
 */
public class Concept {

	private String name = "";

	private HashSet<Concept> children = new HashSet<Concept>();

	private HashSet<String> terms = new HashSet<String>();

	public Concept() {
	}

	public Concept(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the children
	 */
	public HashSet<Concept> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(HashSet<Concept> children) {
		this.children = children;
	}

	/**
	 * @return the terms
	 */
	public HashSet<String> getTerms() {
		return terms;
	}

	/**
	 * @param terms
	 *            the terms to set
	 */
	public void setTerms(HashSet<String> terms) {
		this.terms = terms;
	}

	public boolean isDescendant(Concept c) {
		if (children.contains(c))
			return true;
		for (Concept d : children) {
			if (d.isDescendant(c))
				return true;
		}
		return false;
	}

	public void addChildConcept(Concept c) {
		if (isDescendant(c))
			return;
		else {
			children.add(c);
		}
	}

	public boolean isContainedTerm(String t) {
		if (terms.contains(t))
			return true;
		for (Concept d : children) {
			if (d.isContainedTerm(t))
				return true;
		}
		return false;
	}

	public void addTerm(String t) {
		if (isContainedTerm(t))
			return;
		terms.add(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept other = (Concept) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

}
