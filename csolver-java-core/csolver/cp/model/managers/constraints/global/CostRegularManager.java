/**
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
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

package csolver.cp.model.managers.constraints.global;

import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;

import csolver.cp.model.managers.IntConstraintManager;
import csolver.cp.solver.constraints.global.automata.fast_costregular.CostRegular;
import csolver.kernel.model.constraints.automaton.FA.IAutomaton;
import csolver.kernel.model.constraints.automaton.FA.ICostAutomaton;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.global.automata.fast_costregular.structure.Arc;
import csolver.kernel.solver.constraints.global.automata.fast_costregular.structure.Node;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;



public final class CostRegularManager extends IntConstraintManager {

public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {

        if (parameters instanceof Object[] && ((Object[])parameters).length == 2)
        {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));

                IAutomaton auto = null;
                DirectedMultigraph<Node, Arc> graph = null;
                Node source = null;
                Object[] tmp = (Object[]) parameters;
                try {
                        auto = (IAutomaton)tmp[0];
                        try {
                                int[][][]csts = (int[][][])tmp[1];
                                return new CostRegular(vars,auto,csts,solver);
                        }
                        catch (Exception e)
                        {
                                int[][] csts = (int[][])tmp[1];
                                return new CostRegular(vars,auto,csts,solver);
                        }
                }
                catch (Exception e)
                {
                        try {
                                graph = (DirectedMultigraph<Node,Arc>) tmp[0];
                                source = (Node) tmp[1];
                        }
                        catch (Exception e2)
                        {
                                Logger.severe(CostRegularManager.class,"Invalid parameters in costregular manager");
                                return null;
                        }
                }

                return new CostRegular(vars,graph,source, solver);
        }
        else if (parameters instanceof ICostAutomaton)
        {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));
                return new CostRegular(vars,(ICostAutomaton)parameters,solver);
        }
        return null;

}
}
