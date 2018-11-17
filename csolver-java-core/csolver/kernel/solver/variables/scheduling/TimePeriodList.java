package csolver.kernel.solver.variables.scheduling;

import java.awt.Point;
import java.util.ArrayList;

import csolver.kernel.common.util.tools.java.awt.NPoint;

public final class TimePeriodList implements ITimePeriodList {

	private final ArrayList<Integer> timePeriods = new ArrayList<Integer>(4);

	private int expendedDuration;

	@Override
	public final void reset() {
		timePeriods.clear();
		expendedDuration = 0;
	}


	public void addTimeLength(int start, int length) {
		if( ! timePeriods.isEmpty() && start == timePeriods.get(timePeriods.size()-2)) {
			timePeriods.set(timePeriods.size()-1, start + length);
		} else {
			timePeriods.add(start);
			timePeriods.add( start + length);
		}
		expendedDuration += length;
	}

	public void addTimePeriod(int start, int end) {
		if( ! timePeriods.isEmpty() && start == getPeriodLast()) {
			timePeriods.set(timePeriods.size()-1, end);
		} else {
			timePeriods.add(start);
			timePeriods.add(end);
		}
		expendedDuration += (end - start);
	}

	public final ArrayList<Integer> getTimePeriods() {
		return timePeriods;
	}

	@Override
	public final int getExpendedDuration() {
		return expendedDuration;
	}

	@Override
	public final boolean isEmpty() {
		return timePeriods.isEmpty();
	}
	@Override
	public final int getTimePeriodCount() {
		return timePeriods.size()/2;
	}

	@Override
	public final NPoint getTimePeriod(int i) {
		final int offset = 2 * i;
		return new NPoint(timePeriods.get(offset), timePeriods.get(offset + 1));
	}

	public final int getPeriodFirst() {
		return timePeriods.get(0);
	}

	public final int getPeriodLast() {
		return timePeriods.get( timePeriods.size() - 1);
	}

	@Override
	public final int getPeriodStart(int i) {
		return timePeriods.get(2 * i);
	}

	@Override
	public final int getPeriodEnd(int i) {
		return timePeriods.get(2 * i + 1);
	}


	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');
		if(! isEmpty()) {
			for (int i = 0; i < getTimePeriodCount(); i++) {
				b.append(getPeriodStart(i)).append("-").append(getPeriodEnd(i)).append(';');
			}
			b.deleteCharAt(b.length() - 1);
		}
		b.append(']');
		return b.toString();
	}





}