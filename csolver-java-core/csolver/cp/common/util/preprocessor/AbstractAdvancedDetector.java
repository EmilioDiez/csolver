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

package csolver.cp.common.util.preprocessor;

import java.util.HashMap;
import java.util.logging.Level;

import csolver.cp.model.CPModel;
import csolver.kernel.model.constraints.Constraint;
import csolver.kernel.model.variables.IHook;
import csolver.kernel.model.variables.Variable;
import nitoku.log.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * An abstract class to set the methods of a detector.
 * A detector analyzes a model and thanks to its analizis, it is allowed to strongly modified the model involved. 
 */
public abstract class AbstractAdvancedDetector extends AbstractDetector{

    /**
     * Internal structure to store constraint addition instructions
     */
    protected final HashMap<Long,Constraint> constraintsToAdd;

    /**
     * Internal structure to store constraint deletion instructions
     */
    protected final HashMap<Long,Constraint> constraintsToDelete;

    /**
     * Internal structure to store variable addition instructions
     */
    protected final HashMap<Long,Variable> variablesToAdd;

    /**
     * Internal structure to store variable deletion instructions
     */
    protected final HashMap<Long,Variable> variablesToDelete;

    /**
     * Internal structure to store variable deletion instructions
     */
    protected final HashMap<Variable, Variable> variablesToReplace;

    protected AbstractAdvancedDetector(CPModel model) {
        super(model);
        constraintsToAdd = new HashMap<Long,Constraint>();
        constraintsToDelete = new HashMap<Long,Constraint>();
        variablesToAdd = new HashMap<Long,Variable>();
        variablesToDelete = new HashMap<Long,Variable>();
        variablesToReplace = new HashMap<Variable, Variable>();
    }

    /**
     * Apply the detection defined within the detector.
     */
    public abstract void apply();

    /**
     * Add a constraint {@code c} to the model which is currently treated by the detector.<br/>
     * The addition is recorded but not done immediatly. It must be "commited" using {@link AbstractAdvancedDetector#commit()}.
     * @param c contraint to add
     */
    protected final void add(Constraint c){
        constraintsToAdd.put(c.getIndex(), c);
    }

    /**
     * Delete a constraint {@code c} to the model which is currently treated by the detector.<br/>
     * The deletion is recorded but not done immediatly. It must be "commited" using {@link AbstractAdvancedDetector#commit()}.
     * @param c contraint to delete
     */
    protected final void delete(Constraint c){
        constraintsToDelete.put(c.getIndex(), c);
    }

    /**
     * Remove deletion instruction on {@code c}.
     * @param c contraint to keep
     */
    protected final void keep(Constraint c){
        constraintsToDelete.remove(c.getIndex());
    }

    /**
     * Remove addition instruction on {@code c}.
     * @param c contraint to not add
     */
    protected final void forget(Constraint c){
        constraintsToAdd.remove(c.getIndex());
    }

    /**
     * Add a variable {@code v} to the model which is currently treated by the detector.<br/>
     * The addition is recorded but not done immediatly. It must be "commited" using {@link AbstractAdvancedDetector#commit()}.
     * @param v variable to add
     */
    protected final void add(Variable v){
        variablesToAdd.put(v.getIndex(), v);
    }

    /**
     * Delete a variable {@code v} from the model which is currently treated by the detector.<br/>
     * The deletion is recorded but not done immediatly. It must be "commited" using {@link AbstractAdvancedDetector#commit()}.
     * @param v variable to delete
     */
    protected final void delete(Variable v){
        variablesToDelete.put(v.getIndex(), v);
    }

    /**
     * Remove deletion instruction on {@code v}.
     * @param v variable to keep
     */
    protected final void keep(Variable v){
        variablesToDelete.remove(v.getIndex());
    }

    /**
     * Remove addition instruction on {@code v}.
     * @param v contraint to not add
     */
    protected final void forget(Variable v){
        variablesToAdd.remove(v.getIndex());
    }

    /**
     * Replace {@code outVar} by {@code inVar} in every constraint where {@code outVar} is involved.
     * @param outVar deleted variable
     * @param inVar the substitute
     */
    protected final void replaceBy(Variable outVar, Variable inVar){
        variablesToReplace.put(outVar, inVar);
    }

    /**
     * Send changes detected to the treated model.
     */
    public final void commit(){
        for(long l : variablesToAdd.keySet()){
            final Variable v = variablesToAdd.get(l);
            model.addVariables(v);
            if(Logger.isLoggable(Level.CONFIG)) {
                Logger.config("..add variable : " + v.pretty());
            }
        }
        for(long l : constraintsToAdd.keySet()){
            final Constraint c = constraintsToAdd.get(l);
            model.addConstraint(c);
            if(Logger.isLoggable(Level.CONFIG)) {
                Logger.config("..add constraint : " + c.pretty());
            }
        }
        for(long l : constraintsToDelete.keySet()){
            final Constraint c = constraintsToDelete.get(l);
            model.removeConstraint(c);
            if(Logger.isLoggable(Level.CONFIG)) {
                Logger.config("..delete constraint : " +  c.pretty());
            }
        }
        for(Variable outVar : variablesToReplace.keySet()){
            final Variable inVar = variablesToReplace.get(outVar);
            for(Constraint c : outVar.getConstraints()){
                c.replaceBy(outVar, inVar);
                if(!inVar._contains(c)){
                    inVar._addConstraint(c);
                }
                outVar._removeConstraint(c);
            }
            if( inVar.getHook() == IHook.NO_HOOK) {
                inVar.setHook(outVar.getHook());
            }
            if(Logger.isLoggable(Level.CONFIG)) {
                Logger.config(".."+  outVar.getName() +" replaced by : " + inVar.getName());
            }
        }
        for(long l : variablesToDelete.keySet()){
            final Variable v = variablesToDelete.get(l);
            model.removeVariable(v);
            if(Logger.isLoggable(Level.CONFIG)) {
                Logger.config("..delete variable : " +  v.pretty());
            }
        }  
    }

    /**
     * Remove all uncommited instructions.
     */
    public final void rollback(){
        constraintsToAdd.clear();
        constraintsToDelete.clear();
        variablesToAdd.clear();
        variablesToDelete.clear();
        variablesToReplace.clear();
    }
}
