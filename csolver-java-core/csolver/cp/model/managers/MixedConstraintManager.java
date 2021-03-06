/*
 *  Javascript Constraint Solver (Csolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *  Choco Copyright (c) 1999-2010-2011, Ecole des Mines de Nantes
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
package csolver.cp.model.managers;

import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.reified.leaves.ConstraintLeaf;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.constraints.ConstraintManager;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.reified.INode;

import java.util.List;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* A constraint manager for mixed variable type managers (like EqManager).
* It can deal with different types of variable.
*/
public abstract class MixedConstraintManager  extends ConstraintManager<Variable> {

    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters
     * @param options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
    @Override
    public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, List<String> options) {
        SConstraint c = makeConstraint(solver, variables, parameters, options);
        SConstraint opp = c.opposite(solver);
        return new SConstraint[]{c, opp};
    }

    /**
     * @param options : the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    @Override
    public int[] getFavoriteDomains(List<String> options) {
        return ConstraintManager.getACFavoriteIntDomains();
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return
     */
    @Override
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        return new ConstraintLeaf(((CPSolver)solver).makeSConstraint(cstrs[0]), null);
    }
}
