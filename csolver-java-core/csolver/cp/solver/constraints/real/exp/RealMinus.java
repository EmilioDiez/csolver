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

package csolver.cp.solver.constraints.real.exp;

import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.real.RealExp;
import csolver.kernel.solver.constraints.real.exp.AbstractRealBinTerm;
import csolver.kernel.solver.variables.real.RealInterval;
import csolver.kernel.solver.variables.real.RealMath;

/**
 * An expression modelling a substraction.
 */
public final class RealMinus extends AbstractRealBinTerm {
  public RealMinus(Solver solver, RealExp exp1, RealExp exp2) {
    super(solver, exp1, exp2);
  }

   public String pretty() {
    return "("+exp1.pretty() + " - " + exp2.pretty()+")";
  }

  public void tighten() {
    RealInterval res = RealMath.sub(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  public void project() throws ContradictionException {
    exp1.intersect(RealMath.add(this, exp2));
    exp2.intersect(RealMath.sub(exp1, this));
  }
}
