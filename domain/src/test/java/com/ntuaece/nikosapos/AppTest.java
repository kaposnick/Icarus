package com.ntuaece.nikosapos;

<<<<<<< HEAD
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
=======
import org.junit.Test;

import com.ntuaece.nikosapos.entities.Packet;

import static org.junit.Assert.*;
/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testTimeCalculation(){
    	double distance = 10;
    	long speed = 3000;
    	System.out.println((long)(distance/speed * 1000) + "");
    	assertTrue(distance/speed > 0);
    }
    
    @Test
    public void checkLink(){
    	/**
    	 * check the queues. WORKING!!!
    	 */
    	
    	
    	
>>>>>>> refs/heads/nikos-branch
    }
}
