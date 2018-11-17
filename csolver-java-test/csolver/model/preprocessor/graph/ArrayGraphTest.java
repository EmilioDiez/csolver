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

package csolver.model.preprocessor.graph;

import csolver.cp.common.util.preprocessor.graph.ArrayGraph;
import csolver.cp.common.util.preprocessor.graph.MaxCliques;
import csolver.kernel.common.logging.CSolverLogging;
import nitoku.log.Logger;

import org.junit.Test;

import java.util.Random;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ArrayGraphTest {

    

    public static void test(int n, int m, int seed) {
		double start = System.currentTimeMillis();
		ArrayGraph g = MaxCliques.generateGraph(n,m,seed,start);
	    MaxCliques myCliques = new MaxCliques(g);
	    Logger.info("cliques : \n" + MaxCliques.display(myCliques.getMaxCliques()));
	    Logger.info("Total time : " + (System.currentTimeMillis()-start) + " ms.\n");
	    if(n<=16) {
	    	Logger.info(g.toString());
	    }
	}

    @Test
	public void testEmptyGraph241108() {
		Logger.info("Graph without edges");
		test(6,0,1986);
	}

    @Test
	public void test2() {
        for (int i = 0; i < 100; i++) {
            Random r = new Random(i);
            int m = r.nextInt(10);
            int p = 1 +r.nextInt(10);
            int n = m + p;
            int seed = r.nextInt();

            test(n, m, seed);
        }
    }
}
