/* 
 * Choco is an open-source software,          
 * distributed under a BSD licence           
 *    Copyright (C) F. Laburthe,             
 *                  N. Jussien    1999-2008  
 *
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.visu.searchloop;

import static csolver.kernel.timer.nitoku.SolverLastStepState.DOWN;
import static csolver.kernel.timer.nitoku.SolverLastStepState.END;
import static csolver.kernel.timer.nitoku.SolverLastStepState.NONE;
import static csolver.kernel.timer.nitoku.SolverLastStepState.RESTART;
import static csolver.kernel.timer.nitoku.SolverLastStepState.SOLUTION;
import static csolver.kernel.timer.nitoku.SolverLastStepState.UP;

import java.util.ArrayList;

import csolver.kernel.IObserver;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.search.AbstractGlobalSearchStrategy;
import csolver.kernel.solver.search.AbstractSearchLoop;
import csolver.kernel.timer.nitoku.IObservableStepSearchLoop;
import csolver.kernel.timer.nitoku.SolverLastStepState;

public class ObservableStepSearchLoop extends AbstractSearchLoop implements IObservableStepSearchLoop {


	private final ArrayList<IObserver> obs;

	private final AbstractSearchLoop internalSearchLoop;

	private int previousNbSolutions;

	private Step action = Step.PAUSE; // 0: pause; 1: next; 2: play

	private Solver solver;
	
	//for tree search vizualisation
	public SolverLastStepState state = NONE;

	public ObservableStepSearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
		solver = searchStrategy.getSolver();
		obs = new ArrayList<IObserver>(16);
        internalSearchLoop = searchStrategy.searchLoop;
        searchStrategy.setSearchLoop(this);
	}

    public AbstractSearchLoop getInternalSearchLoop() {
        return internalSearchLoop;
    }

    public final void testTimer() {
    	
		state = NONE;
		
		this.solver.getTimer().action(action);
	
	}

	////////////////////////////////////////TO INTERACT WITH GUI////////////////////////////////////////////////////////

	@Override
	public void initialize() {
		super.initialize();
		internalSearchLoop.initialize();
	}




	@Override
	public void downBranch() {
        testTimer();
		state = DOWN;
		internalSearchLoop.downBranch();
		notifyObservers(this);

	}




	@Override
	public Boolean endLoop() {
		state = END;
		notifyObservers(this);
		return internalSearchLoop.endLoop();
	}




	@Override
	public void initLoop() {
		internalSearchLoop.initLoop();
		previousNbSolutions = searchStrategy.getSolutionCount();
	}

	@Override
	public void initSearch() {
		internalSearchLoop.initSearch();

	}


	@Override
	public void openNode() {
        testTimer();
		internalSearchLoop.openNode();
		if(searchStrategy.getSolutionCount() > previousNbSolutions) {
            previousNbSolutions++;
			state = SOLUTION;
			stop = true;	
		}
		notifyObservers(this);
		if(action.equals(Step.PAUSE)){
			action=Step.NEXT;
		}
	}




	@Override
	public void restart() {
        testTimer();
		state = RESTART;
		notifyObservers(this);
		internalSearchLoop.restart();
	}




	@Override
	public void upBranch() {
        testTimer();
		state = UP;
		internalSearchLoop.upBranch();
        if(searchStrategy.isTraceEmpty()){
            stop = true;
        }
		notifyObservers(this);
	}


	////////////////////////////////////////TO INTERACT WITH GUI////////////////////////////////////////////////////////

	public void runStepByStep(){
		action = Step.NEXT;
		testTimer();
	}

	public void runForAWhile(){
		action = Step.PLAY;
		testTimer();
	}

	public void pause(){
		action = Step.PAUSE;
		testTimer();
	}
	
	public void next(){
		action = Step.NEXT;
		testTimer();
	}

	public void setAction(Step action) {
		this.action = action;
		testTimer();
	}


	////////////////////////////////IObservable implementation////////////////////////////

	/**
	 * Adds an observer to the set of observers for this object, provided
	 * that it is not the same as some observer already in the set.
	 * The order in which notifications will be delivered to multiple
	 * observers is not specified. See the class comment.
	 *
	 * @param o an observer to be added.
	 * @throws NullPointerException if the parameter o is null.
	 */
	public synchronized void addObserver(IObserver o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.add(o);
		}
	}

	/**
	 * If this object has changed, as indicated by the
	 * <code>hasChanged</code> method, then notify all of its observers
	 * and then call the <code>clearChanged</code> method to indicate
	 * that this object has no longer changed.
	 * <p/>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and the <code>arg</code> argument.
	 *
	 * @param arg any object.
	 * @see choco.IObserver#update(choco.IObservable , Object)
	 */
	public void notifyObservers(Object arg) {
		/*
		 * a temporary array buffer, used as a snapshot of the state of
		 * current Observers.
		 */
		Object[] arrLocal;

		synchronized (this) {
			/* We don't want the Observer doing callbacks into
			 * arbitrary code while holding its own Monitor.
			 * The code where we extract each Observable from
			 * the Vector and store the state of the Observer
			 * needs synchronization, but notifying observers
			 * does not (should not).  The worst result of any
			 * potential race-condition here is that:
			 * 1) a newly-added Observer will miss a
			 *   notification in progress
			 * 2) a recently unregistered Observer will be
			 *   wrongly notified when it doesn't care
			 */
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length-1; i>=0; i--)
			((IObserver)arrLocal[i]).update(this, arg);
	}

}


