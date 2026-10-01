package com.vmc.java25.virtual.threads.interrupt;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

public class StackSimpleExamples {
    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        exampleCompleteAllTasks();
        System.out.println("Main : Completed");
    }

    private static void exampleCompleteAllTasks() throws Exception {
        try (var scope = new StructuredTaskScope<TaskResponse>()) {
            var expTask = new LongRunningThread("expedia-task", 3, "100$", false);
            var hotTask = new LongRunningThread("hotwire-task", 10, "110$", false);
            Subtask<TaskResponse> expSubTask = scope.fork(expTask);
            Subtask<TaskResponse> hotSubTask = scope.fork(hotTask);

            //Wait for all task to complete(success of not)
            scope.join();

            State expState = expSubTask.state();
            if (expState == State.SUCCESS) {
                System.out.println(expSubTask.get());
            } else if (expState == State.FAILED) {
                System.out.println(expSubTask.exception());
            }

            State hotState = hotSubTask.state();
            if (hotState == State.SUCCESS) {
                System.out.println(hotSubTask.get());
            } else if (hotState == State.FAILED) {
                System.out.println(hotSubTask.exception());
            }

        }
    }
}
