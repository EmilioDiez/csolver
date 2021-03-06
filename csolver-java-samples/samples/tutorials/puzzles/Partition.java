/*
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez,All rights reserved.
 *
 *
 *  Choco Copyright (c) 1999-2010-2011, Ecole des Mines de Nantes
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
package samples.tutorials.puzzles;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import csolver.cp.solver.search.integer.valselector.MinVal;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.CSolver;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 13/04/11
 */
public class Partition extends PatternExample {
    static final NumberFormat formatter = new DecimalFormat("#0.00");
    //@Option(name = "-n", usage = "Numbers to partition", required = false)
    int size = 24;

    @Override
    public void printDescription() {
        Logger.info("This problem consists in finding a partition of numbers 1..2*N into two sets A and B such that:");
        Logger.info("a) A and B have the same cardinality");
        Logger.info("b) sum of numbers in A = sum of numbers in B");
        Logger.info("c) sum of squares of numbers in A = sum of squares of numbers in B");
        Logger.info(MessageFormat.format("Here n = {0}\n\n", size));
    }

    IntegerVariable[] xy;

    @Override
    public void buildModel() {
        model = new CPModel();
        IntegerVariable[] x = new IntegerVariable[size], y = new IntegerVariable[size];
        for (int i = 0; i < size; i++) {
            x[i] = CSolver.makeIntVar("x", 1, 2 * size);
            y[i] = CSolver.makeIntVar("y", 1, 2 * size);
        }

        // break symmetries
        for (int i = 0; i < size - 1; i++) {
            model.addConstraint(CSolver.lt(x[i], x[i + 1]));
            model.addConstraint(CSolver.lt(y[i], y[i + 1]));
        }
        model.addConstraint(CSolver.lt(x[0], y[0]));

        xy = new IntegerVariable[2 * size];
        for (int i = size - 1; i >= 0; i--) {
            xy[i] = x[i];
            xy[size + i] = y[i];
        }

        int[] coeffs = new int[2 * size];
        for (int i = size - 1; i >= 0; i--) {
            coeffs[i] = 1;
            coeffs[size + i] = -1;
        }
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, xy), 0));

        IntegerVariable[] sxy, sx = new IntegerVariable[size], sy = new IntegerVariable[size];
        sxy = new IntegerVariable[2 * size];
        for (int i = 0; i < size; i++) {
            sx[i] = CSolver.makeIntVar("sx", 1, 4 * size * size);
            sy[i] = CSolver.makeIntVar("sy", 1, 4 * size * size);
        }
        for (int i = size - 1; i >= 0; i--) {
            model.addConstraint(CSolver.times(x[i], x[i], sx[i]));
            sxy[i] = sx[i];
            model.addConstraint(CSolver.times(y[i], y[i], sy[i]));
            sxy[size + i] = sy[i];
        }
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, sxy), 0));

        coeffs = new int[size];
        Arrays.fill(coeffs, 1);
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, x), 2 * size * (2 * size + 1) / 4));
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, y), 2 * size * (2 * size + 1) / 4));
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, sx), 2 * size * (2 * size + 1) * (4 * size + 1) / 12));
        model.addConstraint(CSolver.eq(CSolver.scalar(coeffs, sy), 2 * size * (2 * size + 1) * (4 * size + 1) / 12));

        model.addConstraint(CSolver.allDifferent(xy));
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignOrForbidIntVarVal(new MinDomain(solver, solver.getVar(xy)), new MinVal()));
    }

    @Override
    public void solve() {
        solver.solve();
    }

    @Override
    public void prettyOut() {
        StringBuilder st = new StringBuilder("A: ");
        for(int i = 0 ; i < size; i++){
            st.append(solver.getVar(xy[i]).getVal()).append(" ");
        }
        st.append("\nB: ");
        for(int i = size ; i < 2*size; i++){
            st.append(solver.getVar(xy[i]).getVal()).append(" ");
        }
        Logger.info(st.toString());
    }

    public static void main(String[] args) {
        new Partition().execute(args);
    }
}
