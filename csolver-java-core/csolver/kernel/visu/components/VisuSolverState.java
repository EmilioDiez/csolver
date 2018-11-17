package csolver.kernel.visu.components;

import csolver.kernel.timer.nitoku.SolverLastStepState;
import csolver.kernel.visu.events.SolverStateChangeEvent;
import csolver.kernel.visu.events.SolverStateEventType;

public class VisuSolverState {

	private SolverLastStepState stepState;
	private int solutionCount;
	private ISolverStateHandler iSolverStateHandler;
	
	public SolverLastStepState getSearchStepState() {
		return stepState;
	}

	public void setNoSolutions(int solutionCount) {
		this.solutionCount = solutionCount;		
	}

	public int getNoSolutions() {
		return solutionCount;
	}
	
	public void setSolverStep(SolverLastStepState stepState) {
		this.stepState = stepState;		
	}

	public void throwPropagationEvent() {
		// TODO Auto-generated method stub
		
	}

	public void throwSolutionFoundEvent() {
		
		SolverStateChangeEvent event = new SolverStateChangeEvent(
				this, SolverStateEventType.SOLUTION_FOUND);
		iSolverStateHandler.onRefresh(event);
		
	}

	public void throwNextStepEvent() {
		// TODO Auto-generated method stub		
	}

	public void addSolverStateHandler(ISolverStateHandler iSolverStateHandler) {
		this.iSolverStateHandler =  iSolverStateHandler;
	}
	
}
