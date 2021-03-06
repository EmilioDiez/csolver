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

package csolver.cp.solver.search.integer.varselector;

import csolver.kernel.solver.branch.ConstraintSelector;
import csolver.kernel.solver.branch.VarSelector;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.integer.AbstractIntSConstraint;
import csolver.kernel.solver.search.integer.HeuristicIntVarSelector;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class that composes two heuristics for selecting a variable:
 * a first heuristic is appled for selecting a constraint.
 * from that constraint a second heuristic is applied for selecting the variable
 */
public class CompositeIntVarSelector implements VarSelector<IntDomainVar> {
    protected ConstraintSelector cs;
    protected HeuristicIntVarSelector cvs;

    public CompositeIntVarSelector(ConstraintSelector cs, HeuristicIntVarSelector cvs) {
        this.cs = cs;
        this.cvs = cvs;
    }

    public IntDomainVar selectVar() {
        SConstraint<?> c = cs.getConstraint();
        if (c == null) return null;
        else return cvs.getMinVar((AbstractIntSConstraint) c);
    }

    public ConstraintSelector getCs() {
        return cs;
    }

    public HeuristicIntVarSelector getCvs() {
        return cvs;
    }
    
}
