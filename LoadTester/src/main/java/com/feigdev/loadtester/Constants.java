package com.feigdev.loadtester;

/**
 * Created by ejf3 on 11/5/13.
 */
public class Constants {
    static boolean TIMED_KILL = false;

    static long KEEP_ALIVE = 1000 * 60;

    static int NUM_CPU_THREADS = 3;
    static long CPU_ON_TIME = 1000;
    static long CPU_IDLE_TIME = 1000 * 2;

    static long RAM_IDLE_TIME = 500;
    static int NUM_RAM_THREADS = 1;
    static int NUM_STORED_IMAGES = 8;

    static int NUM_THREADS = NUM_CPU_THREADS + NUM_RAM_THREADS;

}
