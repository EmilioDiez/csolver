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

package samples.tutorials.assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.IncreasingDomain;
import csolver.cp.solver.search.integer.varselector.MinDomain;
import csolver.kernel.CSolver;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 7 oct. 2010
 */
public class CarSequence extends PatternExample {

    //@Option(name = "-f", required = false, usage = "Data file")
    String file = null;

    int nbCars = 10;
    int nbOpt = 5;
    int nbClasses = 6;

    int[] demands = {1, 1, 2, 2, 2, 2};
    Integer[][] optfreq = {
            {1, 2},
            {2, 3},
            {1, 3},
            {2, 5},
            {1, 5}
    };
    int[][] matrix = {{1, 0, 1, 1, 0},
            {0, 0, 0, 1, 0},
            {0, 1, 0, 0, 1},
            {0, 1, 0, 1, 0},
            {1, 0, 1, 0, 0},
            {1, 1, 0, 0, 0}};

    int[][] options;
    int[][] idleConfs;

    IntegerVariable[] cars;

    @Override
    public void printDescription() {
        if (file != null) {
            parse(file);
        }
        Logger.info("A number of cars are to be produced; they are not identical, because different options are available as variants on the basic model.");
        Logger.info("The assembly line has different stations which install the various options (air-conditioning, sun-roof, etc.).");
        Logger.info("These stations have been designed to handle at most a certain percentage of the cars passing along the assembly line.");
        Logger.info("Furthermore, the cars requiring a certain option must not be bunched together, otherwise the station will not be able to cope.");
        Logger.info("Consequently, the cars must be arranged in a sequence so that the capacity of each station is never exceeded.");
        Logger.info("(http://www.csplib.org/)");
        Logger.info("\nData:");
        Logger.info(String.format("%d %d %d", nbCars, nbOpt,nbClasses));
        Integer[][] ropt = ArrayUtils.transpose(optfreq);
        Logger.info(String.format("%s", Arrays.toString(ropt[0])));
        Logger.info(String.format("%s", Arrays.toString(ropt[1])));
        for(int i = 0; i < matrix.length;i++){
            Logger.info(String.format("%d %d %s", i, demands[i], Arrays.toString(matrix[i])));
        }

    }

    @Override
    public void buildModel() {
        prepare();
        model = new CPModel();
        int max = nbClasses - 1;
        cars = CSolver.makeIntVarArray("cars", nbCars, 0, max);

        IntegerVariable[] expArray = new IntegerVariable[nbClasses];

        for (int optNum = 0; optNum < options.length; optNum++) {
            int nbConf = options[optNum].length;
            for (int seqStart = 0; seqStart < (cars.length - optfreq[optNum][1]); seqStart++) {
                IntegerVariable[] carSequence = extractor(cars, seqStart, optfreq[optNum][1]);
                IntegerVariable[] atMost = CSolver.makeIntVarArray("atmost", options[optNum].length, 0, max);
                model.addConstraint(CSolver.globalCardinality(carSequence, options[optNum], atMost));
                // configurations that include given option may be chosen
                // optfreq[optNum][0] times AT MOST
                for (int i = 0; i < nbConf; i++) {
                    model.addConstraint(CSolver.leq(atMost[i], optfreq[optNum][0]));
                }

                IntegerVariable[] atLeast = CSolver.makeIntVarArray("atleast", idleConfs[optNum].length, 0, max);
                model.addConstraint(CSolver.globalCardinality(carSequence, idleConfs[optNum], atLeast));
                // all others configurations may be chosen
                model.addConstraint(CSolver.geq(CSolver.sum(atLeast), optfreq[optNum][1] - optfreq[optNum][0]));
            }
        }

        int[] values = new int[expArray.length];
        for (int i = 0; i < expArray.length; i++) {
            expArray[i] = CSolver.makeIntVar("var", 0, demands[i]);
            values[i] = i;
        }
        model.addConstraint(CSolver.globalCardinality(cars, values, expArray));
    }

    private void prepare() {
        options = new int[nbOpt][];
        idleConfs = new int[nbOpt][];
        for (int i = 0; i < matrix[0].length; i++) {
            int nbNulls = 0;
            int nbOnes = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 1)
                    nbOnes++;
                else
                    nbNulls++;
            }
            options[i] = new int[nbOnes];
            idleConfs[i] = new int[nbNulls];
            int countOnes = 0;
            int countNulls = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 1) {
                    options[i][countOnes] = j;
                    countOnes++;
                } else {
                    idleConfs[i][countNulls] = j;
                    countNulls++;
                }
            }
        }
    }

    @Override
    public void buildSolver() {
        solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignVar(new MinDomain(solver), new IncreasingDomain()));
    }

    @Override
    public void solve() {
        solver.solve();
    }

    @Override
    public void prettyOut() {
        Logger.info("\nA valid sequence for this set of cars is:");
        for (int i = 0; i < cars.length; i++) {
            int k = solver.getVar(cars[i]).getVal();
            Logger.info(String.format("%d\t %s", k, Arrays.toString(matrix[k])));
        }
        Logger.info("\n");
    }

    private static IntegerVariable[] extractor(IntegerVariable[] cars, int initialNumber, int amount) {
        if ((initialNumber + amount) > cars.length) {
            amount = cars.length - initialNumber;
        }
        IntegerVariable[] tmp = new IntegerVariable[amount];
        System.arraycopy(cars, initialNumber, tmp, initialNumber - initialNumber, initialNumber + amount - initialNumber);
        return tmp;
    }


    public static void main(String[] args) {
        new CarSequence().execute(args);
    }


    ////////////////////////////////////////////////////

    protected int[][] parse(String fileName) {
        int[][] data = null;
        try {
            Scanner sc = new Scanner(new File(fileName));
            nbCars = sc.nextInt();
            nbOpt = sc.nextInt();
            nbClasses = sc.nextInt();

            optfreq = new Integer[nbOpt][2];
            // get frequencies
            for (int i = 0; i < nbOpt; i++) {
                optfreq[i][0] = sc.nextInt();
            }
            for (int i = 0; i < nbOpt; i++) {
                optfreq[i][1] = sc.nextInt();
            }

            // get the demand and options
            demands = new int[nbClasses];
            matrix = new int[nbClasses][nbOpt];
            for (int i = 0; i < nbClasses; i++) {
                sc.nextInt();
                demands[i] = sc.nextInt();
                for (int j = 0; j < nbOpt; j++) {
                    matrix[i][j] = sc.nextInt();
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
