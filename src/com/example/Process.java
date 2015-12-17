package com.example;

import java.awt.*;
import java.util.List;

/**
 * Created by yu on 11/29/2015.
 */
public class Process implements Comparable<Process> {
    public String id;
    public int priority;
    public int burstTime;
    public int memoryUsage;
    public List<Integer> memoryTable;
    public double arrival;
    public Color color;

    public Process(String id, int priority, int burstTime, int memoryUsage, double arrival, Color color) {
        this.id = id;
        this.priority = priority;
        this.burstTime = burstTime;
        this.memoryUsage = memoryUsage;
        this.arrival = arrival;
        this.color = color;
    }

    @Override
    public int compareTo(Process p) {
        return priority - p.priority;
    }

}
