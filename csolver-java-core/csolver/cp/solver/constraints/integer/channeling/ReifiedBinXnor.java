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

package csolver.cp.solver.constraints.integer.channeling;

import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/**
 * A constraint that ensures :
 * b = x1 xnor x2 ... Or xn
 * where b, and x1,... xn are boolean variables (of domain {0,1})
 */
public final class ReifiedBinXnor extends AbstractTernIntSConstraint {

     /**
     * A constraint to ensure :
     * b = v1 xnor v2
     */
    public ReifiedBinXnor(IntDomainVar b, IntDomainVar v1, IntDomainVar v2) {
        super(b, v1, v2);
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINT_MASK;
    }

    public void propagate() throws ContradictionException {
        if (v0.isInstantiated()) {
            filter0();
        } else {
            if(v1.isInstantiated())filter(v1.getVal(), v2);
            if(v2.isInstantiated())filter(v2.getVal(), v1);
        }
    }

    private void filter0() throws ContradictionException {
        switch (v0.getVal()) {
            case 0:
                if (v1.isInstantiated()) {
                    v2.instantiate(Math.abs(v1.getVal() - 1), this, false);
                } else if (v2.isInstantiated()) {
                    v1.instantiate(Math.abs(v2.getVal() - 1), this, false);
                }
                break;
            case 1:
            if (v1.isInstantiated()) {
                    v2.instantiate(v1.getVal(), this, false);
                } else if (v2.isInstantiated()) {
                    v1.instantiate(v2.getVal(), this, false);
                }
            break;
        }
    }

    private void filter(int val, IntDomainVar v) throws ContradictionException {
        switch (val){
            case 0:
                if (v.isInstantiated()) {
                    v0.instantiate(Math.abs(v.getVal() - 1), this, false);
                }
                break;
            case 1:
                if (v.isInstantiated()) {
                    v0.instantiate(v.getVal(), this, false);
                }
                break;
        }
    }


    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        switch (idx) {
            case 0:
                filter0();
                break;
            case 1:
                filter(v1.getVal(), v2);
                break;
            case 2:
                filter(v2.getVal(), v1);
                break;
        }
    }

    @Override
    public void awakeOnInf(int varIdx) throws ContradictionException {

    }
    @Override
    public void awakeOnSup(int varIdx) throws ContradictionException {
    }
    @Override
    public void awakeOnBounds(int varIndex) throws ContradictionException {
    }
    @Override
    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    }


    public boolean isSatisfied(int[] tuple) {
        if (tuple[0] == 1) {
            return tuple[1] == tuple[2];
        } else {
            return tuple[1] != tuple[2];
        }
    }

    public Boolean isEntailed() {
        if(v0.isInstantiatedTo(1)
                && v1.isInstantiated() && v2.isInstantiated()){
            return v1.getVal() == v2.getVal();
        }else if(v0.isInstantiatedTo(0)){
            return v1.getVal() != v2.getVal();
        }
        return null;
    }

}