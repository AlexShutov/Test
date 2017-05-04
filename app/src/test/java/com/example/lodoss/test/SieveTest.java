package com.example.lodoss.test;

import com.example.lodoss.test.sieve.InfiniteSieve;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by lodoss on 04/05/17.
 */

@RunWith(JUnit4.class)
public class SieveTest {

    private InfiniteSieve algorithmInstance;

    /**
     * Algorithm is a Iterator - it incapsuletes state. We need to create new instance for every
     * test method.
     */
    @Before
    public void initAlrogithm(){
        algorithmInstance = new InfiniteSieve();
    }

    @Test
    public void testTestWorks(){

    }
}
