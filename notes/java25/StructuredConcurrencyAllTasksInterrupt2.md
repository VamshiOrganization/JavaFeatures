
Here are the key structural and behavioral differences introduced in this updated code compared to your previous version:

### 1. External Interruption Mechanism (`mainInterruptFunction`)

-   **Previous:** `main()` invoked `exampleCompleteAllTasks()` directly with no external timer or thread targeting `mainThread`.

-   **New:** `mainInterruptFunction()` spawns a separate background platform thread that sleeps for 5 seconds and then calls `mainThread.interrupt()`. This triggers an interrupt signal on the main thread while it is blocked inside `scope.join()`.


### 2. Handling of Unfinished Tasks (`UNAVAILABLE` State)

-   **Previous:** Used simple `if / else if` checks (`State.SUCCESS` and `State.FAILED`). It assumed every task would finish before inspection.

-   **New:** Uses a `switch` statement that explicitly handles `Subtask.State.UNAVAILABLE`. If `scope.join()` is interrupted at 5 seconds, `hotwire-task` (which takes 10 seconds) will not have finished, so its state evaluates to `UNAVAILABLE` rather than throwing an exception.


### 3. Automatic Scope Cancellation on Main Interrupt

-   **Previous:** The scope waited 10 full seconds for both tasks to complete normally before exiting the `try-with-resources` block.

-   **New:** When `mainThread.interrupt()` fires at 5 seconds:

    1.  `scope.join()` wakes up and throws an `InterruptedException`.

    2.  The `try-with-resources` block automatically closes `scope`.

    3.  Scope closure sends an interrupt signal to all remaining child tasks, canceling `hotwire-task` automatically at 5 seconds instead of letting it run for 10 seconds.


### Summary Table
| **Feature**              | **Previous Version**                  | **New Version**                                |
| ------------------------ | ------------------------------------- | ---------------------------------------------- |
| **Execution Trigger**    | Runs tasks to natural completion      | Interrupts `mainThread` after 5 seconds        |
| **Total Runtime**        | 10 Seconds                            | ~5 Seconds                                     |
| **`hotwire-task` State** | `SUCCESS`                             | canceled with error                            |
| **State Checks**         | `if / else if` (`SUCCESS` / `FAILED`) | `switch` statement incorporating `UNAVAILABLE` |