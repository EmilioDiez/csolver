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

import static csolver.cp.solver.search.BranchingFactory.domDDeg;
import static csolver.cp.solver.search.BranchingFactory.domDDegBin;
import static csolver.cp.solver.search.BranchingFactory.domDeg;
import static csolver.cp.solver.search.BranchingFactory.domDegBin;
import static csolver.cp.solver.search.BranchingFactory.domWDeg;
import static csolver.cp.solver.search.BranchingFactory.domWDegBin;
import static csolver.cp.solver.search.BranchingFactory.incDomWDeg;
import static csolver.cp.solver.search.BranchingFactory.incDomWDegBin;
import static csolver.cp.solver.search.BranchingFactory.minDomMinVal;
import static csolver.cp.solver.search.BranchingFactory.randomIntBinSearch;
import static csolver.cp.solver.search.BranchingFactory.randomIntSearch;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.plus;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.kernel.CSolver;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.search.ValSelector;
import nitoku.log.Logger;

public class BranchingTest {
	public static final int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

	private Model m;
	private Solver s;

	private IntegerVariable[] queens;

	private void buildQueen(int n) {
		Logger.config(BranchingTest.class, "n queens, binary model, n=" + n);
		if( queens == null || queens.length != n) {
			m = new CPModel();
			// create variables
			queens = CSolver.makeIntVarArray("Q", n, 1, n);
			// diagonal constraints
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					int k = j - i;
					m.addConstraint(neq(queens[i], queens[j]));
					m.addConstraint(neq(queens[i], plus(queens[j], k)));
					m.addConstraint(neq(queens[i], minus(queens[j], k)));
				}
			}
		}
	}

	private void checkResolution() {
		final int n =queens.length;
		if(n < nbQueensSolution.length) {
			if( nbQueensSolution[n] == 0) assertEquals(Boolean.FALSE, s.isFeasible());
			else assertEquals(Boolean.TRUE, s.isFeasible());
			assertEquals(nbQueensSolution[n], s.getNbSolutions());
		} else assertEquals(Boolean.TRUE, s.isFeasible());
	}

	private final static int NB_BRANCHING = 13;
	
	private void setBranching(int type) {
		s = new CPSolver();
		s.read(m);
		final ValSelector valsel = new RandomIntValSelector(type);
		switch (type) {
		case 0:	s.attachGoal(randomIntBinSearch(s, type));
		case 1:	s.attachGoal(randomIntSearch(s, type));
		case 2:s.attachGoal(domWDeg(s));break;
		case 3:s.attachGoal(domWDegBin(s));break;
		case 4:s.attachGoal(incDomWDeg(s));break;
		case 5:s.attachGoal(incDomWDegBin(s));break;
		case 6:s.attachGoal(domWDeg(s, valsel));break;
		case 7:s.attachGoal(domWDegBin(s, valsel));break;
		case 8:s.attachGoal(incDomWDegBin(s, valsel));break;
		case 9:s.attachGoal(domDegBin(s, valsel));break;
		case 10:s.attachGoal(domDeg(s, valsel));break;
		case 11:s.attachGoal(domDDegBin(s, valsel));break;
		case 12:s.attachGoal(domDDeg(s, valsel));break;
		default: s.attachGoal(minDomMinVal(s));break;
		}
	}

	private void testQueen(int n) {
		buildQueen(n);
		for (int i = 0; i < NB_BRANCHING; i++) {
			setBranching(i);
			s.solveAll();
			checkResolution();
		}

	}
	@Test
	public void testQueen4() {
		testQueen(4);
	}

	@Test
	public void testQueen5() {
		testQueen(5);
	}

	@Test
	public void testQueen6() {
		testQueen(6);
	}

	@Test
	public void testQueen7() {
		testQueen(7);
	}

	@Test
	public void testQueen8() {
		testQueen(8);
	}

	@Test
	public void testQueen9() {
		testQueen(9);
	}

	@Test
	public void testQueen10() {
		testQueen(10);
	}
	
	@Test
	public void testQueen11() {
		testQueen(11);
	}
}
