package com.example;

/**
 * Created by yu on 11/29/2015.
 */
public class Processor {
    public int id;
    public Process runningProcess;

    public Processor(int id) {
        this.id = id;
    }

    public void execute() {
        if (runningProcess == null) {
            return;
        }

        runningProcess.burstTime--;
        System.out.println("processor: " + id + " processing: " + runningProcess.id);
    }
}
