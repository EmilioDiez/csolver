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

package csolver.model.constraints.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.common.util.tools.MathUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;

public class TestInverseSet {


	private final static int N = 12;
	private final static int NB_TESTS = 5;
	protected Model m;
	protected CPSolver s;
	protected SetVariable s1 = CSolver.makeSetVar("s1", 0, N);
	protected SetVariable s2 = CSolver.makeSetVar("s2", 0, N);
	protected SetVariable s3 = CSolver.makeSetVar("s3", 0, N);
	protected SetVariable s4 = CSolver.makeSetVar("s3", 0, N);

	protected IntegerVariable[] iv = CSolver.makeIntVarArray("v", N+1,0,2);
	
	@Before
	public void initialize() {
		m = new CPModel();
		s = new CPSolver();
	}

	@Test
	public void testBadArgs() {
		m.addConstraint( CSolver.inverseSet(CSolver.makeIntVarArray("v", 2*N,0,2), new SetVariable[]{s1, s2, s3}));
		s.read(m);
		assertFalse("Bad Args", s.solve());
	}

	@Test
	public void testNbSolutions() {
		m.addConstraints( 
				CSolver.inverseSet( iv, new SetVariable[]{s1, s2, s3}),
				CSolver.eqCard(s1, N/3),
				CSolver.eqCard(s2, N/4),
				CSolver.eqCard(s3, N - N/3 - N/4 + 1)
		);
		for (int i = 0; i < NB_TESTS; i++) {
			s.read(m);
			s.setRandomSelectors(i);
			assertTrue("sat", s.solveAll());
			// nbSubsetN/3 * nbSubsetN/4
			int nbsols = MathUtils.combinaison(N + 1, N/3) * MathUtils.combinaison(N + 1 - N/3 , N/4); 
			assertEquals("nbsols", nbsols, s.getSolutionCount());
			s.clear();
		}
	}
	
	@Test
	public void testUnsatCSP() {
		m.addConstraints( 
				CSolver.inverseSet( iv, new SetVariable[]{s1, s2, s3}),
				CSolver.setInter(s1, s2, s4),
				CSolver.geqCard(s4, 1)
		);
		for (int i = 0; i < NB_TESTS; i++) {
			s.read(m);
			s.setRandomSelectors(i);
			assertFalse("Unsat", s.solve());
			s.clear();
		}
	}

}
