package org.baseagent.util;

public class Pair<A, B> {
	private A first;
	private B second;
	
	public Pair() {
		setFirst((A)null);
		setSecond((B)null);
	}
	
	public Pair(A first, B second) {
		setFirst(first);
		setSecond(second);
	}
	
	public void setFirst(A first) {
		this.first = first;
	}
	
	public void setSecond(B second) {
		this.second = second;
	}
	
	public A getFirst() {
		return this.first;
	}
	
	public B getSecond() {
		return this.second;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Pair)) return false;
		
		Pair pair2 = (Pair)o;
		return ((pair2.first != null && pair2.first.equals(this.first)) && ((pair2.second != null) && pair2.second.equals(this.second)));
	}
	
	@Override
	public int hashCode() {
		return (((first != null) ? first.hashCode() : 43) * ((second != null) ? second.hashCode() : 37));
	}
}
