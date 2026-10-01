
# Chapter 1: Java Execution, JVM Memory Architecture & Native Thread Limits

### Q1: Can I compile and run a Java 25 program using a Java 21 JDK in IntelliJ IDEA or Maven?

**Answer:**

No. Running Java 25 bytecode (class file version `69.0`) on a Java 21 JDK (version `65.0`) results in an `UnsupportedClassVersionError` or `invalid source release: 25` compiler error. The `javac` compiler and JVM runtime of Java 21 have no knowledge of newer Java 25 class file formats or language features. You must install and use a Java 25 JDK.

### Q2: What does the `-Xss` VM option configure in Java, and how is it allocated?

**Answer:**

The `-Xss` flag (e.g., `-Xss1m` or `-Xss6m`) configures the maximum **thread stack size** allocated per thread.

-   It allocates **native off-heap memory** from the operating system, not memory inside the Java Heap (`-Xmx`).

-   The OS allocates this memory lazily: setting `-Xss6m` reserves a 6 MB virtual memory address range per thread, but physical RAM is only committed as stack depth grows during execution.


### Q3: Why does `Runtime.getRuntime().totalMemory() - freeMemory()` remain unchanged even when `-Xss` is raised or thread counts increase?

**Answer:**

`Runtime.getRuntime()` tracks **Java Heap Memory** only (objects created via `new`). Because thread stacks, local primitive variables, and execution call frames reside in **Native OS Memory (Off-Heap)**, changes to `-Xss` or thread stack allocations will never reflect in Java Heap memory metrics.

### Q4: What is the difference between Reserved Memory and Committed Memory in JVM Native Memory Tracking (NMT)?

**Answer:**

-   **Reserved Memory:** The total **virtual address space** the OS has set aside for the process (e.g., $N \text{ threads} \times \text{-Xss value}$). It costs almost zero physical RAM.

-   **Committed Memory:** The actual **physical RAM (Resident Set Size / RSS)** currently backed by hardware memory for active stack frames and heap objects.


### Q5: How do you convert NMT memory readings from Kilobytes (KB) to Megabytes (MB)?

**Answer:**

Divide the KB value by **1,024** (the binary IEC standard used by the JVM):

$$\text{MB} = \frac{\text{KB}}{1024}$$

-   _Example:_ $\text{Thread (reserved}=16,591,501\text{ KB}, \text{committed}=188,426\text{ KB})$

    -   **Reserved:** $16,591,501 \div 1024 \approx \mathbf{16,202.64\text{ MB} \; (\sim 16.2\text{ GB})}$

    -   **Committed:** $188,426 \div 1024 \approx \mathbf{183.98\text{ MB}}$
### Q6: Why didn't running 100,000 threads with `-Xss6m` cause an `OutOfMemoryError` on a local machine?

**Answer:** Because of **thread lifecycle recycling** and **lazy OS physical memory allocation**:

1.  Early threads in the loop were finishing their execution and terminating, releasing their native stack space back to the OS. The active concurrent thread count reached equilibrium (e.g., ~2,000–3,000 active threads at any given moment).

2.  The threads had shallow stack depth (mostly sleeping), so committed physical RAM remained small (~180 MB), while the 16 GB figure was merely virtual reserved space.


### Q7: How do you force a native thread `OutOfMemoryError` at the Operating System level?

**Answer:** By constraining the OS user process limit using `ulimit -u <limit>`. When Java attempts to spawn new OS-level platform threads beyond this quota, the OS `pthread_create` call fails with `EAGAIN` (Resource temporarily unavailable), causing the JVM to throw:
```
[warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN)
java.lang.OutOfMemoryError: unable to create new native thread
```