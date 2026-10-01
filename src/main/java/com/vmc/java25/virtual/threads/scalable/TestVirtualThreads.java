package com.vmc.java25.virtual.threads.scalable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestVirtualThreads {
    private static final int NUM_OF_USERS=2;
    public static void main(String[] args) {
       try(ExecutorService executorService= Executors.newFixedThreadPool(2)){
           IntStream.range(0,NUM_OF_USERS).forEach(j->
           executorService.submit(new UserHandler())
           );
       }
    }
}
