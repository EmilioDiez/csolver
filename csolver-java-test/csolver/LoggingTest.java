package csolver;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;

import org.junit.Test;

import nitoku.log.Logger;

public class LoggingTest  {

	@Test
    public void testLogger() {
    	
    	assertEquals(Level.INFO,Logger.getLevel());

    }
    
}
