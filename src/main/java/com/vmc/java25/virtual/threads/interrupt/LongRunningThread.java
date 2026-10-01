package com.vmc.java25.virtual.threads.interrupt;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LongRunningThread implements Callable<TaskResponse> {
    private final String name;
    private final long time;
    private final String output;
    private final boolean fail;


    public LongRunningThread(String name, long time, String output, boolean fail) {
        this.name = name;
        this.time = time;
        this.output = output;
        this.fail = fail;
    }

    public TaskResponse call()  {
        long start = System.currentTimeMillis();
        print("Started");
        int numSeconds=0;
        while ((numSeconds++)<this.time) {
            if(Thread.currentThread().isInterrupted()){
                System.out.println("Interrupted");
                throwExceptionOnFailure();
            }
            print("Working..."+numSeconds);
            try {
                Thread.sleep(Duration.ofSeconds(1));
            }catch (Exception e){
                throwExceptionOnFailure();
            }

        }
        if(fail){
            throwExceptionOnFailure();
        }
        print("Completed");
        long end = System.currentTimeMillis();
        return new TaskResponse(this.name,this.output, end-start);
    }
    private void throwExceptionOnFailure(){
        print("Interrupted");
        throw new RuntimeException(name +": Failed");
    }

    private void print(String message){
        System.out.printf("> %s : %s\n",name,message);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main started");
        LongRunningThread longRunningThread = new LongRunningThread("LongTask1",10,"json-response1",true);
        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<TaskResponse> future = executorService.submit(longRunningThread::call);
            Thread.sleep(Duration.ofSeconds(5));
            future.cancel(true);
        }
        System.out.println("Main ended");
    }
}
