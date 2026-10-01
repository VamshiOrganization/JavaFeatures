### 1. Scope Type (`ShutdownOnFailure` vs Generic Scope)

-   **`StructuredConcurrencyAllTasks1`:** Uses `new StructuredTaskScope<TaskResponse>()` (a standard scope that waits for all tasks to complete regardless of failure).

-   **This Code:** Uses `new StructuredTaskScope.ShutdownOnFailure()` (a policy scope that triggers short-circuit cancellation as soon as any single subtask fails).


### 2. Failure Parameter in Task 1

-   **`StructuredConcurrencyAllTasks1`:** Sets the failure flag to `false` (`expTask`: `fail = false`).

-   **This Code:** Sets the failure flag to `true` (`dbTask`: `fail = true`), forcing Task 1 to throw an exception after 3 seconds.


### 3. Task Names

-   **`StructuredConcurrencyAllTasks1`:** Named tasks `"expedia-task"` and `"hotwire-task"`.

-   **This Code:** Named tasks `"db-task"` and `"rest-task"`.


### 4. Behavioral Impact (Total Execution Time & Early Cancellation)

-   **`StructuredConcurrencyAllTasks1`:** Runs for **10 seconds total** because both tasks run to completion.

-   **This Code:** Runs for **3 seconds total**. When `dbTask` fails at second 3, `ShutdownOnFailure` automatically sends an interrupt to cancel `restTask` early, causing `scope.join()` to return immediately without waiting out the remaining 7 seconds.


### Summary Table

| **Code Metric**         | **StructuredConcurrencyAllTasks1**                   | **StructuredConcurrencyShutdownOnFailure3**                 |
| ----------------------- | ----------------------------------------- | ----------------------------------------------------------- |
| **Scope Definition**    | `new StructuredTaskScope<TaskResponse>()` | `new StructuredTaskScope.ShutdownOnFailure()`               |
| **Task 1 Failure Flag** | `false`                                   | `true`                                                      |
| **Task Names**          | `"expedia-task"`, `"hotwire-task"`        | `"db-task"`, `"rest-task"`                                  |
| **Task 2 Outcome**      | Runs full 10s to `SUCCESS`                | **Canceled at 3s** (interrupted due to `ShutdownOnFailure`) |
| **Total Runtime**       | 10 Seconds                                | **3 Seconds**                                               |