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

package csolver.model.variables.delta;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import csolver.cp.solver.CPSolver;
import csolver.cp.solver.variables.delta.BitSetDeltaDomain;
import csolver.cp.solver.variables.integer.AbstractIntDomain;
import csolver.cp.solver.variables.integer.IntVarEvent;
import csolver.kernel.common.util.iterators.DisposableIntIterator;
import csolver.kernel.common.util.tools.ArrayUtils;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.delta.IDeltaDomain;
import csolver.kernel.solver.variables.integer.IntDomainVar;
public class DeltaDomainTest {

    @Test
    public void test01(){
        final Solver s = new CPSolver();
        final IntDomainVar v = s.createEnumIntVar("v", 1, 10);
        final IDeltaDomain dom = new BitSetDeltaDomain(10, 1);
        final int[] rem_values = new int[]{1,2,8,9,10};
        for(final int i : rem_values){
            dom.remove(i);
        }

        dom.freeze();
        ArrayList<Integer> values = new ArrayList<Integer>();
        final DisposableIntIterator dit = dom.iterator();
        while(dit.hasNext()){
            values.add(dit.next());
        }
        dit.dispose();
        assertEquals(5, values.size());
        values = ArrayUtils.sort(values);
        assertArrayEquals(rem_values, ArrayUtils.convertIntegers(values));

        dom.remove(4);
        assertFalse(dom.isReleased());
        dom.clear();
        assertTrue(dom.isReleased());
    }


    @Test
    public void test1() {
        Random r;
        for (int i = 0; i < 20; i++) {
            r = new Random(i);
            final Solver s = new CPSolver();
            IntDomainVar v = null;
            switch (r.nextInt(4)) {
                case 0:
                    v = s.createEnumIntVar("v", 1, 10);
                    break;
                case 1:
                    v = s.createIntVar("v", IntDomainVar.LINKEDLIST, 1, 10);
                    break;
                case 2:
                    v = s.createIntVar("v", IntDomainVar.BINARYTREE, 1, 10);
                    break;
                case 3:
                    v = s.createIntVar("v", IntDomainVar.BIPARTITELIST, 1, 10);
                    break;
            }

            final AbstractIntDomain dom = (AbstractIntDomain) v.getDomain();

            dom.remove(5);
            dom.updateInf(3);
            dom.updateSup(7);

            dom.freezeDeltaDomain();
            final HashSet<Integer> set1258910 = new HashSet<Integer>(
            		ArrayUtils.convertToInteger(new int[]{1, 2, 5, 8, 9, 10}));
            final HashSet<Integer> set = new HashSet<Integer>();
            final DisposableIntIterator dit = dom.getDeltaIterator();
            while (dit.hasNext()) {
                set.add(dit.next());
            }
            assertEquals(set1258910, set);

            dom.remove(4);

            assertFalse(dom.releaseDeltaDomain());
        }
    }

    @Test
    public void test2(){
        final Solver s = new CPSolver();
        final IntDomainVar v = s.createBoundIntVar("v", 1, 10);
        v.getEvent().addPropagatedEvents(IntVarEvent.BOUNDS_MASK + IntVarEvent.REMVAL_MASK);
        final AbstractIntDomain dom = (AbstractIntDomain)v.getDomain();

        dom.updateInf(3);
        dom.updateSup(7);

        dom.freezeDeltaDomain();

        final DisposableIntIterator dit = dom.getDeltaIterator();
        assertTrue(dit.hasNext());
        assertEquals(1, dit.next());
        assertTrue(dit.hasNext());
        assertEquals(2, dit.next());
        assertTrue(dit.hasNext());
        assertEquals(8, dit.next());
        assertTrue(dit.hasNext());
        assertEquals(9, dit.next());
        assertTrue(dit.hasNext());
        assertEquals(10, dit.next());
        assertFalse(dit.hasNext());

        dom.updateInf(4);

        assertFalse(dom.releaseDeltaDomain());
    }


    @Test
    public void test3(){
        final Solver s = new CPSolver();
        final IntDomainVar v = s.createBooleanVar("v");
        //v.getEvent().addPropagatedEvents(IntVarEvent.BOUNDS_MASK + IntVarEvent.REMVAL_MASK);
        final AbstractIntDomain dom = (AbstractIntDomain)v.getDomain();

        dom.restrict(1);

        dom.freezeDeltaDomain();

        final DisposableIntIterator dit = dom.getDeltaIterator();
        assertTrue(dit.hasNext());
        assertEquals(0, dit.next());
        assertFalse(dit.hasNext());

        assertTrue(dom.releaseDeltaDomain());
    }


    @Test
    public void test4() {
        final Set<Integer> expectedSet357 = new TreeSet<Integer>();
        expectedSet357.add(3);
        expectedSet357.add(5);
        expectedSet357.add(7);
        final Set<Integer> expectedSet9 = new TreeSet<Integer>();
        expectedSet9.add(9);

        final int[] domtype = new int[]{IntDomainVar.BIPARTITELIST, IntDomainVar.BINARYTREE,
                        IntDomainVar.LINKEDLIST, IntDomainVar.BITSET};
        Random r;
        for (int i = 0; i < 10; i++) {
            r = new Random(i);

            final Solver s = new CPSolver();
            final IntDomainVar v = s.createIntVar("v", domtype[r.nextInt(domtype.length)], 1, 10);
            final AbstractIntDomain yDom = (AbstractIntDomain) v.getDomain();

            yDom.freezeDeltaDomain();
            DisposableIntIterator it = yDom.getDeltaIterator();
            assertFalse(it.hasNext());
            assertTrue(yDom.releaseDeltaDomain());
            it.dispose();

            yDom.remove(3);
            yDom.remove(5);
            yDom.remove(7);
            final Set tmp357 = new TreeSet();
            yDom.freezeDeltaDomain();
            yDom.remove(9);
            for (it = yDom.getDeltaIterator(); it.hasNext();) {
                final int val = it.next();
                tmp357.add(val);
            }
            it.dispose();
            assertEquals(expectedSet357, tmp357);
            assertFalse(yDom.releaseDeltaDomain());
            yDom.freezeDeltaDomain();
            final Set tmp9 = new TreeSet();
            for (it = yDom.getDeltaIterator(); it.hasNext();) {
                final int val = it.next();
                tmp9.add(val);
            }
            it.dispose();
            assertEquals(expectedSet9, tmp9);
            assertTrue(yDom.releaseDeltaDomain());
        }
    }

}
