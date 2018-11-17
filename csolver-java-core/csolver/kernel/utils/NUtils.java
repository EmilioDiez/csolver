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
import java.util.HashMap;

import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.common.opres.pack.AbstractFunctionDFF;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.memory.IStateLong;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.MultipleVariables;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.model.variables.integer.IntegerExpressionVariable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.real.RealVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;

public class NUtils<V> {

    /**
     * Applies the procedure to each value in the list in ascending
     * (front to back) order.
     *
     * @param procedure a <code>TIntProcedure</code> value
     * @return true if the procedure did not terminate prematurely.
     */
    public static ArrayList<Integer> forEach(ArrayList<Integer> arrayList, NIntProcedure procedure) {

    	/** the index after the last entry in the list */
        int _pos = arrayList.size();
        /** the data of the list */
        int[] _data = ArrayUtils.convertIntegers(arrayList);
        
        for (int i = 0; i < _pos; i++) {
            if (! procedure.execute(_data[i])) {
                return ArrayUtils.convertToInteger(_data);
            }
        }
        
        return ArrayUtils.convertToInteger(_data);
        
    }
    
    /**
     * Applies the procedure to each value in the list in descending
     * (back to front) order.
     *
     * @param procedure a <code>TIntProcedure</code> value
     * @return true if the procedure did not terminate prematurely.
     */
    public static ArrayList<Integer> forEachDescending(ArrayList<Integer> arrayList, NIntProcedure procedure) {
    	
    	/** the index after the last entry in the list */
        int _pos = arrayList.size();
        /** the data of the list */
        int[] _data = ArrayUtils.convertIntegers(arrayList);
        
        for (int i = _pos; i-- > 0;) {
            if (! procedure.execute(_data[i])) {
                return ArrayUtils.convertToInteger(_data);
            }
        }
        return ArrayUtils.convertToInteger(_data);
    }
    
	public static ArrayList<Integer> transformValues(
			ArrayList<Integer> arrayList, AbstractFunctionDFF function) {
    	
		/** the index after the last entry in the list */
        int pos = arrayList.size();
        /** the data of the list */
        int[] data = ArrayUtils.convertIntegers(arrayList);
        
		for (int i = pos; i-- > 0;) {
			data[i] = function.execute(data[i]);
        }
		return ArrayUtils.convertToInteger(data);
	}
	
    /**
     * Executes <tt>procedure</tt> for each value in the map.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(HashMap<Integer,V> _values, NObjectProcedure<V> procedure) {

    	V[] values = ArrayUtils.toArray(_values);
        
    	for (int i = values.length; i-- > 0;) {
            if (! procedure.execute(values[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static int[] copyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0,
                         min(original.length, newLength));
        return copy;
    }
 
    public static boolean[] copyOf(boolean[] original, int newLength) {
    	boolean[] copy = new boolean[newLength];
        System.arraycopy(original, 0, copy, 0,
                         min(original.length, newLength));
        return copy;
    }
    
    public static boolean[] copyOf(boolean[] original) {
    	int newLength = original.length;
    	boolean[] copy = new boolean[newLength];
        System.arraycopy(original, 0, copy, 0,
                         min(original.length, newLength));
        return copy;
    }
    
    public static long[] copyOf(long[] original, int newLength) {
        long[] copy = new long[newLength];
        System.arraycopy(original, 0, copy, 0,
                         min(original.length, newLength));
        return copy;
    }
    
    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }
    
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, (Class<T[]>) original.getClass());
    }
    
    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
    	return copyOfRange(original, 0, newLength, newType);
    }
    
	public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
    }
    
    public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
        
    	int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) newInstance(newType, newLength);
        
            //T[] copy = (T[]) new Object[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        
        return copy;
    }

	public static <T> T[] newInstance(Class<T> classe, int size) {
		
		T[] instance;
		
		if ((Object)classe == (Object)Object[].class){
			instance = (T[]) new Object[size];
			return instance;
		} else if ( classe == IntegerVariable.class){
			instance = (T[]) new IntegerVariable[size];
			return instance;
		}else if ( classe == SetVariable.class){
			instance = (T[]) new SetVariable[size];
			return instance;
		}else if ( classe == RealVariable.class){
			instance = (T[]) new RealVariable[size];
			return instance;
		}else if ( classe == Variable.class){
			instance = (T[]) new Variable[size];
			return instance;
		}else if ( classe == IntegerExpressionVariable.class){
			instance = (T[]) new IntegerExpressionVariable[size];
			return instance;
		}else if ( classe == MultipleVariables.class){
			instance = (T[]) new MultipleVariables[size];
			return instance;
		}else if ( classe == Constraint.class){
			instance = (T[]) new Constraint[size];
			return instance;
		}else if ( classe == IntDomainVar.class){
			instance = (T[]) new IntDomainVar[size];
			return instance;
		}else if ( classe == Var.class){
			instance = (T[]) new Var[size];
			return instance;
		}else if ( classe == IStateLong.class){
			instance = (T[]) new IStateLong[size];
			return instance;
		}else{
			//TODO: fix this, develop a proper instantiation method
			Logger.severe(NUtils.class, " NArrays::  classe.getName(): " + classe.getName() +
			               " needs to be included in NArrays.newInstance() ");
		}
		      
		instance = (T[]) new Object[size];
		return instance;
	}

	public static String printArray(ArrayList<Integer> sizes) {
		Integer i = 0;
		String s = new String();
		for (i = 0; i<sizes.size(); i++){
			s = s.concat(" " + sizes.get(i).toString());
		}
		return s;
	}

	public static String printArray(Object[] array) {
		Integer i = 0;
		String s = new String();
		for (i = 0; i<array.length; i++){
			s = s.concat(" " + array[i].toString());
		}
		return s;
	}

	public static int[] copyOf(int[] original) {
		int newLength = original.length;
		return copyOf(original, newLength);
	}
}
