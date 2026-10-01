# Chapter 2 Study Guide & Q&A Summary: Java 25 Virtual Threads & Project Loom
This study guide summarizes the core concepts, execution mechanics, and code patterns covered in Chapter 2.

### Key Concepts & Mechanics

#### 1. The Core Value Proposition of Virtual Threads

Traditional **Platform Threads** map 1:1 to OS threads, where each thread carries a fixed stack footprint (~1–6 MB) and blocks the underlying OS thread during I/O operations. **Virtual Threads** introduce an $M:N$ scheduling model managed entirely by the JVM, allowing millions of concurrent tasks to run efficiently with minimal memory overhead.

#### 2. Carrier Threads & Non-Blocking I/O Unmounting

When a Virtual Thread executes a blocking operation (such as `Thread.sleep()`, HTTP requests, or database calls), the JVM **unmounts** the Virtual Thread from its underlying **Carrier OS Thread** (a worker in a `ForkJoinPool`).

-   The Carrier OS Thread is immediately freed up to process other Virtual Threads.

-   Once the OS signals I/O completion, the JVM remounts the Virtual Thread onto an available Carrier OS Thread to resume execution.


#### 3. Virtual Thread Pinning

When a Virtual Thread executes blocking I/O inside a `synchronized` block/method or native method (`JNI`), it cannot unmount from its Carrier Thread. This is known as **Thread Pinning**. In JDK 24/25, object monitors have been redesigned so that `synchronized` blocks no longer pin virtual threads in most standard scenarios.

### Chapter 2 Q&A Summary

#### Q1: What is Project Loom in Java?

**Answer:**

Project Loom is the OpenJDK initiative that redesigned Java’s concurrency model. It introduced **Virtual Threads** (finalized in Java 21) to enable high-throughput applications with light resource footprints, along with **Structured Concurrency** and **Scoped Values** (preview features) to simplify concurrent programming and lifecycle management.

#### Q2: Do Carrier OS Threads block during Virtual Thread I/O operations?

**Answer:**

No. During blocking I/O, the JDK's internal network and file libraries convert blocking calls into asynchronous OS notifications (e.g., `epoll` or `kqueue`). The JVM unmounts the Virtual Thread stack and returns the Carrier OS Thread to the pool so it can execute other tasks.

#### Q3: What are the 4 primary ways to create Virtual Threads in Java 21+ / Java 25?
[Chapter 2: Ways to create Virtual thread](WaysToCreateVirtualThread.md)

#### Q4: Why shouldn't you pool Virtual Threads like traditional Platform Threads?

**Answer:**

Platform threads are pooled because they are expensive to create and require heavy OS memory allocation. Virtual Threads are cheap and disposable (requiring only a few hundred bytes initially). Creating a new Virtual Thread per task simplifies architecture and eliminates the overhead of managing fixed pool sizes.

#### Q5: How do URI and URL differ when configuring modern HTTP clients in Java?

**Answer:**

Since Java 20, constructors like `new URL(...)` are deprecated due to parsing flaws and blocking DNS resolution during equality checks. The standard approach is to define endpoints using `URI.create("https://...")` and pass them to `java.net.http.HttpClient` or convert them via `uri.toURL()`.

