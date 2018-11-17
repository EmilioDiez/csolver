/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.timer.nitoku;

import csolver.kernel.timer.nitoku.IObservableStepSearchLoop.Step;



public interface NTimer {
	
	public void start(ISearchTimer searchtimer, ITimerAction launchFinalizeAction);

	public void action(Step action);

}
