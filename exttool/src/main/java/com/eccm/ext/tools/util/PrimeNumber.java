package com.eccm.ext.tools.util;

import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
/**
 * 质数判断
 * @author bayern
 *
 */
public class PrimeNumber {
	private Long number;
    private Map<Integer, Integer> cache;

    public PrimeNumber() {
        cache = new HashMap<Integer, Integer>();
    }

    public PrimeNumber setCandidate(Long number) {
        this.number = number;
        return this;
    }

    public static PrimeNumber getPrime(Long number) {
        return new PrimeNumber().setCandidate(number);
    }

    public boolean isFactor(Long potential) {
        return number % potential == 0;
    }

    public Set<Long> getFactors() {
        Set<Long> factors = new HashSet<Long>();
        factors.add(1l);
        factors.add(number);
        for (long i = 2; i < sqrt(number) + 1; i++)
            if (isFactor(i)) {
                factors.add(i);
                factors.add(number / i);
            }
        return factors;
    }

    public long sumFactors() {
        int sum = 0;
        if (cache.containsValue(number))
            sum = cache.get(number);
        else
            for (long i : getFactors())
                sum += i;
        return sum;
    }

    public boolean isPrime() {
        return number == 2 || sumFactors() == number + 1;
    }
    
    public static void main(String[] args) {
    	PrimeNumber pnum = PrimeNumber.getPrime(7l);
    	//pnum.setCandidate(7l);
    	
    	System.out.println(pnum.getFactors()+"@@"+pnum.isPrime());
    	
	}
}