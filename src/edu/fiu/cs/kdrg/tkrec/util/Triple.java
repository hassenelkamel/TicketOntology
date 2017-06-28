package edu.fiu.cs.kdrg.tkrec.util;

public class Triple<A, B, C> {

	private A first;

	private B second;

	private C third;

	public Triple(A a, B b, C c) {
		first = a;
		second = b;
		third = c;
	}

	/**
	 * @return the first
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(B second) {
		this.second = second;
	}

	/**
	 * @return the third
	 */
	public C getThird() {
		return third;
	}

	/**
	 * @param third
	 *            the third to set
	 */
	public void setThird(C third) {
		this.third = third;
	}

	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
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
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
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
		Triple other = (Triple) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

}
