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
package shaker;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.branching.AssignVar;
import csolver.cp.solver.search.integer.valiterator.DecreasingDomain;
import csolver.cp.solver.search.integer.valselector.MidVal;
import csolver.cp.solver.search.integer.varselector.DomOverWDeg;
import csolver.cp.solver.search.integer.varselector.MaxValueDomain;
import csolver.kernel.solver.variables.integer.IntDomainVar;
import nitoku.log.Logger;
import samples.tutorials.PatternExample;
import samples.tutorials.puzzles.GolombRuler;
import samples.tutorials.puzzles.MagicSquare;
import samples.tutorials.puzzles.Queen;
import shaker.tools.search.IntBranchingFactory;

public class StrategyTest  {

    

    CPSolver s;
    PatternExample pe;
    static Random random;
    boolean print = false;

    @Before
    public void before() {
        s = new CPSolver();
    }

    private void createModel() {
        pe.buildModel();
        pe.solver = new CPSolver();
        pe.solver.read(pe.model);
        pe.solver.solveAll();
    }

    private void loadQueenModel() {
        pe = new Queen();
        pe.readArgsn(8);
        createModel();
    }

    private void loadMagicSquareModel() {
        pe = new MagicSquare();
        pe.readArgsn(3);
        createModel();
    }

    private void loadGolombRulerModel() {
        pe = new GolombRuler();
        pe.readArgss(4);
        createModel();
    }

    @After
    public void after() {
        s = null;
        pe = null;
    }


    @Test
    public void testStrategyQ() {
        loadQueenModel();
        for (int i = 0; i < 100; i++) {
            Logger.info("seed:" + i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe.model);
            IntBranchingFactory bf = new IntBranchingFactory();
            IntDomainVar[] vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars));
            checker();
        }

    }

    @Test
    public void testStrategyMS() {
        loadMagicSquareModel();
        for (int i = 0; i < 100; i++) {
            Logger.info("seed:" + i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe.model);
            IntBranchingFactory bf = new IntBranchingFactory();
            IntDomainVar[] vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars));
            checker();
        }

    }

    @Test
    public void testStrategyGR() {
        loadGolombRulerModel();
        for (int i = 0; i < 100; i++) {
            Logger.info("seed:" + i);
            random = new Random(i);

            s = new CPSolver();
            s.read(pe.model);
            IntBranchingFactory bf = new IntBranchingFactory();
            IntDomainVar[] vars = s.getIntDecisionVars();

            s.attachGoal(bf.make(random, s, vars));
            checker();
        }

    }

    private void checker() {
        s.solveAll(); //checkSolution enbaled by assertion
//        s.solve();
//        if(Boolean.TRUE.equals(s.isFeasible())){
//            do{
//                assertTrue(s.checkSolution());
//            }while(s.nextSolution());
//        }
        assertEquals("feasibility incoherence", pe.solver.isFeasible(), s.isFeasible());
        assertEquals("nb sol incoherence", pe.solver.getNbSolutions(), s.getNbSolutions());

    }

    @Test
    public void testStrategy1() {
        loadQueenModel();
        s = new CPSolver();
        s.read(pe.model);
        s.attachGoal(new AssignVar(new DomOverWDeg(s), new MidVal()));
        checker();
    }

    @Test
    public void testStrategy2() {
        loadGolombRulerModel();
        s = new CPSolver();
        s.read(pe.model);
        s.attachGoal(new AssignVar(new MaxValueDomain(s), new DecreasingDomain()));
        checker();
    }

}