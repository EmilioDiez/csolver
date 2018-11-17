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

package csolver.model.variables.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import csolver.cp.solver.CPSolver;
import csolver.kernel.solver.ContradictionException;
import csolver.kernel.solver.Solver;
import csolver.kernel.solver.variables.integer.IntDomainVar;

public class BooleanVariableTest {

    

    @Test
    public void test1() throws ContradictionException {
        Solver s = new CPSolver();
        IntDomainVar v1 = s.createBooleanVar("v");
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("next value -1", v1.getDomain().hasNextValue(-1));
        assertEquals("getNextValue -1", 0, v1.getDomain().getNextValue(-1));
        assertTrue("next value 0", v1.getDomain().hasNextValue(0));
        assertEquals("getNextValue 0", 1, v1.getDomain().getNextValue(0));
        assertFalse("next value 1", v1.getDomain().hasNextValue(1));
        assertFalse("next value 2", v1.getDomain().hasNextValue(2));

        assertFalse("prev value -1", v1.getDomain().hasPrevValue(-1));
        assertFalse("prev value 0", v1.getDomain().hasPrevValue(0));
        assertTrue("prev value 1", v1.getDomain().hasPrevValue(1));
        assertEquals("getNextValue 1", 0, v1.getDomain().getPrevValue(1));
        assertTrue("prev value 2", v1.getDomain().hasPrevValue(2));
        assertEquals("getNextValue 2", 1, v1.getDomain().getPrevValue(2));

        s.worldPush();

        assertFalse("update inf 0",v1.updateInf(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("update inf 1",v1.updateInf(1, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.updateInf(2, null, true);
            fail("update inf 2");
        }catch (ContradictionException e){

        }

        s.worldPop();
        s.worldPush();

        assertFalse("update sup 1",v1.updateSup(1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("update sup 0",v1.updateSup(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.updateInf(2, null, true);
            fail("update sup -1");
        }catch (ContradictionException e){

        }


        s.worldPop();
        s.worldPush();

        assertFalse("remove 2",v1.removeVal(2, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("remove 1",v1.removeVal(1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.removeVal(0, null, true);
            fail("remove 0");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        assertFalse("remove -1",v1.removeVal(-1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("remove 0",v1.removeVal(0, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.removeVal(1, null, true);
            fail("remove 1");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        assertTrue("instantiate 0",v1.instantiate(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        s.worldPop();
        s.worldPush();

        assertTrue("instantiate 1",v1.instantiate(1, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        s.worldPop();
        s.worldPush();

        try{
            assertFalse("instantiate 2",v1.instantiate(2, null, true));
            fail("unknown value");
        }catch (ContradictionException ex){
        }
        try{
            assertFalse("instantiate -1",v1.instantiate(-1, null, true));
            fail("unknown value");
        }catch (ContradictionException ex){
        }
    }

    @Test
    public void test2() throws ContradictionException {
        CPSolver s = new CPSolver();
        IntDomainVar v0 = s.createBooleanVar("v0");
        IntDomainVar v1 = s.createNotBooleanVar("v", v0);

        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertEquals("getNextValue -1", 0, v1.getNextDomainValue(-1));
        assertEquals("getNextValue 0", 1, v1.getNextDomainValue(0));

        assertEquals("getNextValue 1", 0, v1.getPrevDomainValue(1));
        assertEquals("getNextValue 2", 1, v1.getPrevDomainValue(2));

        s.worldPush();

        assertFalse("update inf 0",v1.updateInf(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("update inf 1",v1.updateInf(1, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.updateInf(2, null, true);
            fail("update inf 2");
        }catch (ContradictionException e){

        }

        s.worldPop();
        s.worldPush();

        assertFalse("update sup 1",v1.updateSup(1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("update sup 0",v1.updateSup(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.updateInf(2, null, true);
            fail("update sup -1");
        }catch (ContradictionException e){

        }


        s.worldPop();
        s.worldPush();

        assertFalse("remove 2",v1.removeVal(2, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("remove 1",v1.removeVal(1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.removeVal(0, null, true);
            fail("remove 0");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        assertFalse("remove -1",v1.removeVal(-1, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",2,v1.getDomainSize());

        assertTrue("remove 0",v1.removeVal(0, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.removeVal(1, null, true);
            fail("remove 1");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        assertTrue("instantiate 0",v1.instantiate(0, null, true));
        assertEquals("lower bound",0,v1.getInf());
        assertEquals("upper bound",0,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(0));

        s.worldPop();
        s.worldPush();

        assertTrue("instantiate 1",v1.instantiate(1, null, true));
        assertEquals("lower bound",1,v1.getInf());
        assertEquals("upper bound",1,v1.getSup());
        assertEquals("domain size",1,v1.getDomainSize());
        assertTrue("instantiated", v1.isInstantiatedTo(1));

        s.worldPop();
        s.worldPush();

        try{
            assertFalse("instantiate 2",v1.instantiate(2, null, true));
            fail("unknown value");
        }catch (ContradictionException ex){
        }
        try{
            assertFalse("instantiate -1",v1.instantiate(-1, null, true));
            fail("unknown value");
        }catch (ContradictionException ex){
        }
    }
    
    
}
