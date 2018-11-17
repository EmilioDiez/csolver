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
package csolver.cp.model.managers.variables;

import csolver.cp.solver.CPSolver;
import csolver.kernel.model.ModelException;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.model.variables.VariableManager;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.reified.INode;
import csolver.kernel.solver.variables.Var;

/* User:    charles
 * Date:    21 août 2008
 */
public final class TaskVariableManager implements VariableManager<TaskVariable> {


    /**
     * Build a set variable for the given solver
     * @param solver
     * @param var
     * @return a set variable
     */
    public Var makeVariable(Solver solver, TaskVariable var) {
        if(solver instanceof CPSolver){
            return solver.createTaskVar(var.getName(), solver.getVar(var.start()),
                    solver.getVar(var.end()), solver.getVar(var.duration()));
        }
        throw new ModelException("Could not found a variable manager in " + this.getClass() + " !");
    }

    /**
     * Build a expression node
     *
     * @param solver
     * @param cstrs  constraints
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        return null;
    }
}
