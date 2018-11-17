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
/**
 *
 */
package common;

import static csolver.kernel.solver.Configuration.BOTTOM_UP;
import static csolver.kernel.solver.Configuration.INIT_DESTRUCTIVE_LOWER_BOUND;
import static csolver.kernel.solver.Configuration.INIT_SHAVING;
import static csolver.kernel.solver.Configuration.NOGOOD_RECORDING_FROM_RESTART;
import static csolver.kernel.solver.Configuration.RESTART_AFTER_SOLUTION;
import static csolver.kernel.solver.Configuration.RESTART_BASE;
import static csolver.kernel.solver.Configuration.RESTART_LUBY;
import static csolver.kernel.solver.Configuration.STOP_AT_FIRST_SOLUTION;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import csolver.cp.solver.CPSolver;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.ResolutionPolicy;
import nitoku.log.Logger;
import samples.tutorials.to_sort.MinimumEdgeDeletion;

public class TestStrategyMed {

    

    private final MinimumEdgeDeletion med = new MedShaker();

    private Number objective = null;

    private final static Configuration CONFIG = new Configuration();

    @Before
    public void setUp() {
        CONFIG.putFalse(STOP_AT_FIRST_SOLUTION);
        CONFIG.putEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.MINIMIZE);
        CONFIG.putInt(RESTART_BASE, 1);//many restarts, bad performance but good testing !
    }

    private void solve(boolean randomSelectors) {
        med.buildSolver();
        if (randomSelectors) ((CPSolver) med.solver).setRandomSelectors(0);
        med.solve();
        med.prettyOut();
        assertEquals("Minimum Edge Deletion is Feasible", Boolean.TRUE, med.solver.isFeasible());
        if (objective == null) {
            objective = med.solver.getOptimumValue();
        } else {
            assertEquals("objective", objective, med.solver.getOptimumValue());
        }
    }

    private void recursiveTestMED(Object parametersMED, String... confBoolValues) {
        if (confBoolValues != null && confBoolValues.length > 0) {
            final int n = confBoolValues.length - 1;
            final String[] newConfboolValues = Arrays.copyOf(confBoolValues, n);
            CONFIG.putFalse(confBoolValues[n]);
            recursiveTestMED(parametersMED, newConfboolValues);
            CONFIG.putTrue(confBoolValues[n]);
            recursiveTestMED(parametersMED, newConfboolValues);
        } else {
            //configuration is set: solve instance
            Logger.info(CONFIG.toString());
            solve(false);
            solve(true);
        }

    }

    /**
     * shake a little bit the optimization options.
     *
     * @param parametersMED parameters of the  minimum edge deletion
     */
    //public void testMED(String... parametersMED) {
        //CONFIG.clear();
    //    med.readArgs(parametersMED);

    //}

    @Test
    public void testMinimumEquivalenceDetection1() {
        testMED(6, 0.7,2);
    }

    @Test
    public void testMinimumEquivalenceDetection2() {
        testMED(8,0.6);
    }

    @Test
    public void testMinimumEquivalenceDetection3() {
        testMED(9,0.6,6);

    }

    private void testMED(int i, double d, int j) {
		// TODO Auto-generated method stub
		
	}

	@Test
    //@Ignore
    public void testMinimumEquivalenceDetection4() {
        testMED(10, 0.9, 1);
    }

    @Test
    @Ignore
    public void testLargeMinimumEquivalenceDetection() {
        testMED(15, 0.4);
    }

    private void testMED(int i, double d) {
    	
    	med.readArgs(i,d);
        med.buildModel();
        objective = null;
        recursiveTestMED(i,d,
                RESTART_LUBY, RESTART_AFTER_SOLUTION, NOGOOD_RECORDING_FROM_RESTART
                , BOTTOM_UP
                , INIT_SHAVING, INIT_DESTRUCTIVE_LOWER_BOUND
        );
		
	}

	private void recursiveTestMED(int i, double d, String restartLuby,
			String restartAfterSolution, String nogoodRecordingFromRestart,
			String bottomUp, String initShaving,
			String initDestructiveLowerBound) {
		// TODO Auto-generated method stub
		
	}

	class MedShaker extends MinimumEdgeDeletion {

        @Override
        public void buildSolver() {
            solver = new CPSolver(CONFIG);
            solver.monitorFailLimit(true);
            solver.read(model);
        }
    }
}




