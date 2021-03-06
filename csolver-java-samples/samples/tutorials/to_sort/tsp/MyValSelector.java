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
package samples.tutorials.to_sort.tsp;

import csolver.kernel.solver.search.ValSelector;
import csolver.kernel.solver.variables.integer.IntDomainVar;


public class MyValSelector implements ValSelector<IntDomainVar> {

    protected IntDomainVar objective;
    protected IntDomainVar[] vars;
    protected int[][] matrix;
    protected int src;
    protected int dest;

    protected boolean optimize;

    public MyValSelector(IntDomainVar objective, IntDomainVar[] vars, int[][] matrix, int src, int dest) {
        this.objective = objective;
        this.vars = vars;
        this.matrix = matrix;
        this.src = src;
        this.dest = dest;
        this.optimize = false;
        int idx = 0;
        while(idx < vars.length && !this.optimize) {
            if(vars[idx].equals(objective)) this.optimize = true;
            idx++;
        }
    }

    public int getBestVal(IntDomainVar v) {
        int n;
        if (optimize) n = vars.length-1;
        else n = vars.length;
        if (v.equals(objective)) {
            return objective.getInf();
        } else {
            if (v.equals(vars[dest])) {
                return src;
            } else {
                int idx = -1;
                do{
                    idx++;
                } while(!vars[idx].equals(v));
                int cost = Integer.MAX_VALUE;
                int val = -1;
                for(int i = 0; i < n; i++) {
                    if(matrix[idx][i] < cost && v.canBeInstantiatedTo(i)) {
                        cost = matrix[idx][i];
                        val = i;
                    }
                }
                return val;
            }
        }
    }

}
