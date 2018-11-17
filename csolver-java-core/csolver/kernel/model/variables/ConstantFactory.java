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

package csolver.kernel.model.variables;

import java.util.HashMap;

import csolver.kernel.model.variables.integer.IntegerConstantVariable;
import csolver.kernel.model.variables.real.RealConstantVariable;
import csolver.kernel.model.variables.set.SetConstantVariable;
import nitoku.log.Logger;

public final class ConstantFactory {

    

    private final static HashMap<Integer,IntegerConstantVariable> INTEGER_MAP = new HashMap<Integer,IntegerConstantVariable>();
    private final static HashMap<int[], SetConstantVariable> SET_MAP = new HashMap<int[], SetConstantVariable>();
    private final static HashMap<Double,RealConstantVariable> REAL_MAP = new HashMap<Double,RealConstantVariable>();

    
    
    protected ConstantFactory() {
		super();
    }

	/**
     * Create if necessary and return the IntegerConstantVariable that
     * corresponds to the value
     * @param value int constant value
     * @return IntegerConstantVariable
	 * @throws Exception 
     */
    public static IntegerConstantVariable getConstant(int value) {
        if(INTEGER_MAP.get(0)!=null
                &&INTEGER_MAP.get(0).getValue() !=0){
            Logger.severe(ConstantFactory.class,"IntegerConstantVariable Error : $$$$$$$$$$$$$ ALARM $$$$$$$$$$$$$$$$$$$$$$$$$$");
        }
        if(!INTEGER_MAP.containsKey(value)){
            INTEGER_MAP.put(value, new IntegerConstantVariable(value));
        }
        return INTEGER_MAP.get(value);
    }

    /**
     * Create if necessary and return the SetConstantVariable that
     * corresponds to arrays of value
     * @param values set constant values
     * @return SetConstantVariable
     * @throws Exception 
     */
    public static SetConstantVariable getConstant(int[] values){
        if(!SET_MAP.containsKey(values)){
            SET_MAP.put(values, new SetConstantVariable(getConstant(values.length), values));
        }
        return SET_MAP.get(values);
    }

    /**
     * Create if necessary and return the RealConstantVariable that
     * corresponds to the value
     * @param value double real value
     * @return RealConstantVariable
     */
    public static RealConstantVariable getConstant(double value){
        if(!REAL_MAP.containsKey(value)){
            REAL_MAP.put(value, new RealConstantVariable(value));
        }
        return REAL_MAP.get(value);
    }

}
