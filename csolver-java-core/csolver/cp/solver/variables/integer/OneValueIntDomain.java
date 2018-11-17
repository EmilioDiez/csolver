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

package csolver.cp.solver.variables.integer;

import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.common.util.iterators.OneValueIterator;
import csolver.kernel.solver.propagation.PropagationEngine;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 31 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
final class OneValueIntDomain extends AbstractIntDomain {

    private final int value;

    private final boolean isBoolean;

    OneValueIntDomain(final IntDomainVar aVariable, final int theValue, final PropagationEngine propagationEngine) {
        super(aVariable, propagationEngine);
        value = theValue;
        isBoolean = (value == 0 || value == 1);
    }


    /**
     * Access the minimal value stored in the domain.
     */
    @Override
    public int getInf() {
        return value;
    }

    /**
     * Access the maximal value stored in the domain/
     */
    @Override
    public int getSup() {
        return value;
    }

    /**
     * Augment the minimal value stored in the domain.
     * returns the new lower bound (<i>x</i> or more, in case <i>x</i> was
     * not in the domain)
     */
    @Override
    public int updateInf(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Diminish the maximal value stored in the domain.
     * returns the new upper bound (<i>x</i> or more, in case <i>x</i> was
     * not in the domain).
     */
    @Override
    public int updateSup(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Testing whether an search value is contained within the domain.
     */
    @Override
    public boolean contains(final int x) {
        return x == value;
    }

    /**
     * Removing a single value from the domain.
     */
    @Override
    public boolean remove(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Restricting the domain to a singleton
     */
    @Override
    public void restrict(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Access the total number of values stored in the domain.
     */
    @Override
    public int getSize() {
        return 1;
    }

    public DisposableIntIterator getIterator() {
        return OneValueIterator.getIterator(value);
    }

    /**
     * Accessing the smallest value stored in the domain and strictly greater
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public int getNextValue(final int x) {
        if (x < value) {
            return value;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Accessing the largest value stored in the domain and strictly smaller
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public int getPrevValue(final int x) {
        if (x > value) {
            return value;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Testing whether there are values in the domain that are strictly greater
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public boolean hasNextValue(final int x) {
        return x < value;
    }

    /**
     * Testing whether there are values in the domain that are strictly smaller
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public boolean hasPrevValue(final int x) {
        return x > value;
    }

    /**
     * Draws a value at random from the domain.
     */
    @Override
    public int getRandomValue() {
        return value;
    }

    @Override
    public boolean isEnumerated() {
        return true;
    }

    /**
     * Is it a 0/1 domain ?
     */
    @Override
    public boolean isBoolean() {
        return isBoolean;
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return String.valueOf('[' + value + ']');
    }
}
