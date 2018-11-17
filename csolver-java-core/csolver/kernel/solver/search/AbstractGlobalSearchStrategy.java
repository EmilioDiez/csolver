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

package csolver.kernel.solver.search;

import static csolver.kernel.common.util.tools.StringUtils.pretty;
import static csolver.kernel.common.util.tools.StringUtils.prettyOnePerLine;

import java.util.logging.Level;

import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solution;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.branch.AbstractBranchingStrategy;
import csolver.kernel.solver.branch.AbstractIntBranchingStrategy;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.propagation.ShavingTools;
import csolver.kernel.solver.search.AbstractSearchLoop.SearchLoopStep;
import csolver.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import csolver.kernel.solver.search.measure.ISearchMeasures;
import csolver.kernel.timer.nitoku.ISearchTimer;
import csolver.kernel.timer.nitoku.ITimerAction;
import nitoku.log.Logger;

/**
 * An abstract class for controlling tree search in various ways
 * version 2.0.3 : change the value of search constant to use bit masks.
 */
public abstract class AbstractGlobalSearchStrategy extends AbstractSearchStrategy 
											implements ISearchMeasures, ISearchTimer{

	/**
	 * constants for driving the incremental search algorithm
	 */
	public static final int INIT_SEARCH = 0;
	public static final int OPEN_NODE = 1;
	public static final int UP_BRANCH = 1 << 1;
	public static final int DOWN_BRANCH = 1 << 2;
	public static final int RESTART = 1 << 3;
	public static final int STOP = 1 << 4;

	/**
	 * index of the current trace in the stack
	 */
	protected int currentTraceIndex = -1;

	/**
	 * a data structure storing the stack of choice contexts (for incremental search explorations)
	 */
	protected IntBranchingTrace[] traceStack;

	/**
	 * a reusable trace object to start the branching from the root node.
	 */
	private final IntBranchingTrace initialTrace = new IntBranchingTrace();

	/**
	 * search controller: a flag storing the next move in the search tree
	 */
	public int nextMove = INIT_SEARCH;

	/**
	 * indicates whether the control should stop after the first solution is found
	 */
	public final boolean stopAtFirstSol;

	/**
	 * indicates whether a limit was encountered in the alst incremental search
	 */
	protected AbstractGlobalSearchLimit encounteredLimit = null;

	/**
	 * 
	 * the goal that needs be solved to drive the exploration
	 */
	public AbstractIntBranchingStrategy mainGoal;

	/**
	 * the index of the world where the search started
	 */
	public int baseWorld = 0;

	public ShavingTools shavingTools;

	public GlobalSearchLimitManager limitManager;

	public AbstractSearchLoop searchLoop;

	protected long initialPropagation;

	protected AbstractGlobalSearchStrategy(Solver solver) {
		super(solver);
		traceStack = new IntBranchingTrace[solver.getNbIntVars() + solver.getNbSetVars()];
		nextMove = INIT_SEARCH;
		stopAtFirstSol = solver.getConfiguration().readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
	}


	public void initMainGoal(SConstraint c) {
		if (mainGoal != null) {
			mainGoal.initConstraintForBranching(c);
			AbstractBranchingStrategy branch = mainGoal.getNextBranching();
			while(branch != null) {
				branch.initConstraintForBranching(c);
				branch = branch.getNextBranching();
			}
		}
	}


	public IObjectiveManager getObjectiveManager() {
		return null;
	}

	public final GlobalSearchLimitManager getLimitManager() {
		return limitManager;
	}


	public final AbstractSearchLoop getSearchLoop() {
		return searchLoop;
	}


	public final ShavingTools getShavingTools() {
		return shavingTools;
	}


	public final void setShavingTools(ShavingTools shavingTools) {
		this.shavingTools = shavingTools;
	}


	public final void setSearchLoop(AbstractSearchLoop searchLoop) {
		this.searchLoop = searchLoop;
	}

	public final void setLimitManager(GlobalSearchLimitManager limitManager) {
		this.limitManager = limitManager;
	}


	/*
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 *
	 * @deprecated replaced by incrementalRun
	 */
	/*public void run() {
    boolean feasibleRootState = true;
    try {
      newTreeSearch();
      model.propagate();
    } catch (ContradictionException e) {
      feasibleRootState = false;
    }
    if (feasibleRootState) {
      model.worldPush();
      mainGoal.explore(1);
      model.worldPop();
      // explore is responsible for recordinf model.feasible = Boolean.TRUE in case a solution is found
      if (model.feasible == null) {
        model.feasible = Boolean.TRUE;
      } else if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
        restoreBestSolution();
      }
    } else {
      model.feasible = Boolean.FALSE;
    }
    endTreeSearch();
  }*/

	/**
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 * @param launchFinalizeAction 
	 */
	public void incrementalRun(ITimerAction launchFinalizeAction) {
		
		baseWorld = solver.getWorldIndex();
		boolean feasibleRootState = true;
		try {
			newTreeSearch();
			//initializeDegreeOfVariables();
			solver.propagate();
		} catch (ContradictionException e) {
			feasibleRootState = false;
		}
		
		if (feasibleRootState) {
			
			newFeasibleRootState();
			solver.getTimer().start(this, launchFinalizeAction);
						
		} else {
			solver.setFeasible(Boolean.FALSE);
			endTreeSearch();
			if(launchFinalizeAction != null){
				launchFinalizeAction.runTimerAction();
			}
		}		
	}
	
	public SearchLoopStep timerRun(){
		return searchSolutions(stopAtFirstSol);
	}
	
	public SearchLoopStep searchSolutions(Boolean stopAtFirstSol) {
		//precondition for calling the search loop is that a limit has not been attempted
		//useful when solution are recorded from outside the OPEN_NODE case (hand-made)
		//in this case a limit and a solution could have been found at the same time
		
		if(!this.isEncounteredLimit()) {
			
			AbstractSearchLoop.SearchLoopStep step = searchLoop._run();
			
			if (stopAtFirstSol == Boolean.TRUE){
				if ( step != AbstractSearchLoop.SearchLoopStep.LOOP_SEARCH_ENDED ){
					return step; //continue the search					
				}else{
					return SearchLoopStep.ONE_SOLUTION_SEARCH_ENDED;
				}
			}else{
				if ( step != AbstractSearchLoop.SearchLoopStep.SOLUTIONS_SEARCH_ENDED ){
					if (step == AbstractSearchLoop.SearchLoopStep.LOOP_SEARCH_ENDED){
						return SearchLoopStep.ONE_SOLUTION_SEARCH_ENDED;
					}else{
						return step; //continue the search
					}
				}else{
					return SearchLoopStep.SOLUTIONS_SEARCH_ENDED;
				}
			}			
		}		
		return SearchLoopStep.LOOP_SEARCH_ENDED; //stop the search	a limit has been encountered
		
	}
	
	public void timerEnd(ITimerAction launchFinalizeAction){
		if (  ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
			solver.worldPopUntil(baseWorld);
			restoreBestSolution();
		}
		
		if (!isEncounteredLimit() && !existsSolution()) {
			solver.setFeasible(Boolean.FALSE);
		}
		
		endTreeSearch();
		
		if(launchFinalizeAction != null){
			launchFinalizeAction.runTimerAction();
		}
	}
	protected final boolean isFeasibleRootState() {
		return solver.isFeasible() != Boolean.FALSE;
	}

	public final void initialPropagation() {
		long timer = -System.currentTimeMillis();
		try {
			newTreeSearch();
			//initializeDegreeOfVariables();
			solver.propagate();
			//System.out.println(solver.pretty());
			advancedInitialPropagation();
			newFeasibleRootState();
		} catch (ContradictionException e) {
			if (Logger.isLoggable(Level.FINE) && 
					e.getContradictionCause() != null ) {
				Logger.fine( "- Initial Propagation: Contradiction due to " + e.getContradictionCause());
			}
			solver.setFeasible(Boolean.FALSE);
		}
		initialPropagation = System.currentTimeMillis() + timer;
	}


	protected void advancedInitialPropagation() throws ContradictionException {
		if( solver.getConfiguration().readBoolean(Configuration.INIT_SHAVING) ) shavingTools.shaving();
	}


	protected final void topDownSearch() {
		if (stopAtFirstSol) {
			nextSolution();
		} else {
			//noinspection StatementWithEmptyBody
			while (nextSolution() == Boolean.TRUE){}
		}
	}
	/**
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 * @deprecated use incrementalRun(ITimerAction launchFinalizeAction)
	 */
	public void incrementalRun() {
		//		baseWorld = solver.getWorldIndex();
		//		boolean feasibleRootState = true;
		//		try {
		//			newTreeSearch();
		//			//initializeDegreeOfVariables();
		//			solver.propagate();
		//		} catch (ContradictionException e) {
		//			feasibleRootState = false;
		//		}
		//		if (feasibleRootState) {
		//			newFeasibleRootState();
		//			if (stopAtFirstSol) {
		//				nextSolution();
		//			} else {
		//				//noinspection StatementWithEmptyBody
		//				while (nextSolution() == Boolean.TRUE){}
		//			}
		//			if (  ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
		//				solver.worldPopUntil(baseWorld);
		//				restoreBestSolution();
		//			}
		//			if (!isEncounteredLimit() && !existsSolution()) {
		//				solver.setFeasible(Boolean.FALSE);
		//			}
		//		} else {
		//			solver.setFeasible(Boolean.FALSE);
		//		}
		initialPropagation();
		if(isFeasibleRootState()) {
			//System.out.println(solver.getWorldIndex() +">"+ baseWorld);
			assert(solver.getWorldIndex() > baseWorld);
			topDownSearch();
		}
		endTreeSearch();
	}

	

	/**
	 * called before a new search tree is explored
	 * @throws csolver.kernel.solver.ContradictionException
	 */
	public void newTreeSearch() throws ContradictionException {
		assert(solver.getSearchStrategy() == this);
		//Logger.info(CSolverLogging.START_MESSAGE);
		baseWorld = solver.getWorldIndex();
		resetSolutions();
		initialTrace.setBranching(this.mainGoal);
		solver.getPropagationEngine().getFailMeasure().safeReset();
		limitManager.initialize();
		searchLoop.initialize();
	}

	/**
	 * called when the root state of the search tree is feasible. 
	 */
	public void newFeasibleRootState() {
		solver.worldPush();
	}


	public void endTreeSearch() {
		if ( ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
			solver.worldPopUntil(baseWorld);
			restoreBestSolution();
		}
		if (!isEncounteredLimit() && !existsSolution()) {
			solver.setFeasible(Boolean.FALSE);
		}
		limitManager.endTreeSearch();
		if (Logger.isLoggable(Level.INFO)) {
			if( isEncounteredLimit() ) {
				Logger.info( "- Search incompleted: Exiting on limit reached\n  Limit: "
											+	limitManager.toString() + "\n" + runtimeStatistics());
			}else if( solver.isFeasible() == Boolean.TRUE) {
				Logger.info( "- Search completed\n" + runtimeStatistics());
			}else if( solver.isFeasible() == Boolean.FALSE) {
				Logger.info( "- Search completed: No solutions\n" + runtimeStatistics());
			}else {
				Logger.info( "- Search stopped ?" + runtimeStatistics()); 
			}
		}
	}


	public Boolean nextSolution() {
		//precondition for calling the search loop is that a limit has not been attempted
		//useful when solution are recorded from outside the OPEN_NODE case (hand-made)
		//in this case a limit and a solution could have been found at the same time
		if(this.isEncounteredLimit()) {
			return null;
		} else {return  searchLoop.run();}
	}



	@Override
	public void writeSolution(Solution sol) {
		super.writeSolution(sol);
		sol.recordSearchMeasures(this);
	}


	/**
	 * called when a solution is encountered: printing and, if needed, storing the solution
	 */
	@Override
	public void recordSolution() {
		//Check wether every decisions variables have been instantiated
		assert(solver.checkSolution());
		super.recordSolution();
		if (Logger.isLoggable(Level.FINE)) {
			Logger.fine( "- Solution #"
							+getSolutionCount()+" found. "+partialRuntimeStatistics(false)+".");	
			if (Logger.isLoggable(Level.FINER)) {
				Logger.finer(solver.solutionToString());
			}
		}
	}


	/**
	 * called before going down into each branch of the choice point
	 * @throws csolver.kernel.solver.ContradictionException
	 */
	public void postDynamicCut() throws ContradictionException {}


	public final IntBranchingTrace pushTrace() {
		currentTraceIndex++;
		if (currentTraceIndex >= traceStack.length) {
			//ensure capacity
			int newCapacity = (traceStack.length * 3)/2 + 1;
			IntBranchingTrace[] tmp = new IntBranchingTrace[newCapacity];
			System.arraycopy(traceStack, 0, tmp, 0, traceStack.length);
			traceStack = tmp;
			traceStack[currentTraceIndex] = new IntBranchingTrace(); //create trace 
		}else if (traceStack[currentTraceIndex] == null) {
			traceStack[currentTraceIndex] = new IntBranchingTrace();  //create trace
		}else {
			//is it really useful as we overwrite the trace ?
			traceStack[currentTraceIndex].clear(); //reset old trace
		}
		return traceStack[currentTraceIndex];
	}

	public final boolean isTraceEmpty() {
		return currentTraceIndex < 0;
	}

	public final IntBranchingTrace getTrace(int index) {
		return traceStack[index];
	}

	public final int getCurrentTraceIndex() {
		return currentTraceIndex;
	}

	public final int getTraceSize() {
		return currentTraceIndex+1;
	}

	public final IntBranchingTrace popTrace() {
		if (currentTraceIndex <= 0) {
			currentTraceIndex = -1;
			return null;
		} else {
			currentTraceIndex--;
			return traceStack[currentTraceIndex];
		}
	}


	public final IntBranchingTrace initialTrace() {
		return isTraceEmpty() ? initialTrace : traceStack[currentTraceIndex];
	}

	/**
	 * @returns reusable trace object to start or restart the search.
	 */
	public final IntBranchingTrace getReusableInitialTrace() {
		return initialTrace;
	}

	public final IntBranchingTrace topTrace() {
		return isTraceEmpty() ? null : traceStack[currentTraceIndex];
	}

	public final void clearTrace() {
		currentTraceIndex = -1;
	}

	//	public final void popTraceUntil(int targetWorld) {
	//		clearTrace();
	//
	//		/*int deltaWorld = (solver.getEnvironment().getWorldIndex() - targetWorld);
	//    if (deltaWorld > 0) {
	//      if (currentTraceIndex - deltaWorld < -1)
	//        Logger.severe("bizarre");
	//      currentTraceIndex = currentTraceIndex - deltaWorld;
	//    }*/
	//	}




	public String runtimeStatistics() {
		return "  Solutions: "+getSolutionCount() + '\n' +prettyOnePerLine(this);
	}

	public String partialRuntimeStatistics(boolean logOnSolution) {
		return logOnSolution ? 
				getSolutionCount()+" Solutions, "+pretty(this) :
					pretty(this);
	}



	/**
	 * @return the time elapsed during the last search in milliseconds
	 */
	@Override
	public int getTimeCount() {
		return limitManager.getTimeCount();
	}



	/**
	 * @return the number of nodes of the tree search (including the root node where
	 * initial propagation has been performed and saved)
	 */
	@Override
	public int getNodeCount() {
		return searchLoop.getNodeCount();
	}

	/**
	 * @return the number of backtracks of the tree search
	 */
	@Override
	public int getBackTrackCount() {
		return searchLoop.getBacktrackCount();
	}


	@Override
	public int getRestartCount() {
		return searchLoop.getRestartCount();
	}

	/**
	 * @return the number of fails of the tree search
	 */
	@Override
	public int getFailCount() {
		return solver.getPropagationEngine().getFailMeasure().getFailCount();
	}

	/**
	 * Checks if a limit has been encountered
	 *
	 * @return true if a limit has been reached
	 */
	public final boolean isEncounteredLimit() {
		return encounteredLimit != null;
	}

	/**
	 * If a limit has been encounteres, return the involved limit
	 * @return the encoutered limit
	 */
	public final AbstractGlobalSearchLimit getEncounteredLimit() {
		return encounteredLimit;
	}



	public final void setEncounteredLimit(AbstractGlobalSearchLimit encounteredLimit) {
		this.encounteredLimit = encounteredLimit;
	}





}
