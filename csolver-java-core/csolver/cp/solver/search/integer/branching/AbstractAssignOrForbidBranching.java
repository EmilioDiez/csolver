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

package csolver.cp.solver.search.integer.branching;

import csolver.kernel.solver.branch.AbstractBinIntBranchingStrategy;
import csolver.kernel.solver.search.IntBranchingDecision;
import csolver.kernel.solver.search.ValSelector;

public abstract class AbstractAssignOrForbidBranching extends AbstractBinIntBranchingStrategy {


	// L'heuristique pour le svaleurs
	protected ValSelector valSelector;


	public AbstractAssignOrForbidBranching(ValSelector valSelector) {
		super();
		this.valSelector = valSelector;
	}

	@Override
	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valSelector.getBestVal(decision.getBranchingIntVar()));
	}


	@Override
	public String getDecisionLogMessage(final IntBranchingDecision decision) {
		return getDefaultAssignOrForbidMsg(decision);
	}

}
