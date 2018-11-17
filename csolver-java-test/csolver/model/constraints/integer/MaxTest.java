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

package csolver.model.constraints.integer;

import static csolver.kernel.CSolver.constantArray;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.leqCard;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.makeIntVarArray;
import static csolver.kernel.CSolver.makeSetVar;
import static csolver.kernel.CSolver.max;
import static csolver.kernel.CSolver.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.cp.solver.search.integer.varselector.RandomIntVarSelector;
import csolver.kernel.Options;
import csolver.kernel.common.util.tools.MathUtils;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.SolverException;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.set.SetVar;
import nitoku.log.Logger;

public class MaxTest  {

	private CPModel m;
	private CPSolver s;

	@Before
	public void before(){
		m = new CPModel();
		s = new CPSolver();
	}

	@After
	public void after(){
		m = null;
		s = null;
	}

	@Test
	public void test1() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			IntegerVariable w = makeIntVar("w", 1, 5);
			m.addConstraint(max(new IntegerVariable[]{x, y, z},w));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.solve();
			do {
				/*Logger.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
			} while (s.nextSolution() == Boolean.TRUE);
			Logger.info("" + s.getSearchStrategy().getNodeCount());
			assertEquals(125, s.getNbSolutions());
			//Logger.info("Nb solution : " + s.getNbSolutions());
		}
	}



	@Test
	public void test2() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			m.addVariables(Options.V_BOUND, x, y, z);
			IntegerVariable w = makeIntVar("z", 1, 5);
			m.addConstraint(max(new IntegerVariable[]{x, y, z},w));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.solveAll();
			assertTrue(s.isFeasible());
			assertEquals(125, s.getNbSolutions());
			//Logger.info("Nb solution : " + s.getNbSolutions());
		}
	}

	@Test
	public void test2bis() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			m.addVariables(Options.V_BOUND, x, y, z);
			m.addConstraint(max(y, z, x));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.solve();
			do {
				//Logger.info("" + x.getVal() + "=max(" + y.getVal() + "," +
				//�    z.getVal()+")");
			} while (s.nextSolution() == Boolean.TRUE);
			Logger.info("" + s.getSearchStrategy().getNodeCount());
			assertEquals(25, s.getNbSolutions());
			//Logger.info("Nb solution : " + s.getNbSolutions());
		}
	}


	@Test
	public void test3() {
		Random rand = new Random();
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable(Options.V_BOUND, x);
			}
			IntegerVariable y = makeIntVar("y", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable(Options.V_BOUND, y);
			}
			IntegerVariable z = makeIntVar("z", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable(Options.V_BOUND, z);
			}

			m.addConstraint(max(new IntegerVariable[]{y, z}, x));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.solve();
			do {
				/*Logger.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
			} while (s.nextSolution() == Boolean.TRUE);
			assertEquals(25, s.getNbSolutions());
			//Logger.info("Nb solution : " + s.getNbSolutions());
		}
	}

	@Test
	public void testPropagMaxTern1() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 2);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(max).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max " + s.getVar(max).getDomain().pretty());
		Logger.info("y "+ s.getVar(y).getDomain().pretty());
		Logger.info("z "+ s.getVar(z).getDomain().pretty());
		Logger.info("" + !s.getVar(y).canBeInstantiatedTo(3));
		assertTrue(!s.getVar(y).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern2() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 5);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(y).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max "+ s.getVar(max).getDomain().pretty());
		Logger.info("y " + s.getVar(y).getDomain().pretty());
		Logger.info("z " + s.getVar(z).getDomain().pretty());
		Logger.info(""+ (s.getVar(z).canBeInstantiatedTo(3) && s.getVar(max).canBeInstantiatedTo(3)));
		assertTrue(s.getVar(z).canBeInstantiatedTo(3) && s.getVar(max).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern3() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 5);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(max).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max " + s.getVar(max).getDomain().pretty());
		Logger.info("y "+ s.getVar(y).getDomain().pretty());
		Logger.info("z "+ s.getVar(z).getDomain().pretty());
		Logger.info(""+ (s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3)));
		assertTrue(s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern4() {
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 4, 6);
		IntegerVariable max = makeIntVar("max", 1, 6);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max "+ s.getVar(max).getDomain().pretty());
		Logger.info("y "+ s.getVar(y).getDomain().pretty());
		Logger.info("z "+ s.getVar(z).getDomain().pretty());
		Logger.info(""+ (s.getVar(max).getDomain().getSize() == 3));
		assertTrue(s.getVar(max).getDomain().getSize() == 3);
	}

	@Test
	public void testPropagMaxTern5() {
		IntegerVariable y = makeIntVar("y", 1, 4);
		IntegerVariable z = makeIntVar("z", 4, 8);
		IntegerVariable max = makeIntVar("max", 1, 8);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(z).remVal(5);
			s.getVar(max).remVal(8);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max "+ s.getVar(max).getDomain().pretty());
		Logger.info("y "+ s.getVar(y).getDomain().pretty());
		Logger.info("z "+ s.getVar(z).getDomain().pretty());
		Logger.info(""+ (s.getVar(max).getDomain().getSize() == 3));
		assertEquals(s.getVar(max).getDomain().getSize(),3);
		assertEquals(s.getVar(z).getDomainSize(),3);
	}

	@Test
	public void testRandom() {
		for (int i = 0; i < 10; i++) {

			m = new CPModel();
			s= new CPSolver();


			IntegerVariable varA = makeIntVar("varA", 0, 3);
			IntegerVariable varB = makeIntVar("varB", 0, 3);
			IntegerVariable varC = makeIntVar("varC", 0, 3);
			m.addConstraint(max(varA, varB, varC));

			//-----Now get solutions

			s.read(m);
			s.setFirstSolution(true);
			s.generateSearchStrategy();
			s.setValIntSelector(new RandomIntValSelector(100 + i));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));


			//Logger.info("Choco Solutions");
			int nbSolution = 0;
			if (s.solve() == Boolean.TRUE) {
				do {
					//Logger.info("Max(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
					nbSolution++;
				} while (s.nextSolution() == Boolean.TRUE);
			}

			assertEquals(nbSolution, 16);
		}
	}

	@Test
	public void testConstant(){
		for (int i = 0; i < 10; i++) {

			m = new CPModel();
			s= new CPSolver();


			IntegerVariable x = makeIntVar("x", 0, 3);
			IntegerVariable y = makeIntVar("y", 0, 3);
			m.addConstraint(eq(y, max(x, 1)));

			//-----Now get solutions

			s.read(m);
			s.setFirstSolution(true);
			s.generateSearchStrategy();
			s.setValIntSelector(new RandomIntValSelector(100 + i));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));


			//Logger.info("Choco Solutions");
			int nbSolution = 0;
			if (s.solve() == Boolean.TRUE) {
				do {
					//Logger.info("Max(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
					nbSolution++;
				} while (s.nextSolution() == Boolean.TRUE);
			}

			assertEquals(nbSolution, 4);
		}
	}


	public final static int NB_ITERATION=1;

	protected static void testAll(boolean minOrMax, boolean bounded) {
		testAll(minOrMax,NB_ITERATION,3,2,bounded);
		testAll(minOrMax,NB_ITERATION,5,3,bounded);
		testAll(minOrMax,NB_ITERATION,4,6,bounded);
	}

	protected static void testAll(boolean minOrMax,int nbIter, int nbVars,int domSize, boolean bounded) {
		Logger.info("%%%%%%% TEST MIN/MAX %%%%%%%%%%%%%%");
		CPModel m = new CPModel();
		SetVariable set  = makeSetVar("set", 0, nbVars-1);
		IntegerVariable[] vars = makeIntVarArray("v",nbVars,1, domSize);
		if(bounded) {m.addVariables(Options.V_BOUND, vars);}
		IntegerVariable w = makeIntVar("bound", 1, domSize);
		IntegerVariable c  = makeIntVar("card", 0, nbVars+1);
		Constraint ccard = eq(c, 0);
		m.addConstraint(ccard);
		m.addConstraint( minOrMax ?
				min(set,vars,w) :
					max(set,vars,w)
		);

		m.addConstraint(leqCard(set,c));
		int sum=0;
		for (int k = 0; k < nbVars+1; k++) {
			m.remove(ccard);
			ccard = eq(c, k);
			m.addConstraint(ccard);
			int nbSets = MathUtils.combinaison(nbVars, k);
			int nbAssign = (int) Math.pow(domSize, k==0 ? nbVars+1 : nbVars);
			sum += nbSets*nbAssign;
			Logger.info("NB solutions : "+sum);
			for (int i = 0; i < nbIter; i++) {
				CPSolver s = new CPSolver();
				s.read(m);
				s.setRandomSelectors();
				//CPSolver.setVerbosity(CPSolver.SEARCH);
				//s.setLoggingMaxDepth(4);
				s.solveAll();
				assertEquals("set of cardinality <= "+k,sum, s.getNbSolutions());

			}
		}
	}

	@Test(expected=SolverException.class)
	public void badSetArg() {
		m = new CPModel();
		SetVariable set  = makeSetVar("set", 0, 4);
		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
		IntegerVariable w = makeIntVar("bound", 1, 2);
		m.addConstraint(min(set,new IntegerVariable[]{x, y, z},w));
		s.read(m);
	}




	@Test
	public void testSet1() {
		testAll(false,true);
	}

	@Test
	public void testSet2() {
		testAll(false,false);
	}
	
	@Test
	public void testEmptySetDefValue() {
		IntegerVariable[] vars = constantArray(new int[]{1,2,3});
        IntegerVariable min = makeIntVar("min", 0, 3);
        SetVariable svar = makeSetVar("sv", 0, 2);
        m.addConstraint(max(svar, vars, min, Options.C_MINMAX_SUP));
        s.read(m);
        s.solveAll();
        assertEquals("nb-sols", 8, s.getNbSolutions());        
	}

	@Test
	public void testOneVarMax() {
		IntegerVariable[] vars = makeIntVarArray("vars", 1, 3, 5);
		IntegerVariable max = makeIntVar("min", 1, 6);
		m.addConstraint(eq(max, max(vars)));
		s.read(m);
		try {
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		Logger.info("max " + s.getVar(max).getDomain().pretty());
		assertTrue(s.getVar(max).getDomain().getSize() == 3);
	}
	
	private void checkVar(IntDomainVar mv, int inf, int sup) {
		Logger.info(mv.pretty()); 
		assertEquals(inf, mv.getInf());
		assertEquals(sup, mv.getSup());
		assertEquals(sup-inf+1, mv.getDomainSize());

	}

	@Test
	public void testSetEvents() throws ContradictionException {
		final IntegerVariable[] vars = new IntegerVariable[]{
				makeIntVar("v1", 0, 20),
				makeIntVar("v2", 5, 10),
				makeIntVar("v3", 3, 8),
				makeIntVar("v4", 15, 18)        	
		};
		final IntegerVariable min = makeIntVar("min", -10, 25);
		final SetVariable svar = makeSetVar("sv", 0, 3);
		final Constraint c = max(svar, vars, min);
		m.addConstraint(c);
		s.read(m);
		s.propagate();

		final IntDomainVar mv = s.getVar(min);
		final SetVar sv = s.getVar(svar);

		Logger.setLevel(Level.INFO);
		//checkMin(mv, -1, 20);

		sv.addToKernel(2, null, false);
		s.propagate();
		checkVar(mv, 3, 20);
		sv.remFromEnveloppe(0, null, false);
		s.propagate();
		checkVar(mv, 3, 18);
		mv.updateSup(13, null, true);
		s.propagate();
		assertFalse(sv.isInDomainEnveloppe(3));
		s.getVar(vars[1]).updateSup(9, null, true);
		s.propagate();
		checkVar(mv, 3, 9);
		mv.updateInf(9, null, true);
		s.propagate();
		checkVar(mv, 9, 9);
		assertTrue(sv.isInDomainKernel(1));
		Logger.info(s.getCstr(c).pretty());
	};
	
	
	@Test
	public void testBug_tlapeg07() {
		int[] indexes = new int[]{}; 
		SetVariable set = makeSetVar("set", indexes); 
		int[] values = new int[]{5, 10, 15}; 
		IntegerVariable max = makeIntVar("max", values, "cp:enum"); 
		CPModel mod = new CPModel(); 
		mod.addConstraint(max(set, constantArray(values), max)); 
		CPSolver s = new CPSolver(); 
		s.read(mod); s.solve(); 
		//buggy versions throws :
		//Exception in thread "main" choco.kernel.solver.SolverException: 
		//The enveloppe of the set variable set {Env[], Ker[]} is greater than the array
	}

}
