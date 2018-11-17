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

package csolver.kernel.solver.constraints.set;


import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.set.SetVar;


/**
 * A class to represent a large constraint including both set and int variables in
 * its scope.
 **/
public abstract class AbstractLargeSetIntSConstraint extends AbstractMixedSetIntSConstraint {

	/**
	 * The set variables representing the first part of the scope of the constraint.
	 */
	public SetVar[] svars;

	/**
	 * The int variables representing the rest scope of the constraint.
	 */
	public IntDomainVar[] ivars;

	public AbstractLargeSetIntSConstraint(IntDomainVar[] intvars, SetVar[] setvars) {
		super(ArrayUtils.append(setvars, intvars));
		this.ivars= intvars;
		this.svars = setvars;
	}

	public final int getNbSetVars() {
		return svars.length;
	}
	
	public final int getNbIntVars() {
		return ivars.length;
	}
	
	/**
	 * @return the relative index of an integer variable
	 */
	protected int getIntVarIndex (int i) {
	    return i - svars.length;
	}

	public final boolean isSetVarIndex(int i) {
		return i < svars.length;
	}
	
	public final boolean isIntVarIndex(int i) {
		return i >= svars.length;
	}

}