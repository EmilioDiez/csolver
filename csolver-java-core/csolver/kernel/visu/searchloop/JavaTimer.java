/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.visu.searchloop;

import csolver.kernel.solver.search.AbstractSearchLoop.SearchLoopStep;
import csolver.kernel.timer.nitoku.ISearchTimer;
import csolver.kernel.timer.nitoku.ITimerAction;
import csolver.kernel.timer.nitoku.NTimer;
import csolver.kernel.timer.nitoku.IObservableStepSearchLoop.Step;


public class JavaTimer implements NTimer {
	
	@Override
	public void start(ISearchTimer searchtimer, ITimerAction launchFinalizeAction) {
		while( true ){
			
			SearchLoopStep step = searchtimer.timerRun();
			if ( step == SearchLoopStep.LOOP_SEARCH_ENDED){
				break;
			}
			//if ( step == SearchLoopStep.ONE_SOLUTIONS_SEARCH_ENDED){
			//	break;
			//}
			if ( step == SearchLoopStep.SOLUTIONS_SEARCH_ENDED){
				break;
			}
		};
		searchtimer.timerEnd(launchFinalizeAction);	
	}

//	public void pause(int i) {
//		/*wait*/
//		//try {
//		//	Thread.sleep(i);
//		//} catch (InterruptedException e) {
//		//	Logger.log(NLevel.SEVERE, "The search was interrupted while waiting for a GUI event" , e);
//		//}		
//	}

	@Override
	public void action(Step action) {
		// TODO Auto-generated method stub
		
	}



}