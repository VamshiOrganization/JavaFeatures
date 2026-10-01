package com.vmc.java25.virtual.threads.oom;

import java.util.ArrayList;
import java.util.List;

public class ThreadOutOfMemoryIssue {

    public static void run() {

        System.out.println("Thread started: " + Thread.currentThread().getName()+" -1");
            try {
                NativeMemoryChecker.printThreadMemory();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Thread ended: " + Thread.currentThread().getName()+" -1");
    }
    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 100000; i++) {
            /*    Thread thread = new Thread(()->run());
            thread.start();
            */
            threads.add(Thread.startVirtualThread(() -> run()));
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
