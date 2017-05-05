package com.example.lodoss.test;

import com.example.lodoss.test.sieve.InfiniteSieve;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.sqrt;
import static org.junit.Assert.*;

/**
 * Created by lodoss on 04/05/17.
 */

@RunWith(JUnit4.class)
public class SieveTest {

    public static List<Integer> sieve(int n){
        if(n < 2) return new LinkedList<Integer>();
        LinkedList<Integer> primes = new LinkedList<Integer>();
        LinkedList<Integer> nums = new LinkedList<Integer>();

        for(int i = 2;i <= n;i++){ //unoptimized
            nums.add(i);
        }

        while(nums.size() > 0){
            int nextPrime = nums.remove();
            for(int i = nextPrime * nextPrime;i <= n;i += nextPrime){
                nums.removeFirstOccurrence(i);
            }
            primes.add(nextPrime);
        }
        return primes;
    }

    private static final int MaxNumber = 10000;

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
        List<Integer> numbers = generateNumbersLessThan(MaxNumber);
        int sum = 0;
        for (int n: numbers){
            sum += n;
            System.out.println("current number: " + n + " sum: " + sum);
            assertTrue(n + " isn't prime number", isPrime(n));
        }
    }

    @Test
    public void testBothAlgorithmsReturnTheSameNumbers(){
        Set<Integer> numbers1 = new HashSet<>(sieve(MaxNumber));
        int sum1 = sum(numbers1);
        assertFalse(numbers1.isEmpty());
        Set<Integer> numbers2 = new HashSet<>(generateNumbersLessThan(MaxNumber));
        assertFalse(numbers2.isEmpty());
        int sum2 = sum(numbers2);
        // results must be the same
        assertEquals(sum1, sum2);
        assertEquals(numbers1.size(), numbers2.size());
        Set<Integer> diff = new HashSet<>(numbers1);
        diff.removeAll(numbers2);
        assertTrue(diff.isEmpty());
    }

    /**
     * Checks that number is a prime number
     * @param n
     * @return
     */
    private boolean isPrime(long n){
        for(long i=2; i <= sqrt(n); i++)
        if(n % i ==0)
            return false;
        return true;
    }

    private List<Integer> generateNumbersLessThan(int max){
        List<Integer> numbers = new ArrayList<>();
        Integer n = algorithmInstance.next().intValue();
        while (n < max){
            numbers.add(n);
            n = algorithmInstance.next().intValue();
        }
        return numbers;
    }

    private Integer sum(Iterable<Integer> collection){
        int sum = 0;
        for (int n : collection){
            sum += n;
        }
        return sum;
    }
}
