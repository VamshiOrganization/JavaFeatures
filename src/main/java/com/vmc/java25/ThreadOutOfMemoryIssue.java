package com.vmc.java25;

public class ThreadOutOfMemoryIssue implements Runnable {

    public void run() {

        System.out.println("Thread started: " + Thread.currentThread().getName());
            try {
                long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
                long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);

                System.out.println("Used Memory: " + (totalMem - freeMem) + " MB");
              //  System.out.println("Allocated Memory: " + totalMem + " MB");
               // System.out.println("Max Heap (-Xmx): " + maxMem + " MB");
                NativeMemoryChecker.printThreadMemory();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Thread ended: " + Thread.currentThread().getName());
    }
    public static void main(String[] args) {

        for (int i = 0; i < 100000; i++) {
            Thread thread = new Thread(new ThreadOutOfMemoryIssue());
            thread.start();
        }

    }
}
