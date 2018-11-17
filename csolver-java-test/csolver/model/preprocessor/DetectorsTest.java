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
package csolver.model.preprocessor;

import static csolver.cp.model.preprocessor.ModelDetectorFactory.allSchedulingModelDetectors;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.cliqueDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.disjFromCumulDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.disjointDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.disjunctiveModelDetectors;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.intVarEqDet;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.precFromDisjointDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.precFromImpliedDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.precFromReifiedDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.precFromTimeWindowDetector;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.runtest;
import static csolver.cp.model.preprocessor.ModelDetectorFactory.taskVarEqDet;
import static csolver.kernel.CSolver.ONE;
import static csolver.kernel.CSolver.ZERO;
import static csolver.kernel.CSolver.clause;
import static csolver.kernel.CSolver.constant;
import static csolver.kernel.CSolver.constantArray;
import static csolver.kernel.CSolver.cumulativeMax;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.gt;
import static csolver.kernel.CSolver.leq;
import static csolver.kernel.CSolver.makeBooleanVar;
import static csolver.kernel.CSolver.makeBooleanVarArray;
import static csolver.kernel.CSolver.makeIntVar;
import static csolver.kernel.CSolver.makeIntVarArray;
import static csolver.kernel.CSolver.makeTaskVar;
import static csolver.kernel.CSolver.makeTaskVarArray;
import static csolver.kernel.CSolver.minus;
import static csolver.kernel.CSolver.mult;
import static csolver.kernel.CSolver.neq;
import static csolver.kernel.CSolver.precedence;
import static csolver.kernel.CSolver.precedenceDisjoint;
import static csolver.kernel.CSolver.precedenceImplied;
import static csolver.kernel.CSolver.precedenceReified;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.common.util.preprocessor.AbstractAdvancedDetector;
import csolver.cp.common.util.preprocessor.AbstractDetector;
import csolver.cp.common.util.preprocessor.detector.scheduling.DisjunctiveModel;
import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.BoundAllDiff;
import csolver.cp.solver.constraints.global.matching.AllDifferent;
import csolver.cp.solver.preprocessor.PreProcessCPSolver;
import csolver.cp.solver.preprocessor.PreProcessConfiguration;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.iterators.DisposableIterator;
import csolver.kernel.model.constraints.ConstraintType;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.variables.scheduling.TaskVar;
import nitoku.log.Logger;

public class DetectorsTest {

	@Test
	public void detectEqualities1(){
		CPModel m;
		CPSolver s;
		for(int size = 1000; size <= 2000; size +=100){
			m = new CPModel();
			IntegerVariable[] vars = makeIntVarArray("v", size, 0, 10, Options.V_BOUND);
			for(int i = 0; i < size-1; i++){
				m.addConstraint(eq(vars[i], vars[i+1]));
			}
			long t = -System.currentTimeMillis();
			runtest(m, intVarEqDet(m));
			t += System.currentTimeMillis();
			Logger.info(String.format("t: %d ms", t));
			s = new CPSolver();
			s.read(m);
			assertEquals(1, s.getNbIntVars());
			assertEquals(0, s.getNbIntConstraints());
		}
	}

	@Test
	public void detectEqualities2(){
		CPModel m;
		CPSolver s;
		for(int size = 1000; size <= 2000; size +=100){
			m = new CPModel();
			IntegerVariable[] vars = makeIntVarArray("v", size, 0, 10, Options.V_BOUND);
			for(int i = 0; i < size-1; i++){
				m.addConstraint(eq(vars[i], vars[i+1]));
				m.addConstraint(leq(vars[i], vars[i+1]));
			}
			long t = -System.currentTimeMillis();
			runtest(m, intVarEqDet(m));
			t += System.currentTimeMillis();
			Logger.info(String.format("t: %d ms", t));
			s = new CPSolver();
			s.read(m);
			assertEquals(1, s.getNbIntVars());
			assertEquals(size-1, s.getNbIntConstraints());
		}
	}

	@Test
	public void testDetectionCliques() {
		CPModel m = new CPModel();
		m.setDefaultExpressionDecomposition(false);
		IntegerVariable[] v = makeIntVarArray("v", 13, 0, 11);

		for (int i = 0; i < v.length; i++) {
			for (int j = i + 1; j < v.length; j++) {
				m.addConstraint(neq(v[i],v[j]));
			}
		}
		runtest(m, cliqueDetector(m, true));

		CPSolver s = new CPSolver();

		s.read(m);

		DisposableIterator<SConstraint> it = s.getConstraintIterator();
		while (it.hasNext()) {
			SConstraint p = it.next();
			Logger.info(p.pretty());
		}
		it.dispose();
		s.solveAll();
		assertEquals(0, s.getNbSolutions());
	}


	@Test
	public void testIncludedDiff() {
		CPModel m = new CPModel();
		m.setDefaultExpressionDecomposition(true);
		IntegerVariable[] v = makeIntVarArray("v", 5, 0, 5);
		IntegerVariable[] v2 = makeIntVarArray("v", 5, 0, 5);

		for (int i = 0; i < 5; i++) {
			for (int j = i + 1; j < 5; j++) {
				m.addConstraint(gt(0,mult(minus(v[i],v[j]),minus(v[j],v[i]))));
				m.addConstraint(neq(v2[i],v2[j]));
			}
		}

		runtest(m, cliqueDetector(m, true));

		CPSolver s = new CPSolver();

		s.read(m);

		DisposableIterator<SConstraint> it = s.getConstraintIterator();
		boolean alldiffd = false;
		while (it.hasNext()) {
			SConstraint p = it.next();
			Logger.info(p.pretty());
			Logger.info(String.format("%s", p));
			alldiffd |= (p instanceof AllDifferent || p instanceof BoundAllDiff);
		}
		it.dispose();
		assertTrue(alldiffd);
	}

	@Test
	public void testEqualitiesDetection() {
		for (int k = 0; k < 10; k++) {
			CPModel m = new CPModel();
			Solver s = new PreProcessCPSolver();
			Solver s2 = new CPSolver();
			int n = 2;
			IntegerVariable[] vars = CSolver.makeIntVarArray("v", n, 0, n - 1);

			for (int i = 0; i < n - 1; i++) {
				m.addConstraint(eq(vars[i], vars[i + 1]));
			}
			m.addConstraint(eq(vars[n - 1], n-1));
			long t1 = System.currentTimeMillis();
			runtest(m, intVarEqDet(m));
			s2.read(m);
			long t2 = System.currentTimeMillis();
			s.read(m);
			long t3 = System.currentTimeMillis();

			assertEquals("nb var BB", s.getNbIntVars(), 1);
			assertEquals("nb const var BB", s.getNbConstants(),1);
			assertEquals("nb var S", s2.getNbIntVars(), 1); // n + 1 = n + cste
			assertEquals("nb var S", s2.getNbConstants(),1);
			assertTrue("One solution BB",s.solve());
			assertTrue("One solution S",s2.solve());
			assertEquals("Nb node BB",s.getSearchStrategy().getNodeCount(), 1);
			assertEquals("Nb node S",s2.getSearchStrategy().getNodeCount(), 1);
			Logger.info(String.format("BlackBox:%d / Solver:%d", (t2 - t1), (t3 - t2)));
		}
	}


	@Test
	public void testMixedEqualitiesDetection() {
		for (int k = 0; k < 10; k++) {
			CPModel m = new CPModel();
			Solver ps = new PreProcessCPSolver();
			Solver s = new CPSolver();
			int n = 1000;
			IntegerVariable[] vars = makeIntVarArray("v", n, 0, n - 1);

			for (int i = 0; i < n/2; i++) {
				m.addConstraint(eq(vars[i], vars[i + 1]));
			}
			m.addConstraint(leq(vars[n/2], vars[(n/2)+1]));
			for (int i = (n/2)+1; i < n-1; i++) {
				m.addConstraint(eq(vars[i], vars[i + 1]));
			}
			long t1 = System.currentTimeMillis();
			runtest(m, intVarEqDet(m));
			s.read(m);
			long t2 = System.currentTimeMillis();

			ps.read(m);
			long t3 = System.currentTimeMillis();

			assertEquals("nb var S", s.getNbIntVars(), 2);
			assertTrue("One solution S",s.solve());
			//            assertEquals("Nb node S",s.getSearchStrategy().getNodeCount(), 3);
			assertEquals("nb var PS", ps.getNbIntVars(), 2);
			assertTrue("One solution PS",ps.solve());
			//            assertEquals("Nb node PS",ps.getSearchStrategy().getNodeCount(), 3);
			Logger.info(new StringBuilder().append("S:").append(t2 - t1).append(" / PS:").append(t3 - t2).toString());
		}
	}

	@Test
	public void testEqualitiesWithConstante() {
		for (int k = 0; k < 4; k++) {
			CPModel m = new CPModel();
			Solver s = new CPSolver();
			IntegerVariable v1 = makeIntVar("v1", 0, 2);
			IntegerVariable v2 = null;
			Boolean doable = null;
			switch (k){
			case 0:
				v2 = makeIntVar("v2", 0, 2);
				doable = true;
				break;
			case 1:
				v2 = makeIntVar("v2", -1, 3);
				doable = true;
				break;
			case 2:
				v2 = makeIntVar("v2", 1, 1);
				doable = true;
				break;
			case 3:
				v2 = makeIntVar("v2", 4, 6);
				doable = false;
				break;
			}
			m.addConstraint(eq(v1, v2));
			runtest(m, intVarEqDet(m));
			s.read(m);
			s.solve();
			assertEquals("Not expected results", doable, s.isFeasible());

		}
	}

	@Test
	public void detectTasks2(){
		CPModel m;
		CPSolver s;

		m = new CPModel();
		IntegerVariable A = makeIntVar("A", 0, 10, Options.V_BOUND);
		IntegerVariable B = makeIntVar("B", 0, 10, Options.V_BOUND);
		IntegerVariable C = makeIntVar("C", 0, 10, Options.V_BOUND);

		TaskVariable t1 = makeTaskVar("t2", A, C);
		TaskVariable t2 = makeTaskVar("t1", A, B, C);

		m.addVariables(t1, t2);
		runtest(m, taskVarEqDet(m));

		s = new CPSolver();
		s.read(m);

		assertEquals(1, s.getNbTaskVars());
		TaskVar tv = s.getTaskVar(0);

		assertEquals(A.getLowB(), tv.start().getInf());
		assertEquals(B.getLowB(), tv.duration().getInf());
		assertEquals(C.getLowB(), tv.end().getInf());

		assertEquals(A.getUppB(), tv.start().getSup());
		assertEquals(B.getUppB(), tv.duration().getSup());
		assertEquals(C.getUppB(), tv.end().getSup());
	}

	@Test
	public void detectTasks3(){
		CPModel m;
		CPSolver s;

		m = new CPModel();
		IntegerVariable A = makeIntVar("A", 0, 10, Options.V_BOUND);
		IntegerVariable B = makeIntVar("B", 0, 10, Options.V_BOUND);
		IntegerVariable C = makeIntVar("C", 0, 10, Options.V_BOUND);

		for(int i = 0; i < 100; i++){
			TaskVariable t = makeTaskVar("t", A, B, C);
			m.addVariables(t);
		}

		runtest(m, taskVarEqDet(m));
		s = new CPSolver();
		s.read(m);

		assertEquals(1, s.getNbTaskVars());

	}

	private void testDisjunctive(int[] d, int[] h, int capa, int nbSols){
		CPModel model = new CPModel();
		TaskVariable[] tasks = makeTaskVarArray("T", 0, 10, d);
		model.addConstraint(cumulativeMax("c1", tasks, constantArray(h), makeBooleanVarArray("U", 2), constant(capa)));
		assertEquals(1, model.getNbConstraints());
		runtest(model, disjFromCumulDetector(model));
		assertEquals(2, model.getNbConstraints());
		CPSolver s = new CPSolver();
		s.read(model);
		s.solveAll();
		assertEquals(nbSols, s.getNbSolutions());
	}

	@Test
	public void detectDisjunctive1(){
		testDisjunctive(new int[]{5,2,3,3,3,3}, new int[]{5,5,3,2,3,2}, 5, 384);
		testDisjunctive(new int[]{5,2,3,3,3,3}, new int[]{8,8,5,2,4,4}, 8, 384);
	}

	@Test
	public void detectDisjunctive2(){
		CPModel model = new CPModel();
		TaskVariable[] tasks = makeTaskVarArray("T", 0, 10, new int[]{2,2,2,2,2});
		model.addConstraint(cumulativeMax("c1", tasks, constantArray(new int[]{3,3,3,3,3,}), makeBooleanVarArray("U", 2), constant(5)));
		assertEquals(1, model.getNbConstraints());
		runtest(model, disjFromCumulDetector(model));
		assertEquals(0, model.getNbConstraintByType(ConstraintType.CUMULATIVE));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.DISJUNCTIVE));
	}
	private final TaskVariable t1 = makeTaskVar("T1", 0, 10, 3);
	private final TaskVariable t2 = makeTaskVar("T2", 15, 20, 4);
	private final TaskVariable t3 = makeTaskVar("T3", 0, 10, 5);
	private final TaskVariable t4 = makeTaskVar("T4", 10, 5);

	@Ignore
	public void detectImplied(){
		CPModel model = new CPModel();
		model.addConstraint(precedenceImplied(t4, 3,t3, ZERO));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_IMPLIED));
		final DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, precFromImpliedDetector(model, disjMod));
		assertEquals(0, model.getNbConstraints());

		// FIXME - Problem with hook - created 4 juil. 2011 by Arnaud Malapert
		model = new CPModel();
		model.addConstraint(precedenceImplied(t4, 3,t3, ONE));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_IMPLIED));
		runtest(model, precFromImpliedDetector(model, disjMod));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_DISJOINT));   	
	}

	@Test
	public void detectReified(){
		CPModel model = new CPModel();
		model.addConstraint(precedenceReified(t4, 3,t3, ZERO));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_REIFIED));
		DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, precFromImpliedDetector(model, disjMod));
		assertEquals(0, model.getNbConstraintByType(ConstraintType.PRECEDENCE_IMPLIED));
		assertEquals(1, model.getNbConstraints());

		// FIXME - Problem with hook - created 4 juil. 2011 by Arnaud Malapert
		model = new CPModel();
		model.addConstraint(precedenceReified(t4, 3,t3, ONE));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_REIFIED));
		disjMod = new DisjunctiveModel(model);
		runtest(model, precFromReifiedDetector(model, disjMod));
		assertEquals(1, model.getNbConstraintByType(ConstraintType.PRECEDENCE_DISJOINT));   	
	}

	@Test
	public void detectPrecedence1(){
		CPModel model = new CPModel();
		model.addConstraints(
				precedence(t1, t2),
				precedence(t1, t2,5),
				precedence(t1, t2,6),
				precedence(t1, t3),
				precedence(t1, t3,4),
				precedence(t1, t3,2)				
				);
		assertEquals(6, model.getNbConstraints());
		DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, precFromTimeWindowDetector(model, disjMod),
				precFromDisjointDetector(model, disjMod));
		assertEquals(2, model.getNbConstraints());
	}

	@Test
	public void detectPrecedence2(){
		CPModel model = new CPModel();
		model.addConstraints(
				precedence(t1, t2,3),
				precedenceDisjoint(t1, t2, CSolver.makeBooleanVar("b1"), 4,2)
				);
		assertEquals(2, model.getNbConstraints());
		final DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, precFromTimeWindowDetector(model, disjMod),
				precFromDisjointDetector(model, disjMod),
				disjointDetector(model, disjMod));
		assertEquals(0, model.getNbConstraints());
	}



	@Test
	public void detectDisjoint1(){
		CPModel model = new CPModel();
		IntegerVariable[] b = CSolver.makeBooleanVarArray("b", 3);
		model.addConstraints(
				CSolver.eq(CSolver.scalar(new int[]{3,-2,-1}, b), 0),
				precedenceDisjoint(t1, t3, b[0],2,0),
				precedenceDisjoint(t1, t3, b[1],1,1),
				precedenceDisjoint(t3, t1, b[2],2,0)
				);
		assertEquals(4, model.getNbConstraints());
		final DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, disjointDetector(model, disjMod));
		assertEquals(2, model.getNbConstraints());
		//VisuFactory.getDotManager().show(disjMod);
	}

	@Test
	public void detectDisjModel(){
		CPModel model = new CPModel();
		final TaskVariable[] tv1 = makeTaskVarArray("T1", 0, 15, makeIntVarArray("T", 5, 3, 4));
		final TaskVariable[] tv2 = makeTaskVarArray("T2", 20, 30, makeIntVarArray("T", 5, 3, 4));
		final int[] hv2 = {1,2,3,4,3};
		IntegerVariable[] b = CSolver.makeBooleanVarArray("b", 4);
		model.addConstraints(
				CSolver.disjunctive(tv1),
				CSolver.cumulativeMax(tv2, hv2, 5),
				precedenceDisjoint(tv1[0], tv2[0], b[0],2,8),
				precedenceDisjoint(tv1[0], tv1[3], b[1],1,3),
				precedenceDisjoint(tv1[0], tv1[1], b[2],2,2),
				precedenceDisjoint(tv1[2], tv1[3], b[3],1,4),
				precedenceImplied(tv1[0], 3, tv2[0], b[1]),
				precedenceImplied(tv1[4], 3, tv2[4], b[0]),
				precedenceReified(tv1[3], 8, tv2[0], b[1]),
				precedenceReified(tv1[2], 8, tv2[4], b[0]),
				precedence(tv1[2], tv2[2], 10),
				precedence(tv1[3], tv2[3], 12),
				precedence(tv1[2], tv2[4], 0)
				);

		final DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, allSchedulingModelDetectors(model, disjMod));
		//System.out.println(model.pretty());
		//System.out.println(disjMod.toString());
		//System.out.println(disjMod.setupTimesToString());
		//VizFactory.toDotty(new File("/tmp/test.dot"), disjMod);
	}


	@Ignore
	public void detectCstEq(){
		CPModel model = new CPModel();
		IntegerVariable[] b = makeBooleanVarArray("b", 3);
		model.addConstraints(
				clause(new IntegerVariable[]{b[0]}, new IntegerVariable[]{b[1]}),
				clause(new IntegerVariable[]{b[1]}, new IntegerVariable[]{b[2]}),
				eq(b[0],1)
				);

		runtest(model, intVarEqDet(model));
		//System.out.println(model.pretty());
		assertEquals(2, model.getNbBoolVar());
		assertEquals(1, model.getNbConstraints());

	}


	static class TestReplaceDetector extends AbstractAdvancedDetector {

		private IntegerVariable in, out;
		public TestReplaceDetector(CPModel model, IntegerVariable in, IntegerVariable out) {
			super(model);
			this.in = in;
			this.out = out;
		}

		@Override
		public void apply() {
			replaceBy(out, in);
		}



	}

	@Ignore
	public void detectClauseStore(){
		CPModel model = new CPModel();
		IntegerVariable[] b = makeBooleanVarArray("b", 4);
		model.addConstraints(
				clause(new IntegerVariable[]{b[0]}, new IntegerVariable[]{b[1]}),
				clause(new IntegerVariable[]{b[1]}, new IntegerVariable[]{b[2]}),
				clause(new IntegerVariable[]{b[0]}, new IntegerVariable[]{b[3]}),
				eq(b[0],1)
				);
		runtest(model,  new TestReplaceDetector(model, b[3], b[0]));
		//System.out.println(model.pretty());
		assertEquals(2, model.getNbConstraints());
		assertEquals(3, model.getNbBoolVar());


	}

	private static void test(final CPModel model, final AbstractDetector... detectors) {
		CPSolver solver = new CPSolver();
		solver.read(model);
		//solver.addGoal(BranchingFactory.minDomMinVal(solver));
		runtest(model, detectors);
		CPSolver ppsolver = new CPSolver();
		ppsolver.read(model);

		solver.solveAll();
		ppsolver.solveAll();
		assertEquals(solver.getSolutionCount(), ppsolver.getSolutionCount());
	}

	@Test
	public void detectCutCycleClauses(){
		//ChocoLogging.toVerbose();
		CPModel model = new CPModel();
		TaskVariable[] t = makeTaskVarArray("T", 0, 20, new int[]{1,2,3,4,5});
		IntegerVariable[] b = makeBooleanVarArray("b", 10);
		model.addConstraints(
				precedenceDisjoint(t[1], t[2], b[1]),
				precedenceDisjoint(t[0], t[1], b[0]),
				precedenceDisjoint(t[0], t[2], b[2]),

				precedenceDisjoint(t[1], t[4], b[3]),
				precedenceDisjoint(t[2], t[4], b[4]),
				precedenceDisjoint(t[3], t[4], b[5]),
				precedence(t[0], t[4]),
				precedence(t[0], t[3])				
				);
		final int ctCount = model.getNbConstraints() + 1;
		DisjunctiveModel disjMod = new DisjunctiveModel(model);
		test(model, disjunctiveModelDetectors(model, disjMod, true));
		assertEquals(ctCount, model.getNbConstraints());
		//VisuFactory.getDotManager().show(disjMod);
	}


	@Test
	public void detectTransitive(){
		//ChocoLogging.toSolution();
		CPModel model = new CPModel();
		TaskVariable[] t = makeTaskVarArray("T", 0, 47, new int[]{1,2,3,4,5,6,7,8,9,10, 11, 12, 13, 14, 15,16});
		model.addConstraints(
				precedence(t[0], t[1]),
				precedence(t[1], t[2]),
				precedenceDisjoint(t[0], t[2], makeBooleanVar("b1")),

				precedence(t[3], t[4]),
				precedence(t[4], t[5]),
				precedenceDisjoint(t[3], t[5], makeBooleanVar("b2")),

				precedence(t[6], t[7]),
				precedence(t[7], t[8]),
				precedence(t[8], t[9]),
				precedenceDisjoint(t[6], t[9], makeBooleanVar("b3")),

				precedence(t[7], t[10]),
				precedence(t[3], t[8]),

				precedence(t[2], t[11]),
				precedence(t[11], t[12]),
				precedence(t[11], t[13]),

				precedence(t[0], t[14]),
				precedence(t[13], t[14]),

				precedence(t[0], t[15]),
				precedence(t[13], t[15]),
				precedenceDisjoint(t[2], t[15], makeBooleanVar("b4")),

				precedenceDisjoint(t[2], t[4], makeBooleanVar("b5")),
				precedenceDisjoint(t[2], t[5], makeBooleanVar("b6"))

				);
		DisjunctiveModel disjMod = new DisjunctiveModel(model);
		test(model, disjunctiveModelDetectors(model, disjMod, true));
		//VisuFactory.getDotManager().show(disjMod);
		assertEquals(17, model.getNbConstraints());

	}

	@Ignore
	public void testModelReplacement(){
		CPModel model = new CPModel();
		final TaskVariable t1 = makeTaskVar("T1", 0, 20, 18);
		final TaskVariable t2 = makeTaskVar("T2", 20, 35, 10);
		IntegerVariable b = CSolver.makeBooleanVar("b");
		model.addConstraints(
				precedenceDisjoint(t1, t2, b),
				precedence(t1, t2, 10)
				);
		final DisjunctiveModel disjMod = new DisjunctiveModel(model);
		runtest(model, allSchedulingModelDetectors(model, disjMod));
		//System.out.println(model.pretty());
		assertEquals(1, model.getNbConstraints());
		assertEquals(0, model.getNbBoolVar());
		assertEquals(2, model.getNbStoredMultipleVars());
	}


	@Ignore
	public void testSolverReplacement(){
		CPModel model = new CPModel();
		final TaskVariable t1 = makeTaskVar("T1", 0, 20, 18);
		final TaskVariable t2 = makeTaskVar("T2", 20, 35, 10);
		IntegerVariable b = CSolver.makeBooleanVar("b");
		model.addConstraints(
				precedenceDisjoint(t1, t2, b),
				precedence(t1, t2, 10)
				);
		PreProcessCPSolver solver = new PreProcessCPSolver();
		solver.getConfiguration().putTrue(PreProcessConfiguration.DISJUNCTIVE_MODEL_DETECTION);
		solver.read(model);
		System.out.println(model.pretty());
		assertEquals(1, model.getNbConstraints());
		assertEquals(2, model.getNbStoredMultipleVars());
		
		System.out.println(solver.pretty());
		assertEquals(3, solver.getNbConstraints());
		assertEquals(0, solver.getNbBooleanVars());
		assertEquals(2, solver.getNbTaskVars());
	}




}
