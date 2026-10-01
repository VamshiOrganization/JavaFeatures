package com.vmc.java25.virtual.threads.interrupt;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class StructuredConcurrencyAllTasksInterrupt2 {
    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        mainInterruptFunction();
        exampleCompleteAllTasks();
        System.out.println("Main : Completed");
    }

    private static void mainInterruptFunction() {
        System.out.println("mainInterruptFunction : Started");
        Thread mainThread=Thread.currentThread();
        Thread.ofPlatform().start(()->{
            try {
                Thread.sleep(Duration.ofSeconds(5));
                mainThread.interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("mainInterruptFunction : Ended");
    }

    private static void exampleCompleteAllTasks() throws Exception {
        try (var scope = new StructuredTaskScope<TaskResponse>()) {
            var expTask = new LongRunningThread("expedia-task", 3, "100$", false);
            var hotTask = new LongRunningThread("hotwire-task", 10, "110$", false);
            Subtask<TaskResponse> expSubTask = scope.fork(expTask);
            Subtask<TaskResponse> hotSubTask = scope.fork(hotTask);

            //Wait for all task to complete(success of not)
            //scope.joinUntil(Instant.now().plusSeconds(5));
            scope.join();

            switch (expSubTask.state()) {
                case SUCCESS -> System.out.println("Expedia Result: " + expSubTask.get());
                case FAILED  -> System.out.println("Expedia Failed: " + expSubTask.exception().getMessage());
                case UNAVAILABLE -> System.out.println("Expedia Task did not complete");
            }

            // Inspect Hotwire task
            switch (hotSubTask.state()) {
                case SUCCESS -> System.out.println("Hotwire Result: " + hotSubTask.get());
                case FAILED  -> System.out.println("Hotwire Failed: " + hotSubTask.exception().getMessage());
                case UNAVAILABLE -> System.out.println("Hotwire Task did not complete");
            }

        }
    }
}
