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

package csolver.cp.model.managers.constraints.integer;

import csolver.cp.model.managers.IntConstraintManager;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.integer.ModuloXYC2;
import csolver.cp.solver.constraints.reified.leaves.arithm.ModNode;
import csolver.kernel.model.ModelException;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.reified.INode;

import java.util.List;

/* 
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 6, 2008
 * Since : Choco 2.0.0
 *
 */
public final class ModuloManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] vars, Object parameters, List<String> options) {
        if (solver instanceof CPSolver) {
            if (vars.length == 3) {
                return new ModuloXYC2(solver.getVar(vars[0]), solver.getVar(vars[1]), vars[2].getLowB());
            }
        }
        return null;

    }

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param vars
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        if(solver instanceof CPSolver){
            CPSolver s = (CPSolver)solver;
            if(vars.length == 2){
                INode[] nodes = new INode[vars.length];
                for(int i = 0; i < vars.length; i++){
                    nodes[i] = vars[i].getExpressionManager().makeNode(s, vars[i].getConstraints(), vars[i].getVariables());
                }
                return new ModNode(nodes);
            }else{
                return super.makeNode(solver, cstrs, vars);
            }
        }
        throw new ModelException("Could not found a node manager in " + this.getClass() + " !");
    }

}
