/**
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
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
package csolver.client.solver;

import csolver.cp.solver.CPSolver;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;

public class Solver {

	protected CPSolver cps;

	@JsConstructor
	public Solver(){
		cps = new CPSolver();
	}
	
	@JsMethod
	public void solveAll() {
		cps.solveAll();		
	}

	@JsMethod
	public void printRuntimeStatistics() {
		cps.printRuntimeStatistics();
		
	}

	@JsMethod
	public int getNbSolutions() {
		return cps.getNbSolutions();
	}

	@JsMethod
	public int getBackTrackCount() {
		return cps.getBackTrackCount();
	}

	@JsMethod
	public int getTimeCount() {
		return cps.getTimeCount();
	}

	@JsMethod
	public int getNodeCount() {
		return cps.getNodeCount();
	}

	@JsMethod
	public void read(Model m) {
		cps.read(m.getModel());
		
	}

}
