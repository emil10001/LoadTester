package com.feigdev.loadtester;

/**
 * Created by ejf3 on 11/5/13.
 */
public enum Constants {
    LOW(3, 1000, 1000 * 5, 1, 8, 1000 * 3),
    MEDIUM(6, 1000, 1000 * 3, 1, 8, 100),
    HIGH(9, 1000, 1000 * 2, 2, 8, 500);

    static final boolean TIMED_KILL = false;
    static final long KEEP_ALIVE = 1000 * 60;

    final int NUM_CPU_THREADS;
    final long CPU_ON_TIME;
    final long CPU_IDLE_TIME;
    final long CPU_STAGGER;

    final int NUM_RAM_THREADS;
    final int NUM_STORED_IMAGES;
    final long RAM_IDLE_TIME;

    final int NUM_THREADS;

    static Constants MODE = LOW;

    Constants(int numCpuThreads, long cpuOnTime, long cpuIdleTime,
              int numRamThreads, int numStoredImages, long ramIdleTime){
        NUM_CPU_THREADS = numCpuThreads;
        CPU_ON_TIME = cpuOnTime;
        CPU_IDLE_TIME = cpuIdleTime;
        CPU_STAGGER = (CPU_ON_TIME + CPU_IDLE_TIME) / NUM_CPU_THREADS;

        NUM_RAM_THREADS = numRamThreads;
        NUM_STORED_IMAGES = numStoredImages;
        RAM_IDLE_TIME = ramIdleTime;

        NUM_THREADS = NUM_CPU_THREADS + NUM_RAM_THREADS;
    }



}
