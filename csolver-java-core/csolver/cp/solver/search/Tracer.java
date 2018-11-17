/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package csolver.cp.solver.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import csolver.kernel.IObservable;
import csolver.kernel.IObserver;
import csolver.kernel.solver.propagation.event.VarEvent;
import csolver.kernel.solver.search.ISearchLoop;
import csolver.kernel.solver.search.IntBranchingTrace;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.timer.nitoku.SolverLastStepState;
import csolver.kernel.visu.components.VisuSolverState;
import csolver.kernel.visu.components.VisuVarManager;
import csolver.kernel.visu.searchloop.ObservableStepSearchLoop;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 oct. 2008
 * Since : Choco 2.0.1
 *
 * This class is the main observer of the visualization.
 * Every modification over variables are observed and send to
 * the specific vizualisation.
 *
 */
public final class Tracer implements IObserver {

	protected ArrayList<VisuVarManager> vars;
	protected HashMap<Var, VisuVarManager> mapvars;
	protected int breaklength;
	private VisuSolverState solverState;

	public Tracer() {
		this.breaklength = 10;
	}

	/**
	 * Change the break length value
	 * @param breaklength the new break length
	 */
	public final void setBreaklength(final int breaklength) {
		this.breaklength = breaklength;
	}

	/**
	 * Set the variable event queue to observe
	 *
	 * @param observable
	 */
	public final void addObservable(final IObservable observable) {
		observable.addObserver(this);
		observable.notifyObservers(null);
	}

	/**
	 * Set the variables to draw
	 *
	 * @param vars
	 */
	public final void setVariables(final Collection<VisuVarManager> vars) {
		if(this.vars == null){
			this.vars = new ArrayList<VisuVarManager>();
			mapvars = new HashMap<Var, VisuVarManager>();
		}

        for (VisuVarManager var : vars) {
            this.vars.add(var);
            mapvars.put(var.getSolverVar(), var);
        }
	}

	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param o   the observable object.
	 * @param arg an argument passed to the <code>notifyObservers</code>
	 *            method.
	 *            <p/>
	 *            In that case, it redraw the canvas of the modified variable
	 *            or redraw every canvas if "fail" (arg = 1).
	 */
	public final void update(final IObservable o, final Object arg) {
		//haveBreak();
		if(arg instanceof VarEvent){//propagation
			VarEvent ve = (VarEvent)arg;
			VisuVarManager v =mapvars.get(ve.getModifiedVar());
			if(v != null){
				v.refresh(ve.getEventType());
			}
        	if (solverState != null){
        		solverState.throwPropagationEvent();
	        }
		}else if(arg instanceof ISearchLoop){//search
			
			ObservableStepSearchLoop searchloop = (ObservableStepSearchLoop)arg;
			
			IntBranchingTrace ctx = ((AbstractSearchLoopWithRestart)(searchloop)
                    .getInternalSearchLoop()).getCurrentTrace();
            if(ctx==null)return;
            Object ob = ctx.getBranchingObject();
            VisuVarManager v = null;
            if(ob instanceof IntDomainVar){
                v = mapvars.get(ob);
            }else if (ob instanceof Object[]){
                v = mapvars.get(((Object[])ob)[0]);
            }
            if(v != null){
                v.refresh(arg);
            }
                       
            if(searchloop.isStopped() == true &&
            	searchloop.state == SolverLastStepState.SOLUTION){
            	//new solution found event
            	if (solverState != null){
            		solverState.setNoSolutions(
            				searchloop.searchStrategy.getSolutionCount());
            		solverState.throwSolutionFoundEvent();
	            }                
            }
            
        	if (solverState != null){
        		solverState.setSolverStep(searchloop.state);
        		solverState.throwNextStepEvent();
	        }
        	
        }
  	}

	public VisuSolverState getSolverState() {
		return solverState;
	}

	public void initSolverState() {
		solverState = new VisuSolverState();		
	}


}
