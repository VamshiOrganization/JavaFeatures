package com.vmc.java25.virtual.threads.interrupt;

public class TaskResponse {
    String name;
    String output;
    long time;

    public TaskResponse(String name, String output, long time) {
        this.name = name;
        this.output = output;
        this.time = time;
    }

    @Override
    public String toString() {
        return "TaskResponse{" +
                "name='" + name + '\'' +
                ", output='" + output + '\'' +
                ", time=" + time +
                '}';
    }
}
