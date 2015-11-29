package com.example;

/**
 * Created by yu on 11/29/2015.
 */
public class Process implements Comparable<Process> {
    public String id;
    public int burstTime;
    public int memoryUsage;

    public Process(String id, int burstTime, int memoryUsage) {
        this.id = id;
        this.burstTime = burstTime;
        this.memoryUsage = memoryUsage;
    }

    @Override
    public int compareTo(Process p) {
        return burstTime - p.burstTime;
    }
}
