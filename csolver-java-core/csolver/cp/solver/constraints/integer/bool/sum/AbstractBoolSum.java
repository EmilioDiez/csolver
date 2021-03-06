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

package csolver.cp.solver.constraints.integer.bool.sum;

import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.kernel.memory.IEnvironment;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import csolver.kernel.solver.propagation.event.ConstraintEvent;
import csolver.kernel.solver.variables.integer.IntDomainVar;

/**
 * A special case of sums over boolean variables only
 */
public class AbstractBoolSum extends AbstractLargeIntSConstraint {

	protected final BoolSumStructure boolSumS;

	public AbstractBoolSum(IEnvironment environment, IntDomainVar[] vars, int bValue) {
		super(ConstraintEvent.LINEAR, vars);
		this.boolSumS = new BoolSumStructure(environment, this, vars, bValue);
	}


	@Override
	public int getFilteredEventMask(int idx) {
		return IntVarEvent.INSTINT_MASK;
	}

	@Override
	public void propagate() throws ContradictionException {
		boolSumS.reset();
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].isInstantiated()) {
				awakeOnInst(i);
			}
		}
	}

	

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		final int val = vars[idx].getVal();
		if (val == 0) boolSumS.addZero();
		else boolSumS.addOne();
	}


}
