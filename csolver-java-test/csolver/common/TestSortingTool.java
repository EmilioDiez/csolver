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
package csolver.common;

import static csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.DETECTABLE_PRECEDENCE;
import static csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.EDGE_FINDING_D;
import static csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.NF_NL;
import static csolver.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.OVERLOAD_CHECKING;
import static csolver.kernel.common.util.tools.MathUtils.combinaison;
import static csolver.kernel.common.util.tools.MathUtils.factoriel;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import csolver.kernel.common.util.bitmask.BitMask;
import csolver.kernel.common.util.comparator.IPermutation;
import csolver.kernel.common.util.tools.PermutationUtils;
import nitoku.log.Logger;


public class TestSortingTool  {

    

	public final static int[] IDENTITY = {1,2,3,4,5,6,7,8};

	public final static int[] EXAMPLE1 = {8,3,4,2,5,5,6,5,10};

	public final static int[] EXAMPLE2 = {4,3,4,7,4,3,2,5,12};


	@Test
	public void testIdentity() {
		IPermutation st= PermutationUtils.getSortingPermuation(IDENTITY);
		assertTrue("identity",st.isIdentity());
		test(IDENTITY);
	}

	protected void test(int[] original) {
		IPermutation st= PermutationUtils.getSortingPermuation(original);
		int[] c = Arrays.copyOf(original, original.length);
		Arrays.sort(c);
		assertArrayEquals("get sorted array",c, st.applyPermutation(original));
		for (int i = 0; i < original.length; i++) {
			assertEquals("pi ° pi-1",i,st.getOriginalIndex(st.getPermutationIndex(i)));
		}
		Logger.info(""+st);
	}

	@Test
	public void testArray1() {
		test(EXAMPLE1);
	}

	@Test
	public void testArray2() {
		test(EXAMPLE2);
	}

	@Test
	public void testMathUtil() {
		assertEquals("factorielle",1,factoriel(-1));
		assertEquals("factorielle",6,factoriel(3));
		assertEquals("factorielle",120,factoriel(5));
		assertEquals("combinaison",1,combinaison(1, 1));
		assertEquals("combinaison",3,combinaison(3, 2));
		assertEquals("combinaison",6,combinaison(4, 2));
		assertEquals("combinaison",1,combinaison(5, 0));
		assertEquals("combinaison",10,combinaison(5, 2));
		assertEquals("combinaison",10,combinaison(5, 3));
	}


	@Test
	public void testBitFlags() {
		BitMask flags = new BitMask();
		
		flags.set(NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		//get ans set
		assertTrue( "get", flags.contains(NF_NL));
		assertTrue( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		assertFalse( "get" , flags.contains(OVERLOAD_CHECKING));
		//toggle
		flags.toggle(NF_NL.getBitMask());
		assertFalse( "get", flags.contains(NF_NL));
		flags.toggle(NF_NL.getBitMask());
		assertTrue( "get", flags.contains(NF_NL));
		//unset 
		flags.unset(NF_NL.getBitMask());
		flags.unset(DETECTABLE_PRECEDENCE.getBitMask());
		
		assertFalse( "get", flags.contains(NF_NL));
		assertFalse( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		
	}
}
