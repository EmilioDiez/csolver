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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import csolver.kernel.common.util.objects.BipartiteSet;
import nitoku.log.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: Jul 4, 2003
 * Time: 10:07:46 AM
 * To change this template use Options | File Templates.
 */
public class BipartiteSetTest {

	

	@Before
	public void setUp() {
		Logger.fine("BipartiteSet Testing...");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() {
		Logger.finer("test1");
		BipartiteSet<Object> set = new BipartiteSet<Object>();
		Object obj1 = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();

		set.addLeft(obj1);
		set.addLeft(obj2);
		set.addRight(obj3);

		assertEquals(2, set.getNbLeft());
		assertEquals(1, set.getNbRight());
		assertTrue(set.isLeft(obj1));
		Logger.finest("First Step passed");

		set.moveRight(obj1);

		assertEquals(1, set.getNbLeft());
		assertEquals(2, set.getNbRight());
		assertTrue(set.isLeft(obj2));
		assertFalse(set.isLeft(obj1));
		assertFalse(set.isLeft(obj3));
		Logger.finest("Second Step passed");

		set.moveAllLeft();

		assertEquals(3, set.getNbLeft());
		assertEquals(0, set.getNbRight());
		assertTrue(set.isLeft(obj1));
		assertTrue(set.isLeft(obj2));
		assertTrue(set.isLeft(obj3));
		Logger.finest("Third Step passed");

	}
}
