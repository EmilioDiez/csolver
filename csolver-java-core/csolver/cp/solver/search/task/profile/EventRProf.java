package csolver.cp.solver.search.task.profile;

import csolver.cp.solver.search.task.profile.ProbabilisticProfile.EventType;
import csolver.kernel.solver.variables.scheduling.ITask;

/**
 * Event for the resource probabilistic profile
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 *
 */
public class EventRProf implements Comparable<EventRProf>{

	public final EventType type;

	public final ITask task;

	public int coordinate;

	public double slope;

	public double gap;

	public EventRProf(final EventType type) {
		this(type, null);
	}

	public EventRProf(final EventType type,final ITask task) {
		this(type, 0, 0, 0,task);
	}

	/**
	 * @param type
	 * @param coordinates
	 * @param slope
	 * @param gap
	 */
	public EventRProf(final EventType type, final int coordinates, final double slope, final double gap,final ITask task) {
		super();
		this.type = type;
		this.coordinate = coordinates;
		this.slope = slope;
		this.gap = gap;
		this.task=task;
	}



	/**
	 * @return the coordinate
	 */
	public final int getCoordinates() {
		return coordinate;
	}


	/**
	 * @param coordinates the coordinate to set
	 */
	public final void setCoordinates(final int coordinates) {
		this.coordinate = coordinates;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder buffer=new StringBuilder();
		buffer.append(type);
		buffer.append(coordinate).append("(");
		buffer.append(slope).append(',').append(gap).append(')');
		return buffer.toString();
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final EventRProf o) {
		final int x1=coordinate;
		final int x2=o.getCoordinates();
		if(x1<x2) {return -1;}
		else if(x1>x2) {return 1;}
		else {
			return 0;
		}
	}
}