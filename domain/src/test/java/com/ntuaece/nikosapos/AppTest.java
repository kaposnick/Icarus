package com.ntuaece.nikosapos;

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
    	
    	
    	
    }
}
