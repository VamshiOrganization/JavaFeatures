package com.vmc.java25.virtual.threads.interrupt;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

 class StructuredConcurrencyShutdownOnSuccess4 {
    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        exampleCompleteAllTasks();
        System.out.println("Main : Completed");
    }

    private static void exampleCompleteAllTasks() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<TaskResponse>()) {
            var wr1Task = new LongRunningThread("whether-report-1", 3, "100$", false);
            var wr2Task = new LongRunningThread("whether-report-2", 10, "110$", false);
            Subtask<TaskResponse> wr1SubTask = scope.fork(wr1Task);
            Subtask<TaskResponse> wr2SubTask = scope.fork(wr2Task);


            //Wait for all task to complete(success of not)
            scope.join();

            State wr1State = wr1SubTask.state();
            if (wr1State == State.SUCCESS) {
                System.out.println(wr1SubTask.get());
            } else if (wr1State == State.FAILED) {
                System.out.println(wr1SubTask.exception());
            }



            State wr2State = wr2SubTask.state();
            if (wr2State == State.SUCCESS) {
                System.out.println(wr2SubTask.get());
            } else if (wr2State == State.FAILED) {
                System.out.println(wr2SubTask.exception());
            }

        }
    }
}