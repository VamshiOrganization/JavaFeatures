package com.vmc.java25.virtual.threads.interrupt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

public class CustomAverageWhetherReport extends StructuredTaskScope<TaskResponse> {

    private final List<Subtask<? extends TaskResponse>> successSubtaskList
            = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void handleComplete(Subtask<? extends TaskResponse> subtask) {
        if (subtask.state() == Subtask.State.SUCCESS) {
            addSubTask(subtask);
        }
    }

    private void addSubTask(Subtask<? extends TaskResponse> subtask) {
        int numSuccessful = 0;
        synchronized (successSubtaskList) {
            successSubtaskList.add(subtask);
            numSuccessful = successSubtaskList.size();
        }
        if (numSuccessful == 2)
            this.shutdown();

    }


    public CustomAverageWhetherReport join() throws InterruptedException {
        super.join();
        return this;
    }

    public TaskResponse response() {
        super.ensureOwnerAndJoined();
        if (successSubtaskList.size() != 2) {
            throw new RuntimeException("Atleast 2 tasks must be succesfull");
        }
        TaskResponse r1 = successSubtaskList.get(0).get();
        TaskResponse r2 = successSubtaskList.get(1).get();
        Integer res1 = Integer.parseInt(r1.getOutput());
        Integer res2 = Integer.parseInt(r2.getOutput());

        return new TaskResponse("Whether", (res1 + res2) / 2 + "", r1.getTime() + r2.getTime() / 2);
    }
}
