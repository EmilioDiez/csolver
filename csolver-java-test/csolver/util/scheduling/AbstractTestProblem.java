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

package csolver.util.scheduling;

import java.util.Random;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.scheduling.AbstractResourceSConstraint;
import csolver.cp.solver.preprocessor.PreProcessCPSolver;
import csolver.cp.solver.preprocessor.PreProcessConfiguration;
import csolver.cp.solver.preprocessor.SolverDetectorFactory;
import csolver.kernel.CSolver;
import csolver.kernel.Options;
import csolver.kernel.common.util.bitmask.BitMask;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.scheduling.TaskVariable;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.constraints.global.MetaSConstraint;
import nitoku.log.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public abstract class AbstractTestProblem {

	

	public CPModel model;

	public CPSolver solver;

	public Constraint rsc;

	public IntegerVariable[] starts;

	public IntegerVariable[] durations;

	public TaskVariable[] tasks;

	public int horizon = 10000000;

	private static final Random RANDOM =  new Random();


	public AbstractTestProblem(final IntegerVariable[] starts, final IntegerVariable[] durations) {
		super();

		if(starts != null)
		this.starts = starts;
		
		if(durations != null)
			this.durations = durations;
	}


	public final void setFlags(final BitMask flags) {
		final SConstraint cstr = solver.getCstr(this.rsc);
		BitMask dest = null;
		if (cstr instanceof AbstractResourceSConstraint) {
			dest = ( (AbstractResourceSConstraint) cstr).getFlags();
		} else if (cstr instanceof MetaSConstraint) {
			dest = ( (AbstractResourceSConstraint) ( (MetaSConstraint) cstr).getSubConstraints(0)).getFlags();
		} 
		if(dest != null) {
			dest.clear();
			dest.set(flags);
		}else Logger.info("can not apply resource filtering rules");
	}


	public final static Configuration getConfig(boolean b) {
		final PreProcessConfiguration config = new PreProcessConfiguration();
		PreProcessConfiguration.cancelPreProcess(config);
		config.putTrue(PreProcessConfiguration.DISJUNCTIVE_MODEL_DETECTION);
		config.putTrue(PreProcessConfiguration.DMD_USE_TIME_WINDOWS);
		if(b) {
			config.putTrue(PreProcessConfiguration.DMD_REMOVE_DISJUNCTIVE);
			//can change the number of solutions if it substitutes a cumulative with variable height by a disjunctive. 
			//config.putTrue(PreProcessConfiguration.DISJUNCTIVE_FROM_CUMULATIVE_DETECTION);
		}
		return config;
	}

	public void generateSolver(Configuration conf) {
		SolverDetectorFactory.resetIndexes(model);
		solver = conf == null ? new CPSolver() : new PreProcessCPSolver(conf);
		solver.setHorizon(horizon);
		solver.read(model);	
	}

	protected abstract Constraint[] generateConstraints();

	public void initializeModel() {
		model = new CPModel();
		initializeTasks();
		final Constraint[] cstr = generateConstraints();
		if(cstr!=null) {
			rsc = cstr[0];
			model.addConstraints(cstr);
		}else {
			Logger.severe(AbstractTestProblem.class,"no model constraint ?");
		}

	}

	public void initializeTasks() {
		if(starts==null) { tasks= CSolver.makeTaskVarArray("T", 0, horizon, durations);}
		else {
			tasks=new TaskVariable[durations.length];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i]= CSolver.makeTaskVar(String.format("T_%d", i), starts[i],
						CSolver.makeIntVar(String.format("end-%d", i), 0, horizon, Options.V_BOUND), durations[i]);
			}
		}
	}

	public void setHorizon(final int horizon) {
		this.horizon = horizon;
	}

	protected void horizonConstraints(final IntegerVariable[] starts, final IntegerVariable[] durations) {
		if (horizon > 0) {
			for (int i = 0; i < starts.length; i++) {
				model.addConstraint(CSolver.geq(horizon, CSolver.plus(starts[i], durations[i])));
			}
		}
	}

	public IntegerVariable[] generateRandomDurations(final int n) {
		final IntegerVariable[] durations = new IntegerVariable[n];
		final int gap = horizon / n;
		int max = gap + horizon % n;
		for (int i = 0; i < n - 1; i++) {
			final int v = RANDOM.nextInt(max) + 1;
			max += gap - v;
			durations[i] = CSolver.constant(v);
		}
		durations[n - 1] = CSolver.constant(max);
		return durations;
	}

	public void setRandomProblem(final int size) {
		starts = null;
		durations = generateRandomDurations(size);
	}
}
