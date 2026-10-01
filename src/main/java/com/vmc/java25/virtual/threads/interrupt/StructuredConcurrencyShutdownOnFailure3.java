package com.vmc.java25.virtual.threads.interrupt;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

public class StructuredConcurrencyShutdownOnFailure3 {
    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        exampleCompleteAllTasks();
        System.out.println("Main : Completed");
    }

    private static void exampleCompleteAllTasks() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var dbTask = new LongRunningThread("db-task", 3, "100$", true);
            var restTask = new LongRunningThread("rest-task", 10, "110$", false);
            Subtask<TaskResponse> dbSubTask = scope.fork(dbTask);
            Subtask<TaskResponse> restSubTask = scope.fork(restTask);


            //Wait for all task to complete(success of not)
            scope.join();

            State dbState = dbSubTask.state();
            if (dbState == State.SUCCESS) {
                System.out.println(dbSubTask.get());
            } else if (dbState == State.FAILED) {
                System.out.println(dbSubTask.exception());
            }

            State restState = restSubTask.state();
            if (restState == State.SUCCESS) {
                System.out.println(restSubTask.get());
            } else if (restState == State.FAILED) {
                System.out.println(restSubTask.exception());
            }

        }
    }
}