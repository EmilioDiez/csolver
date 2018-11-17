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

package csolver.model.constraints.global;

import static csolver.kernel.CSolver.constantArray;
import static csolver.kernel.CSolver.eq;
import static csolver.kernel.CSolver.geq;
import static csolver.kernel.CSolver.leq;
import static csolver.kernel.CSolver.pack;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.pack.PackSConstraint;
import csolver.cp.solver.search.integer.branching.PackDynRemovals;
import csolver.cp.solver.search.integer.valselector.BestFit;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.bitmask.StringMask;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.common.util.tools.MathUtils;
import csolver.kernel.common.util.tools.StringUtils;
import csolver.kernel.model.Model;
import csolver.kernel.model.ModelException;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.constraints.pack.PackModel;
import csolver.kernel.model.variables.integer.IntegerConstantVariable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;

/**
 *
 */
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
public class PackTest  {

	public final static int MIN_ITEMS=4;

	public final static int MAX_ITEMS=7;

	public final static int NB_TESTS=5;

	private final static Random RND=new Random();

	public final static int NB_RND_TESTS=1;

	private final static int RND_PB_CAPA=25;

	private final static int RND_NB_ITEMS=7;

	protected CPModel model;

	protected PackModel modeler;

	protected List<CPModel> models = new ArrayList<CPModel>();

	protected List<CPSolver> solvers = new ArrayList<CPSolver>();


	private CPModel initializeModel(Constraint[] cstr,StringMask... options) {
		CPModel m =new CPModel();
		Constraint pack = pack(modeler);
		for (StringMask o : options) {
			pack.addOption(o.getOption());
		}
		m.addConstraint(pack);
		if(cstr!=null) {m.addConstraints(cstr);}
		return m;
	}

	private void initializeModels(Constraint... cstr) {
		models.add(initializeModel(cstr));
		models.add(initializeModel(cstr,PackSConstraint.ADDITIONAL_RULES));
		models.add(initializeModel(cstr,PackSConstraint.DYNAMIC_LB));
		models.add(initializeModel(cstr,PackSConstraint.DYNAMIC_LB,PackSConstraint.ADDITIONAL_RULES));
	}

	private void initializeModels(int[] sizes,int nbBins,int capacity) {
		modeler = new PackModel(sizes, nbBins, capacity);
		models.clear();
		initializeModels();
	}


	protected void initializeSolvers() {
		solvers.clear();
		int seed = RND.nextInt(100000);
		initializeSolvers(false, seed);
		initializeSolvers(true, seed);
	}

	protected void initializeSolvers(boolean set,int seed) {
		for (CPModel m : models) {
			solvers.add(createSolver(m, set, seed));
		}
	}
	private CPSolver createSolver(CPModel model, boolean set,int seed) {
		CPSolver solver = new CPSolver();
		solver.read(model);
		if(seed==-1) {
			solver.setVarIntSelector(new MinDomain(solver));
		}else {
			solver.setRandomSelectors(seed);
		}
		if(!set) {
			solver.clearGoals();
			solver.addGoal(solver.generateDefaultIntGoal());
			solver.addGoal(solver.generateSetDefaultGoal());
		}
		solver.getConfiguration().putFalse(Configuration.STOP_AT_FIRST_SOLUTION);
		solver.generateSearchStrategy();
		return solver;
	}


	protected void testAll(int nbSol) {
		Logger.info("%%%%%%% PACK TEST ALL%%%%%%%%%%%");
		CPSolver last=null;
		for (int i = 0; i < NB_TESTS; i++) {
			initializeSolvers();
			for (CPSolver s : this.solvers) {
				s.launch();
				Boolean test=(nbSol != 0);
				assertEquals("sat ", test , s.isFeasible());
				if(nbSol>0){
					assertEquals("nb Sol.",nbSol,s.getNbSolutions());
				}
				if(last!=null) {
					assertEquals("sat ", last.getNbSolutions() , s.getNbSolutions());
				}
				last=s;
			}
		}
	}

	@Ignore
	public void testCompletion() throws ContradictionException {
		//ChocoLogging.toSolution();
		model = new CPModel();
		modeler = new PackModel("",new int[]{6,6,6,3,3,2}, 6, 10);
		model.addConstraint(pack(modeler, Options.C_PACK_FB,Options.C_PACK_AR ,Options.C_PACK_LBE));
		modeler.packLargeItems(model);
		Solver s = new CPSolver();
		s.read(model);
		//s.minimize(s.getVar(modeler.nbNonEmpty), false);
		s.propagate();
		IntDomainVar[] vars = s.getVar(modeler.bins);
		int[] vals = new int[]{0,1,2,0,1,2};
		Logger.severe(PackTest.class,StringUtils.pretty(s.getVar(modeler.bins)));

		for (int i = 0; i < vars.length; i++) {
			assertTrue(vars[i].isInstantiatedTo(vals[i]));
		}
		// FIXME - fill bin does not detect the completion of bins 0,1 by 3,3 and then bin 2 by 2 - created 12 juil. 2011 by Arnaud Malapert
		//ChocoLogging.flushLogs();
	}


	@Test(expected=ModelException.class)
	public void notSorted() {
		model = new CPModel();
		IntegerConstantVariable[] s= constantArray(new int[]{3,3,3,4,3});
		Constraint pack = pack(CSolver.makeSetVarArray("set",s.length, 0, 1),s,s,s,s[0]);
		model.addConstraint(pack);

	}


	@Test(expected=ModelException.class)
	public void increasing() {
		model = new CPModel();
		IntegerConstantVariable[] s= constantArray(new int[]{2,3,3,4,6});
		Constraint pack = pack(CSolver.makeSetVarArray("set",s.length, 0, 1),s,s,s,s[0]);
		model.addConstraint(pack);
	}

	@Test
	public void binForTwo() {
		for (int i = MIN_ITEMS; i < MAX_ITEMS; i=i+2) {
			int nbBins=(i+1)/2;
			initializeModels(ArrayUtils.oneToN(i), nbBins, i+1);
			testAll( (int) MathUtils.factoriel(nbBins));
		}
	}

	@Test
	public void sat() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		int[] s1={2,3,6,4,3};
		initializeModels(s1, 3, 10);
		testAll(138);
	}

	@Test
	public void unsat() {
		int[] s1={8,5,4,4,4,3};
		int[] s2={8,6,6,5,4,4,4,4,4,3};
		initializeModels(s1, 3, 10);
		testAll(0);
		initializeModels(s2, 5, 10);
		testAll(0);
	}


	@Test
	public void notIdenticalBins1() {
		int[] sizes={7,4,4,3,3,2};
		initializeModels(sizes, 3, 10);
		for (CPModel model : models) {
			model.addConstraints( eq(modeler.getLoads()[0], 8),
					eq(modeler.getLoads()[1], 6),
					geq(modeler.getLoads()[2], 9));
		}
		testAll(1);
	}

	@Test
	public void notIdenticalBins2() {
		int[] sizes={8,7,4,3,3};
		model = new CPModel();
		initializeModels(sizes,3,12);
		for (CPModel model : models) {
			model.addConstraints(
					geq(modeler.getLoads()[0], 11),
					leq(modeler.getLoads()[1], 3),
					geq(modeler.getLoads()[2], 9),
					leq(modeler.getLoads()[2], 11));
		}
		testAll(6);
	}

	private void initializeRandom(int nbItems,int capacity) {
		int[] sizes=new int[nbItems];
		int nbBins=1;
		int lbin=0;
		for (int i = 0; i < sizes.length; i++) {
			sizes[i]=RND.nextInt(capacity)+1;
			if(lbin+sizes[i]>capacity) {
				lbin=0;
				nbBins++;
			}
			lbin+=sizes[i];
		}
		initializeRandomModels(sizes, nbBins, capacity);
	}

	private void initializeRandomModels(int[] sizes,int nbBins,int capacity) {
		modeler = new PackModel(sizes, nbBins, capacity);
		models.clear();
		initializeModels();
		initializeModels(modeler.decreasingLoads(0));
		initializeModels(modeler.packLargeItems());
		initializeModels(modeler.orderEqualSizedItems(0));
		initializeModels(new Constraint[]{modeler.allDiffLargeItems()});
	}



	protected void testRandom() {
		Logger.info("%%%%%%%% TEST RANDOM %%%%%%%%");
		int ub = modeler.getNbBins();
		CPSolver last;
		do {
			Logger.info("\n%%%%%% ub = "+ub+"\n");
			last=null;
			initializeSolvers();
			for (CPSolver s : this.solvers) {
				s.post(s.leq(s.getVar(modeler.nbNonEmpty), ub));
				//CPSolver.setVerbosity(CPSolver.SEARCH);
				s.launch();
				Logger.info( "Nb solutions: "+s.getNbSolutions()+"\n"+ s.runtimeStatistics());
				if(last!=null) {
					assertEquals("sat ", last.isFeasible(), s.isFeasible());
				}
				last=s;
			}
			ub--;
		}while(last.isFeasible() && ub>0);
	}

	@Ignore
	public void testRandomProblems() {
		for (int i = 0; i < NB_RND_TESTS; i++) {
			initializeRandom(RND_NB_ITEMS,RND_PB_CAPA);
			testRandom();
		}
	}

	private final static int CAPACITY_1 = 1000;

	private final static int[] SIZES_1 = new int[]{
		495, 493, 492, 492, 481, 470, 450, 447,
		409, 399, 398, 396, 395, 392, 391, 389,
		385, 381, 378, 372, 370, 369, 352, 352,
		336, 331, 331, 327, 323, 313, 313, 307,
		296, 295, 288, 284, 284, 283, 280, 278,
		278, 270, 268, 268, 267, 266, 266, 258,
		257, 256, 256, 255, 253, 253, 253, 253,
		252, 252, 251, 251
	};


	private final static int[] SIZES_2 = {
		499, 493, 488, 470, 460, 460, 459, 459, 427, 423, 415, 407, 405, 395, 391, 384, 382, 368, 367,
		366, 363, 361, 358, 350, 343, 342, 342, 329, 324, 316, 305, 303, 298, 292, 288, 287, 286, 282,
		279, 276, 273, 270, 267, 263, 261, 261, 259, 259, 258, 257, 257, 255, 254, 254, 253, 253, 252, 251, 251, 250
	};
	//OptValue = 20

	private final static int CAPACITY_2 = 150;

	//u250_09 
	private final static int[] SIZES_3 = {
		100, 100, 100, 100, 100, 99, 99, 99, 99, 99, 98, 97, 97, 97, 97, 97, 97, 96, 96, 96, 95, 95, 95,
		95, 95, 94, 94, 93, 93, 93, 93, 92, 92, 92, 91, 91, 90, 90, 90, 90, 89, 88, 88, 88, 88, 88, 87, 
		87, 87, 86, 86, 86, 86, 86, 86, 85, 85, 85, 85, 85, 84, 84, 84, 84, 84, 84, 83, 83, 82, 81, 80, 
		79, 79, 79, 78, 78, 77, 77, 77, 77, 77, 76, 76, 75, 75, 74, 74, 73, 73, 72, 72, 72, 71, 70, 70,
		70, 69, 69, 69, 69, 69, 68, 68, 67, 67, 67, 66, 66, 65, 65, 65, 65, 64, 63, 63, 62, 62, 62, 62, 
		62, 62, 61, 61, 60, 60, 60, 59, 59, 59, 59, 58, 58, 58, 58, 57, 56, 55, 54, 54, 54, 54, 53, 52,
		51, 51, 50, 50, 50, 50, 50, 50, 50, 49, 49, 49, 49, 48, 48, 48, 47, 46, 46, 46, 46, 45, 44, 44,
		44, 44, 43, 43, 43, 43, 43, 42, 42, 41, 41, 41, 41, 40, 40, 39, 39, 39, 39, 39, 38, 38, 38, 37,
		37, 36, 36, 35, 35, 35, 35, 35, 34, 34, 34, 34, 33, 33, 33, 32, 32, 32, 32, 32, 31, 31, 31, 31, 
		30, 29, 29, 28, 28, 28, 28, 27, 27, 27, 27, 27, 26, 26, 26, 26, 25, 24, 24, 24, 24, 24, 24, 23,
		23, 23, 22, 22, 21, 21, 21, 21, 21, 21, 21
	};
	//optval = 101

	//u1000_11
	private final static int[] SIZES_4 = {
		100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
		99, 99, 99, 99, 99, 99, 99, 99, 99, 98, 98, 98, 98, 98, 98, 98, 98, 98, 98, 98, 98, 98, 98,
		98, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 96, 96, 96, 96, 96, 96, 96, 96,
		96, 96, 96, 96, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 95, 94, 94,
		94, 94, 94, 94, 94, 94, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 92,
		92, 92, 92, 92, 92, 92, 92, 92, 92, 92, 92, 92, 92, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 
		91, 90, 90, 90, 90, 90, 90, 90, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 88, 88, 88,
		88, 88, 88, 88, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 86, 86, 86, 86, 86, 86,
		86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 85, 85, 85, 85, 85, 85, 85, 85, 85, 84, 84, 84, 84,
		84, 84, 84, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 82, 82, 82, 82, 82, 82, 81, 81, 
		81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 79,
		79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 78, 78, 78, 78, 78, 78, 78, 78, 78, 78, 78, 77,
		77, 77, 77, 77, 77, 77, 77, 77, 76, 76, 76, 76, 76, 76, 76, 76, 76, 76, 75, 75, 75, 75, 75,
		75, 75, 75, 75, 75, 75, 74, 74, 74, 74, 74, 74, 74, 74, 74, 74, 74, 74, 74, 73, 73, 73, 73,
		73, 73, 73, 73, 73, 73, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72,
		72, 72, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 70, 70, 70, 70,
		70, 70, 70, 70, 70, 70, 70, 70, 70, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69, 69,
		69, 69, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 67, 67, 67, 67, 67, 67, 67, 66, 66,
		66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 66, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65,
		65, 65, 65, 65, 65, 65, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 63,
		63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62,
		62, 62, 62, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 61, 60, 60, 60, 60, 60, 60, 60, 60,
		60, 60, 60, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 58, 58, 58, 58, 58, 58, 58, 58,
		58, 58, 58, 58, 58, 58, 58, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 56, 56, 56, 56, 56, 56,
		56, 56, 56, 56, 56, 56, 56, 56, 56, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 54, 54, 54, 54,
		54, 54, 54, 54, 54, 54, 54, 54, 54, 53, 53, 53, 53, 53, 53, 53, 53, 52, 52, 52, 52, 52, 52,
		52, 52, 52, 52, 52, 52, 52, 52, 52, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51,
		51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 49, 49, 49, 49, 49,
		49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48,
		48, 48, 48, 48, 48, 48, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 46, 46, 46, 46, 46,
		46, 46, 46, 46, 46, 46, 45, 45, 45, 45, 45, 45, 45, 45, 45, 44, 44, 44, 44, 44, 44, 44, 44,
		44, 44, 44, 44, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 42, 42, 42, 
		42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41,
		40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 38, 38, 38,
		38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37,
		37, 37, 37, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 35, 35, 35, 35, 35, 35, 35, 35,
		35, 35, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 32,
		32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31,
		31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 29, 29, 29, 29, 29, 29, 29,
		29, 29, 29, 29, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 27, 27, 27, 27, 27,
		27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 
		26, 26, 25, 25, 25, 25, 25, 24, 24, 24, 24, 24, 24, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
		23, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 21, 21, 21, 21, 21, 21, 21, 21, 
		21, 21, 21, 21, 21, 21, 21, 20, 20, 20, 20, 20, 20, 20, 20, 20
	};
	//optval = 401

	public Solver packing(PackModel packM) {
		final CPModel m = new CPModel();
		final Constraint pc = CSolver.pack(packM
				,Options.C_PACK_AR, Options.C_PACK_DLB, Options.C_PACK_FB);
		//,Options.C_PACK_AR, Options.C_PACK_FB);
		m.addConstraint(pc);
		final CPSolver s = new CPSolver();
		s.read(m);

		final PackSConstraint spc = (PackSConstraint) s.getCstr(pc);
		s.clearGoals();
		s.addGoal(new PackDynRemovals(new StaticVarOrder(s,s.getVar(packM.bins)), new BestFit(spc), spc));
		return s;
	}

	@Test
	public void testHadrien1() {
		Solver s = packing( new PackModel(SIZES_1, 20, CAPACITY_1));
		assertEquals(Boolean.TRUE, s.solve());
	}

	@Test
	public void testHadrien2() {
		Solver s = packing( new PackModel(SIZES_2, 20, CAPACITY_1));
		assertEquals(Boolean.TRUE, s.solve());
	}

	@Test
	public void testHadrien3() {
		Solver s = packing( new PackModel(SIZES_3, 101, CAPACITY_2));
		assertEquals(Boolean.TRUE, s.solve());
	}

	//@Test
	//public void testHadrien4() {
	//	Solver s = packing( new PackModel(SIZES_4, 401, CAPACITY_2));
	//	assertEquals(Boolean.TRUE, s.solve());
	//}


	// avoid infinite loop
	@Test
	public void testFabien1(){
		int nb  = 5;
		SetVariable[] itemSets = CSolver.makeSetVarArray("itemSets", nb, -5, -1);
		IntegerVariable[] loads = CSolver.makeIntVarArray("loads", nb, 0, 5);
		IntegerVariable[] bins = CSolver.makeIntVarArray("bins", nb, 0, 5);
		int[] _sizes = new int[nb];
		Arrays.fill(_sizes, 5);
		IntegerConstantVariable[] sizes = constantArray(_sizes);


		Constraint cc = CSolver.pack(new PackModel(bins, sizes, itemSets, loads));
		Model m = new CPModel();
		m.addConstraint(cc);

		Solver s= new CPSolver();
		s.read(m);

		assertEquals(Boolean.FALSE, s.solve());
	}

	// avoid infinite loop
	@Test
	public void testFabien2(){
		int nb  = 5;
		SetVariable[] itemSets = CSolver.makeSetVarArray("itemSets", nb, 100, 102);
		IntegerVariable[] loads = CSolver.makeIntVarArray("loads", nb, 0, 5);
		IntegerVariable[] bins = CSolver.makeIntVarArray("bins", nb, 0, 5);
		int[] _sizes = new int[nb];
		Arrays.fill(_sizes, 5);
		IntegerConstantVariable[] sizes = constantArray(_sizes);

		Constraint cc = CSolver.pack(new PackModel(bins, sizes, itemSets, loads));
		Model m = new CPModel();
		m.addConstraint(cc);

		Solver s= new CPSolver();
		s.read(m);

		assertEquals(Boolean.FALSE, s.solve());
	}
}