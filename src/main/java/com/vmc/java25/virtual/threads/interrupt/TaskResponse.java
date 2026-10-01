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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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
