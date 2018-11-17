/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.visu.events;

import csolver.kernel.visu.components.ISolverStateChangeEvent;
import csolver.kernel.visu.components.VisuSolverState;

public class SolverStateChangeEvent implements ISolverStateChangeEvent{

	private VisuSolverState state;
	private SolverStateEventType type;
	
	public SolverStateChangeEvent(VisuSolverState state,
			SolverStateEventType type) {
		this.type = type;
		this.state = state;
	}

	public SolverStateEventType getType() {
		return type;
	}

	public VisuSolverState getState() {
		return state;
	}

}
