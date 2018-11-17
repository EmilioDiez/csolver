/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
*  

 * 
 */
package csolver.kernel.timer.nitoku;

import csolver.kernel.IObservable;
import csolver.kernel.solver.search.ISearchLoop;

public interface IObservableStepSearchLoop extends ISearchLoop, IObservable{

    static enum Step{
        PAUSE, NEXT, PLAY, STOP, NEXTSOL
    }

    /**
     * Action to do in a step-by-step run loop
     */
    public void runStepByStep();

    /**
     * Action to do in a normal run loop
     */
    public void runForAWhile();

    /**
     * Pause the normal run loop
     */
    public void pause();

    public void setAction(Step action);
}
