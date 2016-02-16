package com.devthread.manualreseteventdemo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by RoshanF on 1/27/2016.
 */
public class ThreadManager {

    private int counter = 0;
    private ManualResetEvent eventHandle = null;
    private Lock eventHandleLock = new ReentrantLock();

    /**
     * Constructor of the ThreadManager Class
     * Initializes and starts the Counter Thread, the Thread with sleep() and the Thread with ManualResetEvent
     */
    public ThreadManager() {

        eventHandle = new ManualResetEvent(false);

        /**
         * The counter thread increments a counter and set the ManualResetEvent handle
         */
        Thread counterThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    counter++;
                    System.out.println("T[" + System.currentTimeMillis() + "][Counter Thread] Counter Value changed");

                    eventHandleLock.lock();

                    eventHandle.set();

                    eventHandleLock.unlock();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        System.out.println("[Counter Thread] InterruptedException : " + ie);
                    }
                }
            }
        });
        counterThread.start();

        /**
         * The thread with sleep uses a Thread.sleep() to wait and prints the counter
         */
        Thread threadWithSleep = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    System.out.println("T[" + System.currentTimeMillis() + "][Thread With Sleep] Counter Value : " + counter);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        System.out.println("[Thread With Sleep] InterruptedException : " + ie);
                    }
                }
            }
        });
        threadWithSleep.start();

        /**
         * The threadWithManualResetEvent only prints the counter only when the
         * counter thread triggers that there is a change in the counter to print
         */
        Thread threadWithManualResetEvent = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        eventHandle.waitOne();

                        eventHandleLock.lock();

                        System.out.println("T[" + System.currentTimeMillis() + "][Thread With ManualResetEvent] Counter Value : " + counter);
                        eventHandle.reset();

                        eventHandleLock.unlock();

                    } catch (InterruptedException ie) {
                        System.out.println("[Thread With ManualResetEvent] InterruptedException : " + ie);
                    }
                }
            }
        });
        threadWithManualResetEvent.start();
    }
}
