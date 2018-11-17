/**
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
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

package csolver.cp.common.util.preprocessor.detector.scheduling;

import csolver.cp.common.util.preprocessor.AbstractAdvancedDetector;
import csolver.cp.model.CPModel;
import csolver.kernel.model.variables.MultipleVariables;
import csolver.kernel.model.variables.scheduling.TaskVariable;

public final class PrecFromTimeWindowModelDetector extends AbstractAdvancedDetector {

	public final DisjunctiveModel disjMod;

	public PrecFromTimeWindowModelDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model);
		this.disjMod = disjMod;
	}

	@Override
	public void apply() {
		assert disjMod.isEmpty();
		int n = model.getNbStoredMultipleVars();
		for (int i = 0; i < n; i++) {
			MultipleVariables mv = model.getStoredMultipleVar(i);
			if (mv instanceof TaskVariable) {
				TaskVariable t1 = (TaskVariable) mv;
				for (int j = i+1; j < n; j++) {
					mv = model.getStoredMultipleVar(j);
					if (mv instanceof TaskVariable) {
						TaskVariable t2 = (TaskVariable) mv;
						disjMod.safeAddArc(t1, t2);
						disjMod.safeAddArc(t2, t1);
					}
				}
			}
		}

	}
}