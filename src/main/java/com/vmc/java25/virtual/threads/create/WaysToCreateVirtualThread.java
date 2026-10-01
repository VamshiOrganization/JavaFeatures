package com.vmc.java25.virtual.threads.create;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public class WaysToCreateVirtualThread {

    public static void handleVirtualThread() {
        System.out.println("Thread started " + Thread.currentThread().getName());
        try {
            Thread.sleep(Duration.ofMinutes(10));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Thread ended " + Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        //2. Using Thread.startVirtualThread() (Direct Utility Method)
        // createSimpleVirtualThread();
        //1. Using Thread.ofVirtual() (Builder Pattern)
        //       createUsingBuilder();
        //4. Using a ThreadFactory
        //createUsingThreadFactory();
        //3. Using Executors.newVirtualThreadPerTaskExecutor() (ExecutorService)
       //createUsingExecutorService();
        createSimpleThread();

    }

    private static void createSimpleThread() {
        IntStream.range(0,125000).forEach(j->
        new Thread(WaysToCreateVirtualThread::handleVirtualThread).start()
        );
    }

    private static void createUsingBuilder() throws InterruptedException {
        Thread thread1 = Thread.ofVirtual().name("my-virtual-thread", 0).start(WaysToCreateVirtualThread::handleVirtualThread);
        Thread thread2 = Thread.ofVirtual().name("my-virtual-thread", 1).start(WaysToCreateVirtualThread::handleVirtualThread);
        thread1.join();
        thread2.join();
    }

    private static void createUsingExecutorService() {
        /*
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 2).forEach(j -> executorService.submit(WaysToCreateVirtualThread::handleVirtualThread));
        }
        */

        ThreadFactory factory = Thread.ofPlatform().name("worker-", 1) // Produces worker-1, worker-2, etc.
                .factory();
        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(factory)) {
            IntStream.range(0, 12500).forEach(j -> executorService.submit(WaysToCreateVirtualThread::handleVirtualThread));
        }


    }

    private static void createUsingThreadFactory() throws InterruptedException {
        // Define factory with a custom name pattern
        ThreadFactory factory = Thread.ofVirtual().name("worker-", 1) // Produces worker-1, worker-2, etc.
                .factory();
        Thread t1 = factory.newThread(WaysToCreateVirtualThread::handleVirtualThread);
        Thread t2 = factory.newThread(WaysToCreateVirtualThread::handleVirtualThread);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static void createSimpleVirtualThread() throws InterruptedException {
        Thread t1 = Thread.startVirtualThread(WaysToCreateVirtualThread::handleVirtualThread);
        Thread t2 = Thread.startVirtualThread(WaysToCreateVirtualThread::handleVirtualThread);

        t1.join();
        t2.join();
        /* output
        Thread started
        Thread started
        Thread ended
        Thread ended
         */
    }

}
