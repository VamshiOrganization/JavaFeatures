This code(StackSimpleExamples.java) introduces **Structured Concurrency** (`StructuredTaskScope`), a core component of Project Loom designed to eliminate dangling threads, thread leaks, and uncoordinated cancellation.

### Code Breakdown

1.  **`try (var scope = new StructuredTaskScope<TaskResponse>())`**: Creates a structured concurrent boundary. Every task spawned inside this block is bound to the lifespan of the `scope`. When the `try-with-resources` block closes, it automatically waits for or cleans up all child threads.

2.  **`scope.fork(...)`**: Spawns each task (`expTask` and `hotTask`) asynchronously on its own **Virtual Thread**. It returns a `Subtask<T>` handle to monitor state.

3.  **`scope.join()`**: Blocks the calling thread until **all** forked subtasks finish execution (whether they complete successfully or throw an exception).

4.  **`Subtask.state()` Inspection**: Evaluates whether each subtask ended in `State.SUCCESS` or `State.FAILED`:

    -   `expSubTask.get()` retrieves the `TaskResponse` if successful.

    -   `expSubTask.exception()` retrieves the thrown `Throwable` if it failed.


### What Changes in Modern Java (Java 21–25 Preview API Evolution)

Structured Concurrency has evolved across preview JDK releases (JDK 21 through JDK 25). The major update to be aware of when targeting Java 25 is the **redesign of `Subtask` handling and scope policies**.

#### Key Java 25 API Adjustments:

1.  **`Subtask` Implements `Supplier<T>`:** Instead of `subtask.get()`,  `Subtask<T>` implements `Supplier<T>`, allowing direct invocation via `subtask.get()`. Calling `.get()` on an unfinished or failed task throws an `IllegalStateException`.

2.  **`join()` Exception Handling:**`scope.join()` throws `InterruptedException`, which must be handled or re-thrown.

3.  **Built-in Scope Policies (`ShutdownOnFailure`, `ShutdownOnSuccess`):** When using standard scopes (like `StructuredTaskScope.ShutdownOnFailure()`),  `scope.throwIfFailed()` replaces manual state checking for simpler code.


### Updated & Idiomatic Java 25 Code

Below is your example refactored using the latest Java 25 `StructuredTaskScope` conventions:

Java

```
package com.vmc.java25.virtual.threads.interrupt;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class StackSimpleExamples {

    public static void main(String[] args) throws Exception {
        System.out.println("Main : Started");
        exampleCompleteAllTasks();
        exampleShortCircuitOnFailure();
        System.out.println("Main : Completed");
    }

    /**
     * Approach 1: Wait for ALL subtasks to complete (inspecting individually)
     */
    private static void exampleCompleteAllTasks() throws InterruptedException {
        // Basic StructuredTaskScope spawns Virtual Threads for all forked tasks
        try (var scope = new StructuredTaskScope<TaskResponse>()) {
            
            var expTask = new LongRunningThread("expedia-task", 3, "100$", false);
            var hotTask = new LongRunningThread("hotwire-task", 10, "110$", false);

            // Forking spawns a virtual thread per task
            Subtask<TaskResponse> expSubTask = scope.fork(expTask);
            Subtask<TaskResponse> hotSubTask = scope.fork(hotTask);

            // Wait for all tasks to finish
            scope.join();

            // Inspect Expedia task
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
        } // Scope closure guarantees all child virtual threads are terminated
    }

    /**
     * Approach 2: Short-circuit on failure (ShutdownOnFailure policy)
     * If one task fails, remaining tasks are automatically cancelled via Thread.interrupt()!
     */
    private static void exampleShortCircuitOnFailure() throws Exception {
        System.out.println("\n--- Testing ShutdownOnFailure Policy ---");
        
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            // expedia will fail after 3s, automatically cancelling hotwire (10s)
            var expTask = new LongRunningThread("expedia-failing-task", 3, "100$", true);
            var hotTask = new LongRunningThread("hotwire-task", 10, "110$", false);

            Subtask<TaskResponse> expSubTask = scope.fork(expTask);
            Subtask<TaskResponse> hotSubTask = scope.fork(hotTask);

            scope.join();            // Joins scope
            scope.throwIfFailed();   // Throws ExecutionException if any subtask failed

            // If we reach here, all subtasks succeeded
            System.out.println(expSubTask.get());
            System.out.println(hotSubTask.get());
            
        } catch (Exception e) {
            System.out.println("Scope failed fast with exception: " + e.getMessage());
        }
    }
}

```

### Key Execution Highlights

1.  **Short-Circuiting in Action (`ShutdownOnFailure`):** In `exampleShortCircuitOnFailure()`, when `expedia-failing-task` fails after 3 seconds,  `StructuredTaskScope` automatically sends an **interrupt signal** to `hotwire-task`. The 10-second task stops early instead of wasting resources for the full 10 seconds.

2.  **Virtual Thread Native Support:**`scope.fork(...)` uses virtual threads under the hood, meaning you don't need to manually configure thread pools or pass an `ExecutorService`.