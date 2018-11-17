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

package csolver.solver.search;

import static csolver.kernel.CSolver.allDifferent;
import static csolver.kernel.CSolver.makeIntVarArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.configure.LimitFactory;
import csolver.cp.solver.search.SearchLimitManager;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.Options;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.search.limit.Limit;
import nitoku.log.Logger;

public class LimitTest {
    

	public final static int SIZE=100;

	public CPModel model;

	public CPSolver solver;

	@Before
	public void initialize() {
		Logger.info(LimitTest.class, "here 1");
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		model=new CPModel();
		IntegerVariable[] vars=makeIntVarArray("v", SIZE, 0, SIZE);
		model.addConstraint(Options.C_ALLDIFFERENT_AC,allDifferent(vars));
		solver=new CPSolver();
		solver.read(model);
        solver.attachGoal(new AssignVar(new MinDomain(solver), new IncreasingDomain()));
	}


	private void check(Limit type) {
		solver.solveAll();
		assertTrue(solver.isEncounteredLimit());
		assertEquals(type, solver.getEncounteredLimit().getType());
	}

	@Test
	public void testNodeLimit() {
		
		Logger.info(LimitTest.class, "here 2");
		
		solver.setNodeLimit(SIZE*2);
		check(Limit.NODE);
		solver.getBackTrackCount();

	}

	@Test
	public void testBacktrackLimit() {
		solver.setBackTrackLimit(SIZE * 2);
		check(Limit.BACKTRACK);
	}

	@Test
	public void testFailTimeLimit() {
        solver.setFailLimit(SIZE/10);
		check(Limit.FAIL);
	}


	@Test
	public void testTimeLimit() {
		solver.setTimeLimit(SIZE*10);
		check(Limit.TIME);
	}

    @Test
	public void testTimeLimit2() {
		solver.setTimeLimit(SIZE*10);
        solver.setFirstSolution(false);
        solver.generateSearchStrategy();
        SearchLimitManager slm = (SearchLimitManager)solver.getSearchStrategy().getLimitManager();
        slm.getSearchLimit().setNbMax(10);
        solver.launch();
        assertTrue(solver.isEncounteredLimit());
		assertEquals(Limit.TIME, solver.getEncounteredLimit().getType());
	}
	
	@Test
	public void testRestartLimit1() {
		final int lim =7;
		solver.setTimeLimit(SIZE*20);
		solver.setRestart(true);
		solver.setRestartLimit(lim);
		check(Limit.TIME);
		assertEquals(lim, solver.getRestartCount());
	}

	@Test
	public void testRestartLimit2() {
		final int lim =3;
		solver.setTimeLimit(SIZE*20);
		solver.setRestartLimit(lim);
		solver.setLubyRestart(1, 2, lim);
		check(Limit.TIME);
		assertEquals(lim, solver.getRestartCount());
	}
	
	@Test
	public void testRestartLimit3() {
		solver.setTimeLimit(SIZE*20);
		LimitFactory.setRestartLimit(solver, Limit.NODE, 2);
		solver.setLubyRestart(1, 2);
		solver.setLubyRestart(1, 3);
		check(Limit.TIME);
		assertEquals(1, solver.getRestartCount());
	}
	

}
