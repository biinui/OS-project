package com.example;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OS {

    public void boot() {
        Thread loop = new Thread() {
            public void run() {
                loop();
            }
        };
        loop.start();
    }

    private void loop() {
        double oneCycle = 1000000000; // 1 second
        double lastCycle = System.nanoTime();
        boolean isRunning = true;
        Queue<Process> mReadyQueue = new PriorityQueue<>(new BurstTimeComparator());
        List<Processor> mProcessors = initProcessors(2);
        while (isRunning) {
            double now = System.nanoTime();

            if (now - lastCycle > oneCycle) {
                mReadyQueue.addAll(generateReadyProcesses());

                for (Processor processor : mProcessors) {
                    if (processor.runningProcess == null) {
                        processor.runningProcess = mReadyQueue.poll();
                    }
                    processor.execute();
                }

                lastCycle = System.nanoTime();
            }

            Thread.yield();
            try { Thread.sleep(250); } catch (Exception e) { }
        }
    }

    private List<Processor> initProcessors(int count) {
        List<Processor> processors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Processor processor = new Processor(i);
            processors.add(processor);
        }
        return processors;
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private String generateID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Queue<Process> generateReadyProcesses() {
        Queue<Process> processes = new PriorityQueue<>();
        int count = random(0, 1);
        for (int i = 0; i < count; i++) {
            int burstTime = random(1, 5);
            int memoryUsage = random(100, 250);
            Process process = new Process(generateID(), burstTime, memoryUsage);
            System.out.println("ready process: " + process.id + ", burstTime: " + process.burstTime + ", memoryUsage: " + process.memoryUsage);
            processes.add(process);
        }
        return processes;
    }

    private class BurstTimeComparator implements Comparator<Process> {

        @Override
        public int compare(Process p1, Process p2) {
            return p1.burstTime - p2.burstTime;
        }

    }

    public static void main(String[] args) {
        OS os = new OS();
        os.boot();
    }
}
