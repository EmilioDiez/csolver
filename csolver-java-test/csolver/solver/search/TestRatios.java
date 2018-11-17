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

package csolver.solver.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import csolver.cp.solver.CPSolver;
import csolver.cp.solver.search.integer.varselector.ratioselector.IntVarRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.MaxRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.RandMaxRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import csolver.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import csolver.cp.solver.search.integer.varselector.ratioselector.ratios.SimpleRatio;
import csolver.kernel.solver.Solver;

public class TestRatios {

	private IntVarRatioSelector ratioSel;

	private final Solver s = new CPSolver();

	private final static IntRatio[] RATIOS = {
		new SimpleRatio(1, 1),new SimpleRatio(1, 4),new SimpleRatio(1, 3),new SimpleRatio(1, 2),
		new SimpleRatio(1, 1),new SimpleRatio(3, 4), new SimpleRatio(2, 3),new SimpleRatio(1, 4),
	};

	private final static IntRatio[] ZRATIOS = Arrays.copyOf(RATIOS, RATIOS.length + 1);
	
	private static final SimpleRatio ZDVS = new SimpleRatio(1, 0);
	
	private static final SimpleRatio ZDVD = new SimpleRatio(0, 1);
	
	
	private void check(int bidx, int bsize) {
		assertEquals(bidx, ratioSel.selectIntRatioIndex());
		assertEquals(bsize, ratioSel.selectTiedIntVars().size());
	}
	
	@Test 
	public void testMinRatio() {
		ratioSel = new MinRatioSelector(s, RATIOS);
		check(1, 2);
		ratioSel = new MinRatioSelector(s, ZRATIOS);
		ZRATIOS[RATIOS.length]= ZDVS;
		check(1, 2);
		ZRATIOS[RATIOS.length]= ZDVD;
		check(RATIOS.length, 1);
	}

	@Test 
	public void testMaxRatio() {
		ratioSel = new MaxRatioSelector(s, RATIOS);
		check(0, 2);
		ratioSel = new MaxRatioSelector(s, ZRATIOS);
		ZRATIOS[RATIOS.length]= ZDVS;
		check(RATIOS.length, 1);
		ZRATIOS[RATIOS.length]= ZDVD;
		check(0, 2);
	}

	@Test 
	public void testRandMinRatio() {
		ratioSel = new RandMinRatioSelector(s, RATIOS, 0);
		for (int i = 0; i < 10; i++) {
			final int best = ratioSel.selectIntRatioIndex();
			assertTrue( best == 1 || best == 7);
		}
	}
	
	@Test 
	public void testRandMaxRatio() {
		ratioSel = new RandMaxRatioSelector(s, RATIOS, 0);
		for (int i = 0; i < 10; i++) {
			final int best = ratioSel.selectIntRatioIndex();
			assertTrue( best == 0 || best == 4);
		}
	}
	
	
}
