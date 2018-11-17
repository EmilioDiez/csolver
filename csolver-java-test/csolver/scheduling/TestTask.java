/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *
 *  Choco Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *    
 */

package csolver.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.comparator.TaskComparators;
import csolver.kernel.common.util.tools.TaskUtils;
import csolver.kernel.common.util.tools.java.awt.NPoint;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.scheduling.AbstractTask;
import csolver.kernel.solver.variables.scheduling.ITask;
import nitoku.log.Logger;

final class SimpleTask extends AbstractTask {

	private static int nextID;

	private final int id;
	private final NPoint domain;

	private final int duration;


    /**
     *
     * @param est
     * @param lst
     * @param duration
     */
	public SimpleTask(final int est, final int lst, final int duration) {
		super();
		id = nextID++;
		this.domain = new NPoint(est, lst>=est ? lst :est);
		this.duration = duration>0 ? duration : 0;
	}

	
	@Override
	public boolean isPreemptionAllowed() {
		return false;
	}


	public final int getID() {
		return id;
	}


	/**
	 * @see ITask#getEST()
	 */
	@Override
	public int getEST() {
		return domain.x;
	}

	/**
	 * @see ITask#getLCT()
	 */
	@Override
	public int getLCT() {
		return domain.y+duration;
	}

	/**
	 * @see ITask#getMinDuration()
	 */
	@Override
	public int getMinDuration() {
		return duration;
	}

	/**
	 * @see ITask#isScheduled()
	 */
	@Override
	public boolean isScheduled() {
		return domain.x==domain.y;
	}

	/**
	 * @see ITask#getMaxDuration()
	 */
	@Override
	public int getMaxDuration() {
		return duration;
	}


}


public class TestTask {

    

	private List<SimpleTask> tasksL;

	public static List<SimpleTask> getExample() {
		List<SimpleTask> tasksL=new ArrayList<SimpleTask>(5);
		tasksL.add(new SimpleTask(0,23,5));
		tasksL.add(new SimpleTask(4,26,6));
		tasksL.add(new SimpleTask(2,16,4));
		tasksL.add(new SimpleTask(3,14,10));
		tasksL.add(new SimpleTask(6,16,7));
		return tasksL;
	}
	
	@Before
	public void initialize() {
		this.tasksL=getExample();
	}

	private void testSort(int[] order) {
		for (int i = 0; i < order.length; i++) {
			assertEquals("sort : ",order[i],tasksL.get(i).getID());
		}
	}

	@Ignore
	public void testTasksComparator() {
		Logger.info(String.valueOf(tasksL));
		Collections.sort(tasksL,TaskComparators.makeEarliestStartingTimeCmp());
		testSort(new int[] {0,2,3,1,4});
		Collections.sort(tasksL, TaskComparators.makeEarliestCompletionTimeCmp());
		testSort(new int[] {0,2,1,3,4});
		Collections.sort(tasksL,TaskComparators.makeLatestStartingTimeCmp());
		testSort(new int[] {3,2,4,0,1});
		Collections.sort(tasksL,TaskComparators.makeLatestCompletionTimeCmp());
		testSort(new int[] {2,4,3,0,1});
		Collections.sort(tasksL,TaskComparators.makeMinDurationCmp());
		testSort(new int[] {2,0,1,4,3});
	}
	
	@Test
	public void testTaskVariable() {
		CPModel m = new CPModel();
		csolver.kernel.model.variables.scheduling.TaskVariable t1 = CSolver.makeTaskVar("T1", 20, 5, Options.V_BOUND);
		csolver.kernel.model.variables.scheduling.TaskVariable t2 = CSolver.makeTaskVar("T2", 20, 8, Options.V_BOUND, Options.V_NO_DECISION);
		csolver.kernel.model.variables.scheduling.TaskVariable t3 = CSolver.makeTaskVar("T3", 25, 8, Options.V_ENUM);
		m.addVariables(t1,t2, t3);
		CPSolver solver =new CPSolver();
		solver.read(m);
		Logger.info(solver.pretty());
		assertEquals(4, solver.getIntDecisionVars().length);
		assertEquals(3, solver.getNbTaskVars());
		assertEquals(2, solver.getTaskDecisionVars().length);
		assertTrue(solver.getVar(t3).start().getDomain().isEnumerated());
	}
	

	@Test
	public void testPreserved() {
		final Solver s = new CPSolver();
		final IntDomainVar x = s.createBoundIntVar("x", 1, 6);
		final IntDomainVar y = s.createBoundIntVar("y", 3, 7);
		assertEquals(30, TaskUtils.getA(x, y));
		assertEquals(56, TaskUtils.getB(x, y));
		assertEquals(6, TaskUtils.getCmin(x, y));
		assertEquals(2, TaskUtils.getCmax(x, y));
		//assertEquals( 24.0/30, TaskUtils.getPreserved(x, y));
	}


}
