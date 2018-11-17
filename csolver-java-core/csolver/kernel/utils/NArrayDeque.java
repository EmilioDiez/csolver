/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Vector;

public class NArrayDeque<E> extends Vector<E> implements Queue<E>{

	public NArrayDeque() {
		super();
	}

	public NArrayDeque(Collection<? extends E> c) {
		super(c);
	}

	public NArrayDeque(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	public NArrayDeque(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public boolean offer(E e) {
		return super.add(e);
	}

	@Override
	public E remove() {
		return super.remove(0);
	}

	@Override
	public E poll() {
		return super.remove(0);
	}

	@Override
	public E element() {
		return super.elementAt(0);
	}

	@Override
	public E peek() {
		return super.elementAt(0);
	}

	public Object removeLast() {
		int size = super.size();
		size--;
		return super.remove(size);
	}

	public E removeFirst() {
		return super.remove(0);
	}

	public Object getLast() {
		return super.lastElement();
	}
	
	public void addLast(E e){
		super.add(e);
	}

	public boolean removeLastOccurrence(E e){
		
		int i = 0;
		
		for (E _e : this){
			if ( _e.equals(e)){
				super.remove(i);
			 return true;
			}
			i++;
		}
		
		return false;
	}
	
	public void push(E e){
		addFront(e);
	}
	
	public E pop(){
		return removeFirst();
	}

	//public Iterator<E> descendingIterator() {
	//	return null;
	//}
	
    public Iterator<E> descendingIterator() {
    	
    	final ListIterator<E> listIterator = super.listIterator();  
    	
        return new Iterator<E>() {

            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

			public E next() {
                return listIterator.previous();
            }

            public void remove() {
            	listIterator.remove();
            }

        };
    }
    
	public void addFront(E e){
		super.add(0, e);
	}
	
}
