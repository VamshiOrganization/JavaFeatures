In Java (introduced as a production feature in **Java 21**), there are **4 primary ways** to create and manage virtual threads:
(WaysToCreateVirtualThread.java)

### 1. Using `Thread.ofVirtual()` (Builder Pattern)

This is the most flexible approach, allowing you to configure parameters like the thread's name, exception handlers, or whether it starts automatically.

#### A. Create and start immediately

Java

```
Thread thread = Thread.ofVirtual()
    .name("my-virtual-thread")
    .start(() -> {
        System.out.println("Running on: " + Thread.currentThread());
    });

thread.join(); // Wait for completion

```

#### B. Create unstarted (Lazy initialization)

Java

```
Thread thread = Thread.ofVirtual()
    .name("unstarted-thread")
    .unstarted(() -> {
        System.out.println("Started manually!");
    });

thread.start(); // Start when ready
thread.join();

```

### 2. Using `Thread.startVirtualThread()` (Direct Utility Method)

If you want to start a simple virtual thread without extra configuration, this static helper method is the fastest option.

Java

```
Thread thread = Thread.startVirtualThread(() -> {
    System.out.println("Quick virtual thread: " + Thread.currentThread());
});

thread.join();

```

### 3. Using `Executors.newVirtualThreadPerTaskExecutor()` (ExecutorService)

This is the **recommended approach for production workloads**. Instead of pooling threads, this `ExecutorService` dynamically spins up a new virtual thread for every submitted task and disposes of it when finished.

Java

```
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        // Automatically closes and waits for all tasks to finish
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            for (int i = 1; i <= 3; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("Task " + taskId + " running on " + Thread.currentThread());
                });
            }
            
        } // executor.close() is called implicitly here
    }
}

```

### 4. Using a `ThreadFactory`

If you are integrating with existing frameworks or components that require a `ThreadFactory`, you can generate virtual threads via a factory instance.

Java

```
import java.util.concurrent.ThreadFactory;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Define factory with a custom name pattern
        ThreadFactory factory = Thread.ofVirtual()
            .name("worker-", 1) // Produces worker-1, worker-2, etc.
            .factory();

        Thread t1 = factory.newThread(() -> {
            System.out.println("Executed by: " + Thread.currentThread());
        });

        t1.start();
        t1.join();
    }
}

```
### Summary Comparison

| **Approach**                                  | **Best Used For**                                                   |
| --------------------------------------------- | ------------------------------------------------------------------- |
| `Thread.ofVirtual()`                          | Custom configurations (naming, exception handlers).                 |
| `Thread.startVirtualThread()`                 | Quick tasks or simple testing/demos.                                |
| `Executors.newVirtualThreadPerTaskExecutor()` | High-concurrency production apps, Web servers, or batch processing. |
| `ThreadFactory`                               | Injecting virtual thread creation into frameworks or existing APIs. |