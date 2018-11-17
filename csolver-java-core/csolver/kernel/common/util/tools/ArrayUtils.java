/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *
 *  Choco Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *    
 */

package csolver.kernel.common.util.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import csolver.kernel.common.IIndex;
import csolver.kernel.common.util.tools.java.lang.reflect.NArray;

/*
 * User : charles
 * Mail : cprudhom(a)emn.fr
 * Date : 3 juil. 2009
 * Since : Choco 2.1.0
 * Update : Choco 2.1.5
 */

public final class ArrayUtils {

    static HashSet<Long> hashset = new HashSet<Long>();
    static ArrayList arraylist = new ArrayList();

    private ArrayUtils() {
        super();
    }

    public static int[] zeroToN(int n) {
        final int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            r[i] = i;
        }
        return r;
    }

    public static int[] oneToN(int n) {
        final int[] r = new int[n];
        for (int i = 1; i <= n; i++) {
            r[i - 1] = i;
        }
        return r;
    }

    public static int[] linspace(int begin, int end) {
        if (end > begin) {
            int[] r = new int[end - begin];
            for (int i = begin; i < end; i++) {
                r[i - begin] = i;
            }
            return r;
        } else return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] getColumn(final T[][] array, final int column) {
        if (array != null && array.length > 0
                && column >= 0 && array[0].length > column) {
            T[] res = (T[]) NArray.newInstance(array[0][column].getClass(), array.length);
            for (int i = 0; i < array.length; i++) {
                res[i] = array[i][column];
            }
            return res;
        }
        return null;
    }

    public static <T> int length(final T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            if (array != null) length += array.length;
        }
        return length;
    }

    public static <T> boolean contains(T[] array, T obj) {
        for (T elem : array) {
            if (elem.equals(obj)) return true;
        }
        return false;
    }

    public static <T> T get(int index, final T[]... arrays) {
        int shift = index;
        for (T[] tab : arrays) {
            if (shift < tab.length) {
                return tab[shift];
            } else {
                shift -= tab.length;
            }
        }
        return null;
    }


    public static <T> T get(int index, final List<T>... arrays) {
        int shift = index;
        for (List<T> tab : arrays) {
            if (shift < tab.size()) {
                return tab.get(shift);
            } else {
                shift -= tab.size();
            }
        }
        return null;
    }

    /**
     * Append two Arrays
     *
     * @param toAppend array of arrays to append
     * @return a new Array composed of both given in parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[]... toAppend) {
        int total = length(toAppend);
        T[] ret = (T[]) NArray.newInstance(toAppend.getClass().getComponentType().getComponentType(), total);
        int pos = 0;
        for (T[] tab : toAppend) {
            if (tab != null) {
                System.arraycopy(tab, 0, ret, pos, tab.length);
                pos += tab.length;
            }
        }
        return ret;
    }

    /**
     * Reverse all signs of the a given int table.
     *
     * @param tab array to inverse
     */
    public static void inverseSign(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = -tab[i];
        }
    }

    public static void reverse(int[] tab) {
        int tmp;
        final int n = tab.length;
        for (int i = 0; i < n / 2; i++) {
            tmp = tab[i];
            tab[i] = tab[n - i - 1];
            tab[n - i - 1] = tmp;
        }
    }

    public static <T> void reverse(T[] tab) {
        T tmp;
        final int n = tab.length - 1;
        for (int i = 0; i < n / 2; i++) {
            tmp = tab[i];
            tab[i] = tab[n - i];
            tab[n - i] = tmp;
        }
    }

    /**
     * apply a permuation on an array
     */
    @SuppressWarnings("unchecked")
    public static <T> void permutation(int[] permutation, T[] tab) {
        T[] tmp = (T[]) NArray.newInstance(tab[0].getClass(), tab.length);
        System.arraycopy(tab, 0, tmp, 0, tab.length);
        for (int i = 0; i < tab.length; i++) {
            tab[i] = tmp[permutation[i]];
        }
    }

    public static <T> List<T> toList(T[] array) {
        return Arrays.asList(array);
    }

    public static <T> T[] toArray(Class c, List<T> list) {
        //        T[] array = (T[])Array.newInstance(c, list.size());
        //        return list.toArray(array);
        return list.toArray((T[]) NArray.newInstance(c, list.size()));
    }

    public static <T> T[] toArray(ArrayList<T> list) {//T[] a, ArrayList<T> list) {
		int _size = list.size();
		if (_size == 0) return null;
		
		//if (a.length < _size) {
		//	a = (T[])java.lang.reflect.Array.newInstance(
		//			a.getClass().getComponentType(),
		//			_size);
		//	a = toArray(list.get(0).getClass(), list);
        //}
		
        //return a;
    	return toArray(list.get(0).getClass(), list);
    }

    public static <T> T[] toArray(HashMap<Integer,T> list) {
        
    	ArrayList<T> arraylist = ArrayUtils.hashMapToArrayList(list);
    	return ArrayUtils.toArray(arraylist);
    }
    
    public static <T> ArrayList<T> hashMapToArrayList(HashMap<Integer,T> list){
    	ArrayList<T> arraylist = new ArrayList<T>();
    	//System.out.println("list.size::" + list.size());
    	for (T value : list.values()) {
    		arraylist.add(value);
    	}
    	return arraylist;
    }
    
	public static <T> T[] getValues(T[] a , HashMap<Integer, T> list) {

		int _size = list.size();
	
		if (a.length < _size) {
			a = (T[])NArray.newInstance(
					a.getClass().getComponentType(),
					_size);
        }

        T[] values = toArray(list);
        if( values == null ) return a;
        
        for (int i = values.length, j = 0; i-- > 0;) {
                a[j++] = (T) values[i];
        }

        return a;
		
		//return toArray(list);
	}
	
    public static <T> T[][] transpose(T[][] matrix) {
        T[][] ret = (T[][]) NArray.newInstance(
        					matrix.getClass().getComponentType(), matrix[0].length);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (T[]) NArray.newInstance(
            				matrix[0].getClass().getComponentType(), matrix.length);
        }

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                ret[j][i] = matrix[i][j];

        return ret;

    }

    public static Integer[][] transpose(Integer[][] matrix) {
        Integer[][] ret = (Integer[][]) NArray.newInstance(matrix.getClass().getComponentType(), matrix[0].length);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (Integer[]) NArray.newInstance(
            		          matrix[0].getClass().getComponentType(), matrix.length);
        }

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                ret[j][i] = matrix[i][j];

        return ret;

    }

    public static <T> T[] flatten(T[][] matrix) {
        int sz = 0;
        for (T[] t : matrix) sz += t.length;
        T[] ret = (T[]) NArray.newInstance(matrix[0].getClass().getComponentType(), sz);
        int k = 0;
        for (T[] ta : matrix) {
            for (T t : ta)
                ret[k++] = t;
        }
        return ret;
    }

    public static <T> T[] flattenSubMatrix(int iMin, int iLength, int jMin, int jLength, T[][] matrix) {
        T[] ret = (T[]) NArray.newInstance(matrix[0].getClass().getComponentType(), iLength * jLength);
        for (int i = 0, k = 0; i < iLength; i++, k += jLength)
            System.arraycopy(matrix[iMin + i], jMin, ret, k, jLength);
        return ret;
    }

    public static int[] flatten(int[][] matrix) {
        int sz = 0;
        for (int[] t : matrix) sz += t.length;
        final int[] ret = new int[sz];
        int k = 0;
        for (int[] ta : matrix) {
            for (int t : ta)
                ret[k++] = t;
        }
        return ret;
    }

    /**
     * create a new array which contains sorted distinct values;
     *
     * @param values
     * @return
     */
    public static int[] getNonRedundantSortedValues(int[] values) {
        return createNonRedundantSortedValues(ArrayUtils.convertToInteger(values));
    }

    /**
     * create an array which contains sorted distinct values. do not modify the original list
     */
    public static int[] getNonRedundantSortedValues(ArrayList<Integer> values) {
        return createNonRedundantSortedValues((ArrayList<Integer>) values.clone());
    }

    public static int[] createNonRedundantSortedValues(ArrayList<Integer> values) {
        //Array.sort
    	//values.sort();
    	values = ArrayUtils.sort(values);
        int offset = 1;
        while (offset < values.size()) {
            while (values.get(offset - 1) == values.get(offset)) {
                values.remove(offset);
                if (offset == values.size()) {
                    break;
                }
            }
            offset++;
        }
        return ArrayUtils.convertIntegers(values);
    }

    public static <V extends IIndex> V[] getNonRedundantObjects(V[] all) {

        if (hashset == null) {
        	hashset = new HashSet<Long>();
        }
        hashset.clear();
        if (arraylist == null) {
        	arraylist = new ArrayList();
        }
        arraylist.clear();
        for (V v : all) {
            if (!hashset.contains(v.getIndex())) {
            	arraylist.add(v);
                hashset.add(v.getIndex());
            }
        }
        if (arraylist.size() != all.length) {
            V[] a = (V[]) NArray.newInstance((Class<? extends V[]>) all.getClass().getComponentType(), arraylist.size());
            arraylist.toArray(a);
            return a;
        }
        return all;
    }

    @SuppressWarnings({"unchecked"})
    public static <V extends IIndex> V[] getNonRedundantObjects(Class classe, V[] all) {
        if (hashset == null) {
        	hashset = new HashSet<Long>();
        }
        hashset.clear();
        if (arraylist == null) {
        	arraylist = new ArrayList();
        }
        arraylist.clear();
        for (V v : all) {
            if (!hashset.contains(v.getIndex())) {
            	arraylist.add(v);
                hashset.add(v.getIndex());
            }
        }
        if (arraylist.size() != all.length) {
            V[] a = (V[]) NArray.newInstance(classe, arraylist.size());
            arraylist.toArray(a);
            return a;
        }
        return all;

    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> T[] sort(Set<T> set) {
        final LinkedList<T> tmpl = new LinkedList<T>(set);
        if (tmpl.isEmpty()) {
            return null;
        } else {
            T[] tmpa = (T[]) NArray.newInstance(tmpl.getFirst().getClass(), tmpl.size());
            tmpl.toArray(tmpa);
            Arrays.sort(tmpa);
            return tmpa;
        }
    }

    public static int[][][][] swallowCopy(int[][][][] arr) {
        int s0 = arr.length;
        int[][][][] copy = new int[s0][][][];
        for (int i = s0 - 1; i >= 0; i--) {
            int s1 = arr[i].length;
            copy[i] = new int[s1][][];
            for (int j = s1 - 1; j >= 0; j--) {
                int s2 = arr[i][j].length;
                copy[i][j] = new int[s2][];
                for (int k = s2 - 1; k >= 0; k--) {
                    int s3 = arr[i][j][k].length;
                    copy[i][j][k] = new int[s3];
                    System.arraycopy(arr[i][j][k], 0, copy[i][j][k], 0, s3);
                }
            }
        }
        return copy;

    }

    public static int[][][] swallowCopy(int[][][] arr) {
        int s0 = arr.length;
        int[][][] copy = new int[s0][][];
        for (int i = s0 - 1; i >= 0; i--) {
            int s1 = arr[i].length;
            copy[i] = new int[s1][];
            for (int j = s1 - 1; j >= 0; j--) {
                int s2 = arr[i][j].length;
                copy[i][j] = new int[s2];

                System.arraycopy(arr[i][j], 0, copy[i][j], 0, s2);
            }
        }
        return copy;

    }

    public static int[] zeroToNShuffle(int nb) {
        return zeroToNShuffle(nb, System.currentTimeMillis());
    }

    public static int[] zeroToNShuffle(int nb, long seed) {
        Random r = new Random(seed);
        int[] ret = new int[nb];
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        for (int i = 0; i < nb; i++) tmp.add(i);

        for (int i = 0; i < nb; i++) {
            int idx = r.nextInt(tmp.size());
            ret[i] = tmp.get(idx);
            System.err.println(ret[i]);
            System.err.println(tmp.remove(idx));
        }

        return ret;

    }
    
	public static int[] convertIntegers(List<Integer> integers)
	{
	    return convertIntegers(
	    		integers.toArray(new Integer[integers.size()]));
	}
	
	public static int[] convertIntegers(Integer[] integers)
	{
	    int[] ret = new int[integers.length];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers[i];
	    }
	    return ret;
	}
		
	public static ArrayList<Integer> convertToInteger(int[] values) 
	{ 
		ArrayList<Integer> a = new ArrayList<Integer>(values.length);
	    for (Integer i : values) a.add(i);	    
	    return a;
	}

	public static ArrayList<Integer> convertToInteger(Integer[] values) 
	{ 
		ArrayList<Integer> a = new ArrayList<Integer>(values.length);
	    for (Integer i : values) a.add(i);	    
	    return a;
	}
	
	public static int[] convertIntegers(HashSet<Integer> set) {
		return ArrayUtils.convertIntegers(
				set.toArray(new Integer[set.size()]));
	}

	public static int[] convertIntegers(Set<Integer> integers)
	{
		return ArrayUtils.convertIntegers(
				integers.toArray(new Integer[integers.size()]));
	}

	public static ArrayList<Integer> sort(ArrayList<Integer> values){
		
        final LinkedList<Integer> tmpl = new LinkedList<Integer>(values);
        
        if (tmpl.isEmpty()) {
            return null;
        } else {
        	
        	Integer[] tmpa = (
        			Integer[]) NArray.newInstance(
        					tmpl.getFirst().getClass(), tmpl.size());
        	
        	tmpl.toArray(tmpa);            
            Arrays.sort(tmpa);
            
            return ArrayUtils.convertToInteger(tmpa);
        }

	}




	
	
	//public static void forEach(ArrayList<Integer> arrayList,
	//		IntProcedure tIntProcedure) {
	//	System.out.println("ArrayUtils forEach ERRRRRRORRRRRR");
		// TODO Auto-generated method stub
		
	//}

	//public static void forEachDescending(ArrayList<Integer> binsCLB,
	//		IntProcedure minimumNumberOfNewBins) {
	//	System.out.println("ArrayUtils forEachDescending ERRRRRRORRRRRR");
		// TODO Auto-generated method stub
		
	//}
}
