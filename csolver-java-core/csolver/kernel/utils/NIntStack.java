/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.utils;

import java.util.ArrayList;

import csolver.kernel.common.util.tools.ArrayUtils;


public class NIntStack {


    protected ArrayList<Integer> _list;

    public NIntStack(int capacity) {
        _list = new ArrayList<Integer>(capacity);
    }

    public NIntStack() {
    	 _list = new ArrayList<Integer>();
	}

	/**
     * Pushes the value onto the top of the stack.
     *
     * @param val an <code>int</code> value
     */
    public void push(int val) {
        _list.add(val);
    }

    /**
     * Removes and returns the value at the top of the stack.
     *
     * @return an <code>int</code> value
     */
    public int pop() {
        return _list.remove(_list.size() - 1);
    }

    /**
     * Returns the value at the top of the stack.
     *
     * @return an <code>int</code> value
     */
    public int peek() {
        return _list.get(_list.size() - 1);
    }

    /**
     * Returns the current depth of the stack.
     */
    public int size() {
    	//int size = 0;
    	//if (_list == null)
    	//{return 0;}
    	
        return _list.size();
    }

    /**
     * Clears the stack, reseting its capacity to the default.
     */
    public void clear() {
    	//if (_list == null)
    	//{return;}
        _list.clear();
    }

    /**
     * Copies the contents of the stack into a native array. Note that this will NOT
     * pop them out of the stack.
     *
     * @return an <code>int[]</code> value
     */
    public int[] toNativeArray() {
        return ArrayUtils.convertIntegers(_list);
    }

    /**
     * Copies a slice of the list into a native array. Note that this will NOT
     * pop them out of the stack.
     *
     * @param dest the array to copy into.
     */
    //public void toNativeArray(int[] dest) {
    //    _list.toNativeArray(dest, 0, size());
    //}
} 
