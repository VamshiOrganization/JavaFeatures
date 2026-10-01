package com.vmc.java25.virtual.threads.oom;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

public class NativeMemoryChecker {
    public static void printThreadMemory() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName diagnosticBean = new ObjectName("com.sun.management:type=DiagnosticCommand");

            // Runs 'jcmd <pid> VM.native_memory summary' programmatically
            String result = (String) mbs.invoke(
                    diagnosticBean,
                    "vmNativeMemory",
                    new Object[] { new String[] { "summary" } },
                    new String[] { "[Ljava.lang.String;" }
            );

            // Print only the Thread memory section
            for (String line : result.split("\n")) {
                if (line.contains("Thread (reserved=")) {
                    System.out.println(">>> NMT " + line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}