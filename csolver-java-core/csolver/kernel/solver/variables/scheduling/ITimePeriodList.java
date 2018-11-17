package csolver.kernel.solver.variables.scheduling;

import csolver.kernel.common.util.tools.java.awt.NPoint;

public interface ITimePeriodList {
	
	void reset();
	
	int getExpendedDuration();
	
	boolean isEmpty();
	
	int getTimePeriodCount(); 

	NPoint getTimePeriod(int i);

	int getPeriodFirst();
	
	int getPeriodStart(int i);

	int getPeriodEnd(int i);
	
	int getPeriodLast();
}