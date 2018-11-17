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

package csolver.model.constraints.integer;

import static csolver.kernel.CSolver.feasPairAC;
import static csolver.kernel.CSolver.feasTupleAC;
import static csolver.kernel.CSolver.makeIntVar;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import csolver.cp.model.CPModel;
import csolver.cp.solver.CPSolver;
import csolver.kernel.model.variables.integer.IntegerVariable;
import nitoku.log.Logger;

public class BinRelationSearchTest {

    

  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};


    @Test
  public void testNQueen1AC3() {
    queen0(9, 3, false);
  }

    @Test
  public void testNQueen1AC322() {
    queen0(9, 322, false);
  }

    @Test
  public void testNQueen1AC2001() {
    queen0(9, 2001, false);
  }

    @Test
  public void testNQueen1AC3rm() {
    queen0(9, 32, false);
  }


  private void queen0(int n, int ac, boolean nary) {
    boolean[][] matriceNeq = new boolean[n][n];
    ArrayList<int[]> tuples = new ArrayList<int[]>();
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        if (i == j) {
          matriceNeq[i][j] = false;
        } else {
          tuples.add(new int[]{i+1,j+1});
	      matriceNeq[i][j] = true;
        }
      }

    // create variables
    CPModel m = new CPModel();
    IntegerVariable[] queens = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      queens[i] = makeIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        if (nary)
            m.addConstraint(feasTupleAC(tuples, queens[i], queens[j]));
	    else m.addConstraint(feasPairAC("cp:ac" + ac, queens[i], queens[j], matriceNeq));
        boolean[][] matriceNeqDec1 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
              matriceNeqDec1[z][w] = z != (w - k);
          }
        m.addConstraint(feasPairAC("cp:ac" + ac,queens[i], queens[j], matriceNeqDec1));   // pb.plus(queens[j], k)
        boolean[][] matriceNeqDec2 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
              matriceNeqDec2[z][w] = z != (w + k);
          }
        m.addConstraint(feasPairAC("cp:ac" + ac,queens[i], queens[j], matriceNeqDec2));  // pb.minus(queens[j], k)
      }
    }
    CPSolver s = new CPSolver();
      s.read(m);
      long time = System.currentTimeMillis();
      s.solveAll();
      time = System.currentTimeMillis() - time;
    assertEquals(nbQueensSolution[n], s.getNbSolutions());
    Logger.info("nb SolTh : " + nbQueensSolution[n] + " nb SolReal : " + s.getNbSolutions()+ " in " + (int) time + " ms with ac " + ac);
  }
}
