/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.timer.nitoku;

import csolver.kernel.solver.search.AbstractSearchLoop;




public interface ISearchTimer {

	public AbstractSearchLoop.SearchLoopStep timerRun();
	
	public void timerEnd(ITimerAction launchFinalizeAction);
	
}
