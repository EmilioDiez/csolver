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

package csolver.scheduling;

import static csolver.kernel.CSolver.constantArray;
import static csolver.kernel.CSolver.disjunctive;
import static csolver.kernel.CSolver.makeBooleanVarArray;
import static csolver.kernel.CSolver.makeTaskVar;
import static csolver.kernel.CSolver.makeTaskVarArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive;
import csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.Policy;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.bitmask.BitMask;
import csolver.kernel.common.util.tools.MathUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.util.scheduling.AbstractTestProblem;
import csolver.util.scheduling.DisjProblem;
import csolver.util.scheduling.SchedUtilities;
import nitoku.log.Logger;

public class TestDisjunctive {

	

	public final static BitMask SETTINGS = new BitMask();

	public final static int NB_TESTS = 4;

	protected static void solveAll(DisjProblem dp, final int nbSol, Policy policy) {
		solveAll(dp, nbSol, policy, null);
	}

	protected static void solveAll(DisjProblem dp, final int nbSol, Policy policy, Configuration conf) {
		dp.generateSolver(policy, conf);
		SchedUtilities.solveRandom(dp.solver, nbSol, -1, "disj " + SETTINGS);
		//Utilities.solveTotOrder(apc.createProblem(),nbSol,-1,"disj Total Order "+apc.DisjSettings.toString());
	}

	public static void launchAllRules(DisjProblem apc, final int nbSol) {
		launchAllRules(apc, NB_TESTS, nbSol);
	}

	public static void launchAllRules(DisjProblem apc, final int nbTests, final int nbSol) {
		apc.initializeModel();
		for (int i = 0; i < nbTests; i++) {
			//Rule[] rules = new Rule[]{Rule.NOT_LAST};
			final Policy[] rules = Policy.values();
			for (int j = 2; j < rules.length; j++) {
				apc.generateSolver(rules[j]);
				SchedUtilities.solveRandom(apc.solver, nbSol, -1, "disj " + rules[j]);
			}
			SETTINGS.clear();
			SETTINGS.set(Disjunctive.OVERLOAD_CHECKING);
			solveAll(apc, nbSol, Policy.DEFAULT);

			SETTINGS.set(Disjunctive.NF_NL);
			SETTINGS.set(Disjunctive.DETECTABLE_PRECEDENCE);
			solveAll(apc, nbSol, Policy.VILIM);

			SETTINGS.set(Disjunctive.EDGE_FINDING_D);
			apc.generateSolver(Policy.DEFAULT);
			CPSolver s = apc.solver;
			apc.generateSolver(Policy.VILIM);
			SchedUtilities.compare(nbSol, -1, "Default vs Vilim", s, apc.solver);

			solveAll(apc, nbSol, Policy.VILIM, AbstractTestProblem.getConfig(false));

			solveAll(apc, nbSol, Policy.VILIM, AbstractTestProblem.getConfig(true));


		}
	}



	@Test
	public void testToyProblem() {
		final int[] pt = {4, 6, 2};
		final int[] min = {1, 3, 6};
		final int[] max = {4, 7, 11};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = CSolver.constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 1);
	}


	@Test
	public void testToyProblem2() {
		final int[] pt = {11, 10, 5};
		final int[] min = {0, 1, 14};
		final int[] max = {14, 17, 30};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 238);
	}


	@Test
	public void testToyProblem3() {
		final int[] pt = {5, 16, 9};
		final int[] min = {0, 0, 0};
		final int[] max = {25, 14, 21};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 6);
	}

	@Test
	public void testToyProblem4() {
		final int[] pt = {4, 5, 6};
		final int[] min = {0, 0, 0};
		final int[] max = {11, 10, 9};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 6);
	}

	@Test
	public void gotToBeFactoriel() {
		Logger.info("warning : be patient");
		int[] sizes = {3, 4, 5};
		int[] horizon = {30,25, 15};
		DisjProblem[] pb={new DisjProblem(null, null)};
		//pb[1].forbidInt=true;
		//DisjProblem[] pb={new DisjSchedProblem()};
		for (DisjProblem aPb : pb) {
			//pb[k].forbidInt=true;
			for (int aHorizon : horizon) {
				aPb.setHorizon(aHorizon);
				Logger.info("" + aPb.horizon);
				for (int size : sizes) {
					Logger.info("sizes=" + size);
					aPb.setRandomProblem(size);
					launchAllRules(aPb, Math.max(NB_TESTS / 2, 1),(int) MathUtils.factoriel(size));
				}
			}
		}
	}


	@Test
	public void testVariableDuration0() {
		int[] min = {2, 2};
		int[] max = {3, 2};
		DisjProblem dp = new DisjProblem(null, SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(4);
		launchAllRules(dp, 2);
	}

	@Test
	public void testVariableDuration1() {
		int[] min = {2, 2};
		int[] max = {3, 3};
		DisjProblem dp = new DisjProblem(null , SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(5);
		launchAllRules(dp, 10);
	}

	@Test
	public void testVariableDuration2() {
		// triplet (3,5,3) : 6 permutations * 4 configuration : 24 solutions
		// triplet (3,5,4),(4,5,3) : 2*6 solutions
		//triplet (4,5,4) infaisable
		int[] min = {3, 5, 3};
		int[] max = {4, 5, 4};
		DisjProblem dp = new DisjProblem(null, SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(12);
		launchAllRules(dp, 36);
	}



	@Ignore
	public void testBugNilDuration1() throws ContradictionException {
		//bug detected on: [T_5_7[0, 31], T_6_7[31, 191], T_4_7[199, 325], T_2_7[315, 523], T_1_7[513, 860], T_0_7[885, 1098], T_7_7[1104, 1104], T_3_7[1097, 1104]]
		final TaskVariable[] tasks = { makeTaskVar("t1", 0, 5, 5), makeTaskVar("t2", 5, 10, 5),makeTaskVar("t2", 9, 16, 6), makeTaskVar("t1", 16,16, 0)};
		Model m = new CPModel();
		m.addConstraint( disjunctive(tasks, Options.C_DISJ_NFNL));
		Solver s = new CPSolver();
		s.read(m);
		s.propagate();
	}

	@Ignore
	public void testBugNilDuration2() throws ContradictionException {
		//bug detected on: [T_5_7[0, 31], T_6_7[31, 191], T_4_7[199, 325], T_2_7[315, 523], T_1_7[513, 860], T_0_7[885, 1098], T_7_7[1104, 1104], T_3_7[1097, 1104]]
		final TaskVariable[] tasks = { makeTaskVar("t1", 0, 5, 5), makeTaskVar("t2", 5, 10, 5),makeTaskVar("t2", 9, 16, 6), makeTaskVar("t1", 16,16, 0)};
		final IntegerVariable[] usages = makeBooleanVarArray("u", 2);
		Model m = new CPModel();
		m.addConstraint( disjunctive(tasks, usages, Options.C_DISJ_NFNL));
		Solver s = new CPSolver();
		s.read(m);
		s.getVar(usages[0]).setVal(1);
		s.getVar(usages[1]).setVal(1);
		s.propagate();
	}

	@Ignore
	public void testBugDoublon() {
		final TaskVariable[] tasks = makeTaskVarArray("T", 0, 35, new int[]{2,3,4,5,6,7,8});
		final TaskVariable[] dtasks = Arrays.copyOf(tasks, tasks.length + 2);
		dtasks[tasks.length] = tasks[0];
		dtasks[tasks.length+1] = tasks[1];

		Model m = new CPModel();
		m.addConstraint( disjunctive(dtasks));
		CPSolver s = new CPSolver();
		s.read(m);
		s.setRandomSelectors(0);
		s.solveAll();
		assertFalse(s.isFeasible());

		final IntegerVariable[] usages = makeBooleanVarArray("u", 2);
		m = new CPModel();
		m.addConstraint( disjunctive(tasks, usages));
		s = new CPSolver();
		s.read(m);
		s.solveAll();
		assertTrue(s.isFeasible());
		assertEquals(6, s.getSolutionCount());
		assertTrue(s.getVar(usages[0]).isInstantiatedTo(0));
		assertTrue(s.getVar(usages[1]).isInstantiatedTo(0));		
	}

}
