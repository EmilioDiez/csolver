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

package csolver.cp.solver.search;

import static csolver.cp.solver.search.VarSelectorFactory.domDDegSel;
import static csolver.cp.solver.search.VarSelectorFactory.domDegSel;
import static csolver.cp.solver.search.VarSelectorFactory.domWDegSel;
import static csolver.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createMaxPreservedRatio;
import static csolver.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createMinPreservedRatio;
import static csolver.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createPreservedWDegRatio;
import static csolver.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory.createSlackWDegRatio;
import static csolver.kernel.common.util.tools.VariableUtils.getTaskVars;

import java.util.Arrays;
import java.util.Comparator;

import csolver.cp.common.util.preprocessor.detector.scheduling.DisjunctiveSModel;
import csolver.cp.solver.constraints.global.pack.PackSConstraint;
import csolver.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarValPair;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.branching.PackDynRemovals;
import csolver.cp.solver.search.integer.branching.domwdeg.DomOverWDegBinBranchingNew;
import csolver.cp.solver.search.integer.branching.domwdeg.DomOverWDegBranchingNew;
import csolver.cp.solver.search.integer.branching.domwdeg.TaskOverWDegBinBranching;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.valselector.BestFit;
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.valselector.RandomIntValSelector;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.cp.solver.search.integer.varselector.RandomIntVarSelector;
import csolver.cp.solver.search.integer.varselector.StaticVarOrder;
import csolver.cp.solver.search.integer.varselector.ratioselector.DomOverWDegSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.MaxRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.RandDomOverWDegSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.RandMaxRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.ratios.task.CompositePrecValSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MaxPreservedRatio;
import csolver.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MinPreservedRatio;
import csolver.cp.solver.search.set.AssignSetVar;
import csolver.cp.solver.search.set.MinEnv;
import csolver.cp.solver.search.set.RandomSetValSelector;
import csolver.cp.solver.search.set.RandomSetVarSelector;
import csolver.cp.solver.search.set.StaticSetVarOrder;
import csolver.cp.solver.search.task.OrderingValSelector;
import csolver.cp.solver.search.task.SetTimes;
import csolver.cp.solver.search.task.ordering.CentroidOrdering;
import csolver.cp.solver.search.task.ordering.LexOrdering;
import csolver.cp.solver.search.task.ordering.MaxPreservedOrdering;
import csolver.cp.solver.search.task.ordering.MinPreservedOrdering;
import csolver.cp.solver.search.task.profile.ProfileSelector;
import csolver.kernel.common.util.comparator.TaskComparators;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.global.scheduling.IResource;
import csolver.kernel.solver.search.ValIterator;
import csolver.kernel.solver.search.ValSelector;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.scheduling.ITask;
import csolver.kernel.solver.variables.scheduling.TaskVar;
import csolver.kernel.solver.variables.set.SetVar;

public final class BranchingFactory {

	private BranchingFactory() {
		super();
	}

	//*************************************************************************//

	public static AssignVar minDomMinVal(Solver s, IntDomainVar[] vars) {
		return new AssignVar( new MinDomain(s, vars), new MinVal());
	}

	public static AssignVar minDomMinVal(Solver s) {
		return minDomMinVal(s, s.getIntDecisionVars());
	}

	//*************************************************************************//

	public static AssignVar minDomIncDom(Solver s, IntDomainVar[] vars) {
		return new AssignVar( new MinDomain(s, vars), new IncreasingDomain());
	}

	public static AssignVar minDomIncDom(Solver s) {
		return minDomMinVal(s, s.getIntDecisionVars());
	}

	//*************************************************************************//

	public static AssignVar lexicographic(Solver solver) {
		return lexicographic(solver, solver.getIntDecisionVars());
	}

	public static AssignVar lexicographic(Solver solver, IntDomainVar[] vars) {
		return lexicographic(solver, vars, new MinVal());
	}

	public static AssignVar lexicographic(Solver solver, IntDomainVar[] vars, ValSelector<IntDomainVar> valSel) {
		return new AssignVar( new StaticVarOrder(solver, vars), valSel);
	}

	public static AssignSetVar lexicographic(Solver solver, SetVar[] vars) {
		return new AssignSetVar( new StaticSetVarOrder(solver, vars), new MinEnv());
	}

	//*************************************************************************//

	public static AssignOrForbidIntVarVal randomIntBinSearch(Solver solver, long seed) {
		return randomBinSearch(solver, solver.getIntDecisionVars(), seed);
	}

	public static AssignOrForbidIntVarVal randomBinSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignOrForbidIntVarVal( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignVar randomIntSearch(Solver solver, long seed) {
		return randomSearch(solver, solver.getIntDecisionVars(), seed);
	}

	public static AssignVar randomSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignVar( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignSetVar randomSetSearch(Solver solver, long seed) {
		return randomSearch(solver, solver.getSetDecisionVars(), seed);
	}

	public static AssignSetVar randomSearch(Solver solver, SetVar[] vars, long seed) {
		return new AssignSetVar(new RandomSetVarSelector(solver, vars, seed), new RandomSetValSelector(seed));
	}
	//*****************************************************************//
	//******************** Domain Over Degree ************************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domDegBin(Solver solver) {
		return domDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, ValSelector valSel) {
		return domDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( domDegSel(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( domDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//
	public static AssignVar domDeg(Solver solver) {
		return domDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domDeg(Solver solver, ValIterator valSel) {
		return domDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domDegSel(solver, vars), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//


	public static AssignVar domDeg(Solver solver, ValSelector valSel) {
		return domDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domDegSel(solver, vars), valSel);
	}

	public static AssignVar domDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar( domDegSel(solver, vars, seed), valSel);
	}
	//*****************************************************************//
	//******************** Domain Over Dynamic Degree ****************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver) {
		return domDDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, ValSelector valSel) {
		return domDDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( domDDegSel(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domDDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( domDDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//
	public static AssignVar domDDeg(Solver solver) {
		return domDDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domDDeg(Solver solver, ValIterator valSel) {
		return domDDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domDDegSel(solver, vars), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domDDegSel(solver, vars, seed), valSel);
	}

	//*************************************************************************//


	public static AssignVar domDDeg(Solver solver, ValSelector valSel) {
		return domDDeg(solver,solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domDDegSel(solver, vars), valSel);
	}

	public static AssignVar domDDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar( domDDegSel(solver, vars, seed), valSel);
	}


	//*****************************************************************//
	//******************** Domain Over Weighted Degree  **************//
	//***************************************************************//

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver) {
		return domWDegBin(solver, new MinVal());
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, ValSelector valSel) {
		return domWDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignOrForbidIntVarVal( new DomOverWDegSelector(solver, vars), valSel);
	}

	public static AssignOrForbidIntVarVal domWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignOrForbidIntVarVal( new RandDomOverWDegSelector(solver, vars, seed), valSel);
	}

	//*************************************************************************//

	public static AssignVar domWDeg(Solver solver) {
		return domWDeg(solver, new IncreasingDomain());
	}

	public static AssignVar domWDeg(Solver solver, ValIterator valSel) {
		return domWDeg(solver, solver.getIntDecisionVars(), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new AssignVar( domWDegSel(solver, vars), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new AssignVar( domWDegSel(solver, vars, seed), valSel);
	}
	//*************************************************************************//

	public static AssignVar domWDeg(Solver solver, ValSelector valSel) {
		return domWDeg(solver,solver.getIntDecisionVars() , valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new AssignVar( domWDegSel(solver, vars), valSel);
	}

	public static AssignVar domWDeg(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new AssignVar(domWDegSel(solver, vars, seed), valSel);
	}

	//*****************************************************************//
	//*******************  Task Domain Over Weighted Degree **********//
	//***************************************************************//


	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver) {
		return incDomWDegBin(solver, solver.getIntDecisionVars(), new MinVal());
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, ValSelector valSel) {
		return incDomWDegBin(solver, solver.getIntDecisionVars(), valSel);
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel) {
		return new DomOverWDegBinBranchingNew(solver, vars, valSel, null);
	}

	public static DomOverWDegBinBranchingNew incDomWDegBin(Solver solver, IntDomainVar[] vars, ValSelector valSel, long seed) {
		return new DomOverWDegBinBranchingNew(solver, vars, valSel, seed);
	}

	//*************************************************************************//

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver) {
		return incDomWDeg(solver, new IncreasingDomain());
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, ValIterator valSel) {
		return incDomWDeg(solver, solver.getIntDecisionVars(), valSel);
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel) {
		return new DomOverWDegBranchingNew(solver, vars, valSel, null);
	}

	public static DomOverWDegBranchingNew incDomWDeg(Solver solver, IntDomainVar[] vars, ValIterator valSel, long seed) {
		return new DomOverWDegBranchingNew(solver, vars, valSel, seed);
	}
	//*************************************************************************//

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return slackWDeg(solver, precedences, new CentroidOrdering(seed));
	}

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		return new TaskOverWDegBinBranching(solver, createSlackWDegRatio(precedences, true), valSel, null);
	}

	public static TaskOverWDegBinBranching slackWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		return new TaskOverWDegBinBranching(solver, createSlackWDegRatio(precedences, true), valSel, seed);
	}

	//*************************************************************************//

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return preservedWDeg(solver, precedences, new CentroidOrdering(seed));
	}

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		return new TaskOverWDegBinBranching(solver, createPreservedWDegRatio(precedences, true), valSel, null);
	}

	public static TaskOverWDegBinBranching preservedWDeg(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		return new TaskOverWDegBinBranching(solver, createPreservedWDegRatio(precedences, true), valSel, seed);
	}

	//*****************************************************************//
	//*******************  Preserved Heuristics **********************//
	//***************************************************************//

	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return minPreserved(solver, precedences, new MinPreservedOrdering(seed), seed);
	}

	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		final MinPreservedRatio[] ratios = createMinPreservedRatio(precedences);
		final MinRatioSelector varSel = new MinRatioSelector(solver, ratios);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}


	public static AssignOrForbidIntVarValPair minPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		final MinPreservedRatio[] ratios = createMinPreservedRatio(precedences);
		final RandMinRatioSelector varSel = new RandMinRatioSelector(solver, ratios, seed);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	//*************************************************************************//

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, long seed) {
		return maxPreserved(solver, precedences, new MaxPreservedOrdering(seed), seed);
	}

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel) {
		final MaxPreservedRatio[] ratios = createMaxPreservedRatio(precedences);
		final MaxRatioSelector varSel = new MaxRatioSelector(solver, ratios);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	public static AssignOrForbidIntVarValPair maxPreserved(Solver solver, ITemporalSRelation[] precedences, OrderingValSelector valSel, long seed) {
		final MaxPreservedRatio[] ratios = createMaxPreservedRatio(precedences);
		final RandMaxRatioSelector varSel = new RandMaxRatioSelector(solver, ratios, seed);
		return new AssignOrForbidIntVarValPair(new CompositePrecValSelector(ratios, varSel, valSel));
	}

	//*****************************************************************//
	//************************* SetTimes *****************************//
	//***************************************************************//

	public static SetTimes setTimes(final Solver solver) {
		final TaskVar[] tasks = getTaskVars(solver);
		Arrays.sort(tasks,TaskComparators.makeRMinDurationCmp()); 
		return new SetTimes(solver,Arrays.asList(tasks) , TaskComparators.makeEarliestStartingTimeCmp());
	}

	public static SetTimes setTimes(final Solver solver, final long seed) {
		return setTimes(solver,getTaskVars(solver), TaskComparators.makeEarliestStartingTimeCmp(), seed);
	}

	public static SetTimes setTimes(final Solver solver, final TaskVar[] tasks, final Comparator<ITask> comparator) {
		return new SetTimes(solver, Arrays.asList(tasks), comparator);
	}
	
	public static SetTimes setTimes(final Solver solver, final TaskVar[] tasks, final Comparator<ITask> comparator, final long seed) {
		return new SetTimes(solver, Arrays.asList(tasks), comparator, seed);
	}

	//*****************************************************************//
	//******************** Complete Decreasing ***********************//
	//***************************************************************//

	public static AssignVar completeDecreasing(Solver solver, PackSConstraint ct, boolean bestFit, boolean dynRem) {
		final StaticVarOrder varSel = new StaticVarOrder(solver, ct.getBins());
		//value selection : First-Fit ~ MinVal
		final ValSelector<IntDomainVar> valSel = bestFit ? new BestFit(ct) : new MinVal();
		return dynRem ? new PackDynRemovals(varSel, valSel, ct) : new AssignVar(varSel, valSel);
	}

	public static AssignVar completeDecreasingFirstFit(Solver solver, PackSConstraint ct) {
		return completeDecreasing(solver, ct, false, true);
	}

	public static AssignVar completeDecreasingBestFit(Solver solver, PackSConstraint ct) {
		return completeDecreasing(solver, ct, true, true);
	}

	//*****************************************************************//
	//*******************  Profile Heuristics **********************//
	//***************************************************************//

	public static AssignOrForbidIntVarValPair profile(Solver solver,  DisjunctiveSModel disjSModel) {
		return profile(solver, disjSModel, new LexOrdering());
	}

	public static AssignOrForbidIntVarValPair profile(Solver solver, DisjunctiveSModel disjSModel, long seed) {
		return profile(solver, disjSModel, new CentroidOrdering(seed));
	}

	public static AssignOrForbidIntVarValPair profile(Solver solver, DisjunctiveSModel disjSModel, OrderingValSelector valSel) {
		return new AssignOrForbidIntVarValPair(new ProfileSelector(solver, disjSModel, valSel));
	}


	public static AssignOrForbidIntVarValPair profile(Solver solver, IResource<?>[] resources, DisjunctiveSModel disjSModel) {
		return profile(solver, resources, disjSModel, new LexOrdering());
	}

	public static AssignOrForbidIntVarValPair profile(Solver solver, IResource<?>[] resources, DisjunctiveSModel disjSModel, long seed) {
		return profile(solver, resources, disjSModel, new CentroidOrdering(seed));
	}

	public static AssignOrForbidIntVarValPair profile(Solver solver, IResource<?>[] resources, DisjunctiveSModel disjSModel, OrderingValSelector valSel) {
		return new AssignOrForbidIntVarValPair(new ProfileSelector(solver, resources, disjSModel, valSel));
	}



}
