package com.feigdev.loadtester;

/**
 * Created by ejf3 on 11/5/13.
 */
public enum Constants {
    LOW(6, 1000, 1000 * 5,
            2, 8, 1000 * 3,
            4, 2000, 1000 * 10),
    MEDIUM(12, 1000, 1000 * 3,
            2, 16, 100,
            8, 1000, 1000 * 5),
    HIGH(28, 1000, 1000 * 2,
            4, 32, 500,
            12, 500, 500);

    static final boolean TIMED_KILL = false;
    static final long KEEP_ALIVE = 1000 * 60;

    final int NUM_CPU_THREADS;
    final long CPU_ON_TIME;
    final long CPU_IDLE_TIME;
    final long CPU_STAGGER;


    final int NUM_NET_THREADS;
    final long NET_DELAY_TIME;
    final long NET_IDLE_TIME;

    final int NUM_RAM_THREADS;
    final int NUM_STORED_IMAGES;
    final long RAM_IDLE_TIME;

    final int NUM_THREADS;

    static boolean CPU_ENABLED = true;
    static boolean RAM_ENABLED = true;
    static boolean NET_ENABLED = true;

    static Constants MODE = LOW;

    Constants(int numCpuThreads, long cpuOnTime, long cpuIdleTime,
              int numRamThreads, int numStoredImages, long ramIdleTime,
              int numNetThreads, long netDelayTime, long netIdleTime){
        NUM_CPU_THREADS = numCpuThreads;
        CPU_ON_TIME = cpuOnTime;
        CPU_IDLE_TIME = cpuIdleTime;
        CPU_STAGGER = (CPU_ON_TIME + CPU_IDLE_TIME) / NUM_CPU_THREADS;

        NUM_RAM_THREADS = numRamThreads;
        NUM_STORED_IMAGES = numStoredImages;
        RAM_IDLE_TIME = ramIdleTime;

        NUM_NET_THREADS = numNetThreads;
        NET_DELAY_TIME = netDelayTime;
        NET_IDLE_TIME = netIdleTime;

        NUM_THREADS = NUM_CPU_THREADS + NUM_RAM_THREADS + NUM_NET_THREADS;
    }

}
