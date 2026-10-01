ere are the differences between `StructuredConcurrencyCustomHandling5` (with its custom scope `CustomAverageWhetherReport`) and our baseline, **`StructuredConcurrencyAllTasks1`**:

### 1. Custom Scope Subclassing vs Built-in Scope

-   **`StructuredConcurrencyAllTasks1`:** Uses standard `new StructuredTaskScope<TaskResponse>()` without overriding internal lifecycle hooks.

-   **This Code:** Extends `StructuredTaskScope<TaskResponse>` to create a custom policy class (`CustomAverageWhetherReport`) that overrides `handleComplete(Subtask)` to implement custom aggregation logic.


### 2. Custom Completion Callback (`handleComplete`) & Early Shutdown

-   **`StructuredConcurrencyAllTasks1`:** Has no `handleComplete` callback; every task simply finishes and waits until `scope.join()` completes naturally.

-   **This Code:** Every time a subtask completes, `handleComplete(subtask)` is triggered automatically. Once **2 successful subtasks** are collected into `successSubtaskList`, it calls `this.shutdown()`, which cancels all remaining running subtasks immediately.


### 3. Number of Subtasks & Failure Configurations

-   **`StructuredConcurrencyAllTasks1`:** Forks **2 subtasks** (`expedia-task` for 3s, `hotwire-task` for 10s), both set to `fail = false`.

-   **This Code:** Forks **6 subtasks** (`whether-report-1` through `6` taking 3s, 4s, 6s, 7s, 9s, and 10s), but all are set to **`fail = true`**.


### 4. Custom Result Aggregation (`response()` method)

-   **`StructuredConcurrencyAllTasks1`:** Manually checks individual subtask states after `scope.join()` using `expSubTask.get()` / `expSubTask.state()`.

-   **This Code:** Delegates result calculation to a custom `scope.response()` method. It validates state using `ensureOwnerAndJoined()`, extracts outputs from the top 2 successful tasks, parses them to integers, and calculates an average weather report.


### 5. Runtime Execution & Exception Behavior (All Tasks Failing)

-   **`StructuredConcurrencyAllTasks1`:** Both tasks succeed; code completes cleanly in **10 seconds**.

-   **This Code:**

    1.  Because all 6 tasks are set to `fail = true`, none reach `Subtask.State.SUCCESS`.

    2.  `numSuccessful == 2` is never reached, so `this.shutdown()` is never called.

    3.  `scope.join()` waits out the full **10 seconds** for all 6 tasks to fail.

    4.  Finally, calling `scope.response()` throws a **`RuntimeException: Atleast 2 tasks must be succesfull`** because `successSubtaskList.size()` is `0`.

### Summary Comparison Table

| **Metric / Aspect**    | **Baseline: StructuredConcurrencyAllTasks1** | **StructuredConcurrencyCustomHandling5**                        |
| ---------------------- | -------------------------------------------- | --------------------------------------------------------------- |
| **Scope Class**        | Standard `StructuredTaskScope<TaskResponse>` | Custom `CustomAverageWhetherReport extends StructuredTaskScope` |
| **Lifecycle Hook**     | None                                         | Overrides `handleComplete(Subtask)`                             |
| **Forked Tasks Count** | 2 Tasks                                      | 6 Tasks                                                         |
| **Task Failure Flags** | All `fail = false`                           | All `fail = true`                                               |
| **Shutdown Trigger**   | None (Waits for all)                         | Shuts down early after **2 successes**                          |
| **Result Retrieval**   | Individual `subtask.get()` checks in `main`  | Custom `scope.response()` averaging outputs                     |
| **Outcome**            | Returns 2 individual responses cleanly       | Throws `RuntimeException` after 10s due to all tasks failing    |