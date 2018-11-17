package csolver.kernel.visu.components;

import csolver.kernel.visu.events.SolverStateEventType;

public interface ISolverStateChangeEvent {
	
	public VisuSolverState getState();
	public SolverStateEventType getType();
	
	
}
