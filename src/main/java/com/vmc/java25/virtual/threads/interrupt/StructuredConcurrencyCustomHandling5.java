package com.vmc.java25.virtual.threads.interrupt;

import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

public class StructuredConcurrencyCustomHandling5 {
    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        exampleCompleteAllTasks();
        System.out.println("Main : Completed");
    }

    private static void exampleCompleteAllTasks() throws Exception {
        try (var scope = new CustomAverageWhetherReport()) {
            var wr1Task = new LongRunningThread("whether-report-1", 3, "100", true);
            var wr2Task = new LongRunningThread("whether-report-2", 4, "104", true);
            var wr3Task = new LongRunningThread("whether-report-3", 6, "106", true);
            var wr4Task = new LongRunningThread("whether-report-4", 7, "107", true);
            var wr5Task = new LongRunningThread("whether-report-5", 9, "109", true);
            var wr6Task = new LongRunningThread("whether-report-6", 10, "110", true);
            Subtask<TaskResponse> wr1SubTask = scope.fork(wr1Task);
            Subtask<TaskResponse> wr2SubTask = scope.fork(wr2Task);
            Subtask<TaskResponse> wr3SubTask = scope.fork(wr3Task);
            Subtask<TaskResponse> wr4SubTask = scope.fork(wr4Task);
            Subtask<TaskResponse> wr5SubTask = scope.fork(wr5Task);
            Subtask<TaskResponse> wr6SubTask = scope.fork(wr6Task);

            scope.join();
            //Wait for all task to complete(success of not)
            TaskResponse response = scope.response();
            System.out.println(response);

        }

    }
}