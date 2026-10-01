package com.vmc.java25.virtual.threads.scalable;

import java.util.Arrays;
import java.util.concurrent.*;

public class UserHandler implements Callable<String> {
    public UserHandler() {
    }

    @Override
    public String call() throws Exception {
        
        completableFeatureCall();
        return synchronousCall();
    }

    private void completableFeatureCall() {
    }

    private void concurrentFuncationalCall() throws InterruptedException {
     /*   try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.invokeAll(Arrays.asList(this::dbCall,this::restCall)).stream().
                    map((result1,result2)-> {
                        try {
                            String result=result1.get()+result2.get();
                            return result;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    });

        }*/
    }

    public String synchronousCall() {
        long startTime = System.currentTimeMillis();
        String result1 = dbCall();
        String result2 = restCall();
        String result = result1 + result2;
        long endTime = System.currentTimeMillis();
        long timeTook = endTime - startTime;
        System.out.println("==================Time taken:" + timeTook);
        return result;
    }

    public String concurrentCall() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> dbCall = executorService.submit(this::dbCall);
            Future<String> restCall = executorService.submit(this::restCall);
            String result = dbCall.get() + restCall.get();
            long endTime = System.currentTimeMillis();
            long timeTook = endTime - startTime;
            System.out.println("==================Time taken:" + timeTook);
            return result;
        }

    }


    private String dbCall() {
        System.out.println("dbCall " + Thread.currentThread().getName());
        NetworkCall networkCall = new NetworkCall();

        return networkCall.delay(2);
    }

    private String restCall() {
        System.out.println("restCall " + Thread.currentThread().getName());
        NetworkCall networkCall = new NetworkCall();

        return networkCall.delay(5);
    }
}
