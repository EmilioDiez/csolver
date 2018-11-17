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

package csolver.cp.solver.goals.choice;

import csolver.cp.solver.goals.GoalHelper;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.branch.VarSelector;
import csolver.kernel.solver.goals.Goal;
import csolver.kernel.solver.goals.GoalType;
import csolver.kernel.solver.search.ValIterator;
import csolver.kernel.solver.search.ValSelector;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;


/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Since : Choco 2.0.0
 *
 */
public class Generate implements Goal {

	protected VarSelector<IntDomainVar> varSelector;
  protected ValSelector<IntDomainVar> valSelector;
  protected ValIterator<IntDomainVar> valIterator;
  protected IntDomainVar[] vars;

  public Generate(IntDomainVar[] vars, VarSelector<IntDomainVar> varSelector, ValIterator<IntDomainVar> valIterator) {
    this(vars, varSelector);
    this.valIterator = valIterator;
  }

  public Generate(IntDomainVar[] vars, VarSelector<IntDomainVar> varSelector, ValSelector<IntDomainVar> valSelector) {
    this(vars, varSelector);
    this.valSelector = valSelector;
  }

  public Generate(IntDomainVar[] vars, VarSelector<IntDomainVar> varSelector) {
    this.varSelector = varSelector;
    this.vars = new IntDomainVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
  }

  public Generate(IntDomainVar[] vars) {
    this(vars, new MinDomain(null, vars));
  }

  public String pretty() {
    return "Generate";
  }

  public Goal execute(Solver s) throws ContradictionException {
		Var var;
		var = varSelector.selectVar();
		if (var == null) {
      return null;
    } else {
      if (valSelector != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valSelector), this);
      else if (valIterator != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valIterator), this);
      else return GoalHelper.and(new Instantiate((IntDomainVar) var), this);
    }
	}

    @Override
    public GoalType getType() {
        return GoalType.GEN;
    }
}