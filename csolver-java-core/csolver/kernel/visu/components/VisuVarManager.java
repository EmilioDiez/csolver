/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package csolver.kernel.visu.components;

import java.util.ArrayList;

import csolver.kernel.model.variables.Variable;
import csolver.kernel.solver.variables.Var;


public final class VisuVarManager {

    protected final Var var;
    protected final Variable variable;
    protected final ArrayList<IVisuVar> brick;

    public VisuVarManager(Var var, Variable variable) {
		this.variable = variable;	
        this.var = var;
        brick = new ArrayList<IVisuVar>(16);
    }

    /**
     * Return the solver variable
     * @return
     */
    public Var getSolverVar() {
        return var;
    }

    /**
     * Add a brock observer to the visuvariable
     * @param b
     */
    public final void addBrick(final IVisuVar b){
        brick.add(b);
    }

    /**
     * refresh every visual representation of the variable
     */
    public final void refresh(final Object arg){
        for(IVisuVar b: brick){
            b.refresh(arg);
        }
    }

    public long getIndex() {
        return var.getIndex();
    }

	public Variable getVariable() {
		// TODO Auto-generated method stub
		return variable;
	}
}
