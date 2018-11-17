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

package csolver.kernel.memory;

import csolver.kernel.common.logging.CSolverLogging;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import nitoku.log.Logger;

/**
 * Describes an search vector with states (describing some history of the data structure).
 */
public interface IStateDoubleVector {

	
  /**
   * Minimal capacity of a vector
   */
  public static final int MIN_CAPACITY = 8;

  /**
   * Returns the current size of the stored search vector.
   */

  public int size();

  /**
   * Checks if the vector is empty.
   */

  public boolean isEmpty();

  /**
   * Adds a new search at the end of the vector.
   *
   * @param i The search to add.
   */

  public void add(double i);


  /**
   * Removes an int.
   *
   * @param i The search to remove.
   */

  public void remove(int i);


  /**
   * removes the search at the end of the vector.
   * does nothing when called on an empty vector
   */

  public void removeLast();

  /**
   * Returns the <code>index</code>th element of the vector.
   */

  public double get(int index);

    /**
     * return the indexth element of the vector without an bound check.
     * @param index index
     * @return  the element
     */
    public double quickGet(int index);


  /**
   * Assigns a new value <code>val</code> to the element <code>index</code> and returns
   * the old value
   */

  public double set(int index, double val);

    /**
     * Unsafe setter => don't do bound verification
     * @param index the index of the replaced value
     * @param val the new value
     * @return the old value
     */
  public double quickSet(int index, double val);

  public DisposableIntIterator getIterator();
}
