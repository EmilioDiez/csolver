/*
 *  Javascript Constraint Solver (Csolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *  Choco Copyright (c) 1999-2010-2011, Ecole des Mines de Nantes
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

package csolver.cp.model.managers.constraints.integer;

import java.util.List;

import csolver.cp.model.managers.MixedConstraintManager;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.integer.MaxOfAList;
import csolver.cp.solver.constraints.integer.MaxXYZ;
import csolver.cp.solver.constraints.integer.MinOfAList;
import csolver.cp.solver.constraints.integer.MinXYZ;
import csolver.cp.solver.constraints.set.MaxOfASet;
import csolver.cp.solver.constraints.set.MinOfASet;
import csolver.cp.solver.constraints.set.AbstractBoundOfASet.EmptySetPolicy;
import csolver.kernel.Options;
import csolver.kernel.common.util.tools.VariableUtils;
import csolver.kernel.memory.IEnvironment;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.model.variables.set.SetVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.set.SetVar;


/**
 * A manager to build min or max constraints
 */
public final class MinMaxManager extends MixedConstraintManager {



	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, List<String> options) {
		if (solver instanceof CPSolver) {
			if (parameters instanceof Boolean) {
				return buildConstraint(solver, variables, (Boolean) parameters, options, null);
			}
		}	
		return null;
	}


	/**
	 * Build a constraint and its opposite for the given solver and "model variables"
	 *
	 * @param solver
	 * @param variables
	 * @param parameters
	 * @param options
	 * @return array of 2 SConstraint object, the constraint and its opposite
	 */
	//    @Override
	public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, List<String> options) {
		if (solver instanceof CPSolver) {
			final SConstraint[] cs = new SConstraint[2];
			if (parameters instanceof Boolean) {
				final IntDomainVar X = solver.getVar( (IntegerVariable) variables[0]);
				// Introduces a intermediary variable
				IntDomainVar Y  = (
						X.hasBooleanDomain() ? 
								solver.createBooleanVar("Y_opp") : 
									X.hasEnumeratedDomain() ? 
											solver.createEnumIntVar("Y_opp", X.getInf(), X.getSup()) : 
												solver.createBoundIntVar("Y_opp", X.getInf(), X.getSup()));

				solver.post(buildConstraint(solver, variables, (Boolean) parameters, options, Y));
				cs[0] = solver.eq(Y, X);
				cs[1] = solver.neq(Y, X);
				return cs;
			}
		}
		return null;
	}

	public SConstraint buildConstraint(Solver solver, Variable[] variables, Boolean parameter, List<String> options, IntDomainVar varM) {
		final IEnvironment environment = solver.getEnvironment();
		if (variables[0] instanceof SetVariable) {
			final SetVar svar = solver.getVar((SetVariable) variables[0]);
			final IntDomainVar[] ivars = VariableUtils.getIntVar(solver, variables, 1, variables.length);
			if(varM != null) {ivars[0]=varM;}
			final EmptySetPolicy emptySetPolicy = options.contains(Options.C_MINMAX_INF) ? EmptySetPolicy.INF : 
				options.contains(Options.C_MINMAX_SUP) ? EmptySetPolicy.SUP : EmptySetPolicy.NONE;
			return parameter ? new MinOfASet(environment, ivars, svar, emptySetPolicy) :
				new MaxOfASet(environment, ivars, svar, emptySetPolicy);
		} else {
			final IntDomainVar[] ivars = VariableUtils.getIntVar(solver, variables, 0, variables.length);
			if(varM != null) {ivars[0]=varM;}
			if(ivars.length == 2) {
				return solver.eq(ivars[0], ivars[1]);
			} else if(ivars.length == 3) {
				return parameter ? new MinXYZ(ivars[1], ivars[2], ivars[0]) : new MaxXYZ(ivars[1], ivars[2], ivars[0]);
			} else {
				return parameter ? new MinOfAList(environment, ivars) : new MaxOfAList(environment, ivars);
			}
		}
	}
}



