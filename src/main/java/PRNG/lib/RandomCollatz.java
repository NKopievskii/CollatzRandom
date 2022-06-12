package PRNG.lib;

import java.io.Serializable;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class RandomCollatz {
    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private boolean useShuffle;
    private long seed;
    private final Stack<Long> stack;
    private int stackElements;
    protected long currentIteration;
    public RandomCollatz() {
        this.useShuffle = true;
        this.setSeed(1748742479622468221L ^ System.nanoTime());
        stackElements = 64;
        stack = new Stack<>();
        currentIteration = getSeed();
        nextArray();
    }
    public RandomCollatz(long seed) {
        this.useShuffle = true;
        stackElements = 64;
        stack = new Stack<>();
        this.setSeed(seed);
        currentIteration = getSeed();
        nextArray();
    }
    public boolean isUseShuffle() {
        return useShuffle;
    }
    public void setUseShuffle(boolean useShuffle) {
        this.useShuffle = useShuffle;
    }
    public void setSeed(long seed){
        this.seed = seed;
    }
    public long getSeed(){
        return this.seed;
    }
    public void setStackElements(int elements){
        if (elements > 0)
            this.stackElements = elements;
    }
    protected void nextArray(){
        if (stack.isEmpty()) {
            long nextSeed;
            for (int i = 0; i < stackElements; i++) {
                nextSeed = currentIteration;
                if ((nextSeed & 1) == 0)
                    nextSeed = ((nextSeed >> 1) * 3) ^ 2863311530L;
                else
                    nextSeed =  ((3 * nextSeed) + 1) >> 1;
                currentIteration = nextSeed;
                stack.push(nextSeed);
            }
            if (isUseShuffle())
                Collections.shuffle(stack, new HashRandom());
        }
    }
    protected long next(int bits) {
        if (stack.isEmpty()) {
            synchronized (stack) {
                nextArray();
            }
        }
        long nextSeed;
        synchronized (stack){
            nextSeed = stack.pop();
        }
        return (nextSeed >>> (64 - bits));
    }
    public long nextLong(){
        return next(64);
    }
   public int nextInt(){
        return (int) next(32);
    }
    public boolean nextBoolean() {
        return next(1) != 0;
    }
    public float nextFloat() {
        return next(24) / ((float)(1 << 24));
    }
    public double nextDouble() {
        return (((long)(next(26)) << 27) + next(27)) * DOUBLE_UNIT;
    }
    public class HashRandom extends Random implements Serializable {
        int index = 0;
        @Override
        synchronized protected int next(final int bits){
            if (index >= stack.size())
                index=0;
            return Math.abs((((stack.get(index++).hashCode()^stack.hashCode())%stack.hashCode()) >>> (32 - bits)));
        }
    }
}
