### Key Mechanics of Your Code(LongRunningThread.java)

#### 1. How Interruption Works in Java

Calling `future.cancel(true)` sends an **interruption signal** (`Thread.interrupt()`) to the thread running the task.

Interruption in Java is **cooperative**:

-   It sets an internal boolean flag (`isInterrupted() = true`) on the target thread.

-   It forces any **blocking method** (like `Thread.sleep()`) to immediately wake up and throw an `InterruptedException`.


#### 2. Why Your Code Catches Interruption Twice

Looking at your `call()` loop:

Java

```
while ((numSeconds++) < this.time) {
    if (Thread.currentThread().isInterrupted()) { // <--- Check #1
        System.out.println("Interrupted");
        throwExceptionOnFailure();
    }
    print("Working..." + numSeconds);
    try {
        Thread.sleep(Duration.ofSeconds(1));     // <--- Check #2 (Blocking Call)
    } catch (Exception e) {
        throwExceptionOnFailure();
    }
}

```

Here is what happens step-by-step when `main()` calls `future.cancel(true)` after 5 seconds:

1.  **`main` thread sleeps for 5 seconds:** `LongRunningThread` loops through seconds 1, 2, 3, 4, 5.

2.  **`future.cancel(true)` executes:** The thread executing `call()` is currently inside `Thread.sleep(Duration.ofSeconds(1))`.

3.  **`Thread.sleep()` throws `InterruptedException`:** Sleeping threads do not wait for the next loop iteration; `Thread.sleep()` interrupts immediately, jumping directly into your `catch (Exception e)` block.

4.  **Task fails gracefully:** `throwExceptionOnFailure()` prints the message and throws a `RuntimeException`, terminating the task cleanly.


### Expected Console Output

Plaintext

```
Main started
> LongTask1 : Started
> LongTask1 : Working...1
> LongTask1 : Working...2
> LongTask1 : Working...3
> LongTask1 : Working...4
> LongTask1 : Working...5
> LongTask1 : Interrupted
Main ended

```

### Important Bug / Critical Nuance in Your Code

There is a very subtle detail in how `Thread.sleep()` interacts with the interruption flag:

> **When `Thread.sleep()` catches an `InterruptedException`, it automatically CLEARS the interruption status flag (`isInterrupted()` resets back to `false`).**

If you catch `InterruptedException` and **don't** throw an exception or restore the interrupt status via `Thread.currentThread().interrupt()`, your `if (Thread.currentThread().isInterrupted())` check on the next loop iteration will evaluate to `false`!

Because your `catch (Exception e)` block immediately calls `throwExceptionOnFailure()`, your task exits cleanly. But in general practice, always preserve the interrupt status when catching `InterruptedException`:

Java

```
try {
    Thread.sleep(Duration.ofSeconds(1));
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // Restore interrupted status!
    throwExceptionOnFailure();
}

```

### Virtual Threads vs. Platform Threads Interruption

Whether you run this code on a **Platform Thread** (like your `Executors.newFixedThreadPool(2)`) or a **Virtual Thread** (`Executors.newVirtualThreadPerTaskExecutor()`), thread interruption mechanics work **identically**:

Java

```
// Switching to Virtual Threads in Chapter 3:
try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<TaskResponse> future = executorService.submit(longRunningThread::call);
    Thread.sleep(Duration.ofSeconds(5));
    future.cancel(true); // Sends interrupt to the Virtual Thread!
}

```

When a Virtual Thread is interrupted while unmounted during `Thread.sleep()`, the JVM wakes it up, remounts it onto a Carrier OS Thread, and throws `InterruptedException` exactly like a standard OS thread.