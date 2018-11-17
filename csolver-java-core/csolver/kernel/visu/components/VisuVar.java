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

import java.util.BitSet;

import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.model.variables.Variable;
import csolver.kernel.solver.variables.Var;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import csolver.kernel.solver.variables.set.SetVar;
import csolver.kernel.visu.events.VarChangeEvent;


public final class VisuVar implements IVisuVar {

    private BitSet values;
    private int capacity;
    private int offset;
    private DisposableIntIterator it;
    
    /**
     * The {@code Var} represented by the brick
     */
    protected Var var;
    
    protected Variable variable;
	private IVisuVarHandler brickHandler;
    
    public VisuVar(Variable v) {
		this.variable = v;
	}

    public void init(final Var var)  {
    	this.var = var;
        final int lb = getLowBound();
        final int up = getUppBound();
        this.capacity = up - lb;
        this.offset = -lb;
        this.values = new BitSet(capacity);
        this.it = getDomainValues();
        while(it.hasNext()){
            this.values.set(it.next()+offset);
        }
        it.dispose();
    }
    
    /**
     * Return the var of the brick
     *
     * @return
     */
    public final Var getVar() {
        return var;
    }

    /**
     * Return the domain size of a variable
     * @return the domain size
     */
    public final int getVarSize() {
        if(var instanceof IntDomainVar){
            return((IntDomainVar)var).getDomainSize();
        }
        if (var instanceof SetVar) {
            return ((SetVar) var).getEnveloppeDomainSize();
        }
        return 0;
    }

    /**
     * Return the lower bound of a variable
     * @return the domain size
     */
    public final int getLowBound() {
        if(var instanceof IntDomainVar){
            return((IntDomainVar)var).getInf();
        }
        if (var instanceof SetVar) {
            return ((SetVar) var).getEnveloppeInf();
        }
        return 0;
    }


    /**
     * Return the upper bound of a variable
     * @return the domain size
     */
    public final int getUppBound() {
        if(var instanceof IntDomainVar){
            return((IntDomainVar)var).getSup();
        }
        if (var instanceof SetVar) {
            return ((SetVar) var).getEnveloppeSup();
        }
        return 0;
    }

    /**
     * Return an IntIterator over the values of a specific variable
     *
     * @return an IntIterator
     */
    public final DisposableIntIterator getDomainValues() {
        if (var instanceof IntDomainVar) {
            return ((IntDomainVar) var).getDomain().getIterator();
        }else
        if (var instanceof SetVar) {
            return ((SetVar) var).getDomain().getEnveloppeIterator();
        }
        return null;
    }

    /**
     * Return a string that represents the instantiated values of {@code Var}.
     * @return
     */
    public final String getValues(){
        StringBuffer values = new StringBuffer(128);
        final DisposableIntIterator it = getDomainValues();
        while(it.hasNext()){
            int value = it.next();
            values.append(value).append(" - ");
        }
        it.dispose();
        values = values.delete(values.length()-3, values.length());
        return values.toString();
    }


	/**
     *
     * @param arg an object to precise the refreshing
     */
    public final void refresh(final Object arg) {
        values.clear();
        //System.out.print("Var name: " + this.getVar().getName());
        it = getDomainValues();
        int i;
        while(it.hasNext()){
        	i = it.next()+offset;
            values.set(i);
            //if(values.get(i)){
            //  System.out.print("," + i + " ");
            //}            
         }       
        //System.out.println(" ");
        //varmanager.drawBrick(index);
        if ( brickHandler != null){
        	VarChangeEvent event = new VarChangeEvent(this);
			brickHandler.onRefresh(event);
        }
    }

	public void addBrickHandler(IVisuVarHandler brickHandler) {
		this.brickHandler = brickHandler;
		
	}

	public Variable getVariable() {
		return variable;
	}

}
