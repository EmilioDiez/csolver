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
package csolver.solver.goals;

import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.goals.choice.Generate;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import nitoku.log.Logger;

public class SearchTest {
	
	

	@Test
	public void testnode() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
        int n1 = testNQueens(true);
        int n2 = testNQueens(false);
        assertEquals("Nb Nodes", n1 ,n2);
	}

	//return the number of nodes needed to solve the problem
	private int testNQueens(boolean withgoal) {
		int NB_REINES = 8;

		Model m = new CPModel();


		IntegerVariable[] vars = new IntegerVariable[NB_REINES];
		for (int i = 0; i < NB_REINES; i++) {
			vars[i] = makeIntVar("x" + i, 0, NB_REINES - 1);
		}

		for (int i = 0; i < NB_REINES; i++) {
			for (int j = i + 1; j < NB_REINES; j++) {
				m.addConstraint(neq(vars[i], vars[j]));
			}                                                                                             
		}

		for (int i = 0; i < NB_REINES; i++) {
			for (int j = i + 1; j < NB_REINES; j++) {
				int k = j - i;
				m.addConstraint(neq(vars[i], plus(vars[j], k)));
				m.addConstraint(neq(vars[i], minus(vars[j], k)));
			}
		}

		Solver s = new CPSolver();
		s.read(m);
		if (withgoal) {
            s.setIlogGoal(new Generate(s.getVar(vars)));
        }else{
            s.addGoal(new AssignVar(new MinDomain(s), new IncreasingDomain()));
        }

		s.solveAll();
		Logger.info("Nb solutions = " + s.getNbSolutions());
		return s.getNodeCount();
	}

}