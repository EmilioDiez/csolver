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

package csolver.cp.solver.constraints.set;

import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.cp.solver.variables.set.SetVarEvent;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.AbstractSConstraint;
import csolver.kernel.solver.constraints.set.AbstractBinSetIntSConstraint;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.set.SetVar;

/**
 * Ensure that an int variable does not belong to a set variable
 */
public final class NotMemberXY extends AbstractBinSetIntSConstraint {

    public NotMemberXY(final SetVar set, final IntDomainVar iv) {
        super(iv, set);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        if (idx == 0) {
            return IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK + IntVarEvent.REMVAL_MASK;
        }
        return SetVarEvent.ADDKER_MASK + SetVarEvent.REMENV_MASK + SetVarEvent.INSTSET_MASK;
    }

    /**
     * if only one value out of the kernel, then this value can be removed from the enveloppe
     *
     * @throws ContradictionException
     */
    public void filter() throws ContradictionException {
        final DisposableIntIterator it = v0.getDomain().getIterator();
        try {
            int count = 0, val = Integer.MAX_VALUE;
            while (it.hasNext()) {
                val = it.next();
                if (!v1.isInDomainKernel(val)) {
                    count += 1;
                    if (count > 1) break;
                }
            }
            if (count == 0)
                this.fail();
            else if (count == 1) {
                v0.instantiate(val, this, false);
                v1.remFromEnveloppe(val, this, false);
            }
        } finally {
            it.dispose();
        }
    }

    public void awakeOnInf(final int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnSup(final int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnRem(final int idx, final int x) throws ContradictionException {
        filter();
    }

    public void awakeOnKer(final int varIdx, final int x) throws ContradictionException {
        v0.removeVal(x, this, false);
        filter();
    }

    public void awakeOnEnv(final int varIdx, final int x) throws ContradictionException {
        filter();
    }

    public void awakeOnInst(final int varIdx) throws ContradictionException {
        if (varIdx == 0)
            v1.remFromEnveloppe(v0.getVal(), this, false);
        else {
            int left = Integer.MIN_VALUE;
            int right = left;
            final DisposableIntIterator it = v1.getDomain().getKernelIterator();
            try {
                while (it.hasNext()) {
                    int val = it.next();
                    if (val == right + 1) {
                        right = val;
                    } else {
                        v0.removeInterval(left, right, this, false);
                        left = val;
                        right = val;
                    }
//                    v0.removeVal(it.next(), this, false);
                }
                v0.removeInterval(left, right, this, false);
            } finally {
                it.dispose();
            }
            filter();
        }
    }


    public void propagate() throws ContradictionException {
        int left = Integer.MIN_VALUE;
        int right = left;
        final DisposableIntIterator it = v1.getDomain().getKernelIterator();
        try {
            while (it.hasNext()) {
                int val = it.next();
                if (val == right + 1) {
                    right = val;
                } else {
                    v0.removeInterval(left, right, this, false);
                    left = val;
                    right = val;
                }
//                v0.removeVal(it.next(), this, false);
            }
            v0.removeInterval(left, right, this, false);
        } finally {
            it.dispose();
        }
        filter();
    }

    public boolean isSatisfied() {
        return !v1.isInDomainKernel(v0.getVal());
    }

    public boolean isConsistent() {
        final DisposableIntIterator it = v0.getDomain().getIterator();
        while (it.hasNext()) {
            if (v1.isInDomainKernel(it.next())) {
                it.dispose();
                return false;
            }
        }
        it.dispose();
        return true;
    }

    public String toString() {
        return v0 + " is not in " + v1;
    }

    public String pretty() {
        return v0.pretty() + " is not in " + v1.pretty();
    }

    public Boolean isEntailed() {
        boolean allInKernel = true;
        boolean allOutEnv = true;
        final DisposableIntIterator it = v0.getDomain().getIterator();
        while (it.hasNext()) {
            final int val = it.next();
            if (!v1.isInDomainKernel(val)) {
                allInKernel = false;
            }
            if (v1.isInDomainEnveloppe(val)) {
                allOutEnv = false;
            }
        }
        it.dispose();
        if (allInKernel) {
            return Boolean.FALSE;
        } else if (allOutEnv) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public AbstractSConstraint<Var> opposite(Solver solver) {
        return new MemberXY(v1, v0);
    }
}
