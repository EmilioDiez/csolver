/**
 *  
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
 *  
 */
package csolver.cp.model.managers.constraints.global;

import csolver.cp.model.managers.IntConstraintManager;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.constraints.global.regular.Regular;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.model.ModelException;
import csolver.kernel.model.constraints.automaton.DFA;
import csolver.kernel.model.constraints.automaton.Transition;
import csolver.kernel.model.variables.integer.IntegerVariable;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.constraints.SConstraint;
import csolver.kernel.solver.variables.integer.IntDomainVar;

import java.util.*;

/**
 * A manager to build new all stretchpath constraints
 */
public final class StretchPathManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {
        if (solver instanceof CPSolver) {

            if (parameters instanceof List) {
                List<int[]> stretchParameters = (List<int[]>)parameters;

                IntDomainVar[] vars = solver.getVar(variables);

                IntDomainVar[] tmpVars = new IntDomainVar[vars.length];
                System.arraycopy(vars, 0, tmpVars, 0, vars.length);

                ArrayList<Integer> alphabet = new ArrayList<Integer>();
                for (int i = 0; i < vars.length; i++) {
                    DisposableIntIterator it = tmpVars[i].getDomain().getIterator();
                    for ( ; it.hasNext();) {
                        int val = it.next();
                        if (!alphabet.contains(val)) {
                            alphabet.add(val);
                        }
                    }
                    it.dispose();
                }

                int nbStates = 1;
                Map<Integer, Integer> tab = new HashMap<Integer, Integer>();
                List<Transition> t = new LinkedList<Transition>();
                List<Integer> fs = new LinkedList<Integer>();
                fs.add(0);

                for (int[] vals : stretchParameters) {
                    int valState = nbStates++;
                    tab.put(vals[0], valState);
                    t.add(new Transition(0, vals[0], valState));
                    if (vals[1] == 1) {
                        fs.add(valState);
                    }
                }

                for (Integer val : alphabet) {
                    if (!tab.containsKey(val)) {
                        t.add(new Transition(0, val, 0));
                    }
                }

                for (int[] vals : stretchParameters) {
                    int lastState = tab.get(vals[0]);
                    for (int j = 2; j <= vals[2]; j++) {
                        int newState = nbStates++;
                        t.add(new Transition(lastState, vals[0], newState));
                        if ((j > vals[1])) {
                            for (int i1 = 0; i1 < alphabet.size(); i1++) {
                                Object anAlphabet = alphabet.get(i1);
                                int val = (Integer) anAlphabet;
                                if ((vals[0] != val)) {
                                    if (tab.containsKey(val)) {
                                        int dest = tab.get(val);
                                        t.add(new Transition(lastState, val, dest));
                                    } else {
                                        t.add(new Transition(lastState, val, 0));
                                    }
                                }
                            }
                        }

                        if (j >= vals[1]) {
                            fs.add(newState);
                        }
                        lastState = newState;
                    }

                    for (Integer val : alphabet) {
                        if (vals[0] != val) {
                            if (tab.containsKey(val)) {
                                int dest = tab.get(val);
                                t.add(new Transition(lastState, val, dest));
                            } else {
                                t.add(new Transition(lastState, val, 0));
                            }
                        }
                    }
                }


                DFA auto = new DFA(t, fs, vars.length);

                return new Regular(auto, tmpVars, solver.getEnvironment());
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }
}
