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
package csolver.solver;

import static csolver.kernel.solver.Configuration.STOP_AT_FIRST_SOLUTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import csolver.kernel.common.NProperties;
import csolver.kernel.solver.Configuration;
import csolver.kernel.solver.search.limit.Limit;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 22 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class ConfigurationTest {

    private static final String USER_KEY = "user.key";

    private Configuration configuration;


    @Before
    public void load() {
        configuration = new Configuration();
    }


    @Test
    public void test1() {
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        assertTrue(mykey);
        Boolean safs = configuration.readBoolean(STOP_AT_FIRST_SOLUTION);
        assertTrue(safs);
    }

    @Test(expected = NullPointerException.class)
    public void testNoInt() {
        configuration.readInt(USER_KEY);
    }

    @Test
    public void testInt() {
        configuration.putInt(USER_KEY, 99);
        int value = configuration.readInt(USER_KEY);
        assertEquals(99, value);
        configuration.putInt(USER_KEY, 9);
        value = configuration.readInt(USER_KEY);
        assertEquals(9, value);
    }

    @Test(expected = NullPointerException.class)
    public void testNoBoolean() {
        configuration.readBoolean(USER_KEY);
    }

    @Test
    public void testBoolean() {
        configuration.putBoolean(USER_KEY, true);
        boolean value = configuration.readBoolean(USER_KEY);
        assertEquals(true, value);
        configuration.putBoolean(USER_KEY, false);
        value = configuration.readBoolean(USER_KEY);
        assertEquals(false, value);
    }

    @Test(expected = NullPointerException.class)
    public void testNoDouble() {
        configuration.readDouble(USER_KEY);
    }

    @Test
    public void testDouble() {
        configuration.putDouble(USER_KEY, 9.99);
        double value = configuration.readDouble(USER_KEY);
        assertEquals(9.99, value, 0.01);
        configuration.putDouble(USER_KEY, 1.e-9);
        value = configuration.readDouble(USER_KEY);
        assertEquals(1.e-9, value, 0.01);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Test(expected = NullPointerException.class)
    public void testNoEnum() {
        Limit lim = configuration.readEnum(USER_KEY, Limit.class);
    }

    @Test
    public void testEnum() {
        configuration.putEnum(USER_KEY, Limit.TIME);
        Limit value = configuration.readEnum(USER_KEY, Limit.class);
        assertEquals(Limit.TIME, value);
        configuration.putEnum(USER_KEY, Limit.UNDEF);
        value= configuration.readEnum(USER_KEY, Limit.class);
        assertEquals(Limit.UNDEF, value);
    }

    public void test2() {
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        assertTrue(mykey);
        Boolean safs = configuration.readBoolean(STOP_AT_FIRST_SOLUTION);
        assertTrue(safs);
    }


    @Ignore
    public void test4() {
        NProperties properties = new NProperties();
        //configuration = new Configuration(properties);
        configuration = new Configuration();
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        assertTrue(mykey);
        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
        assertTrue(safs);
    }

    @Test
    public void test5() {
        NProperties empty = new NProperties();
        configuration = new Configuration();
        configuration.putBoolean(USER_KEY, true);
        Boolean mykey = configuration.readBoolean(USER_KEY);
        assertTrue(mykey);
        Boolean safs = configuration.readBoolean(STOP_AT_FIRST_SOLUTION);
        assertTrue(safs);
    }

    @Test
    public void test6() {
    	configuration = new Configuration();
    	assertTrue(configuration.readBoolean(STOP_AT_FIRST_SOLUTION));
    	configuration.putFalse(STOP_AT_FIRST_SOLUTION);
    	assertFalse(configuration.readBoolean(STOP_AT_FIRST_SOLUTION));
    	configuration.remove(STOP_AT_FIRST_SOLUTION);
    	assertTrue(configuration.readBoolean(STOP_AT_FIRST_SOLUTION));
//        Boolean mykey = configuration.readBoolean(USER_KEY);
//        assertTrue(mykey);
//        Boolean safs = configuration.readBoolean(Configuration.STOP_AT_FIRST_SOLUTION);
//        assertTrue(safs);
    }

}
