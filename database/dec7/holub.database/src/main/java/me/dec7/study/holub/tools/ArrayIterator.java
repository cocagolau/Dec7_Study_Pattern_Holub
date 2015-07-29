package me.dec7.study.holub.tools;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator implements Iterator {
	
	private int position = 0;
	
	private final Object[] items;
	
	public ArrayIterator(Object[] items) {
		this.items = items;
	}

	@Override
	public boolean hasNext() {
		
		return (position < items.length);
	}

	@Override
	public Object next() {
		
		if (position >= items.length) {
			throw new NoSuchElementException();
		}
		
		return items[position++];
	}
	
	public void remove() {
		throw new UnsupportedOperationException("ArrayIterator.remove()");
	}
	
	public Object[] toArray() {
		
		return (Object[]) items.clone();
	}

}
