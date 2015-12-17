package com.example;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OS {
    private int PRODUCTION_MIN = 1;
    private int PRODUCTION_MAX = 3;
    private int BURST_TIME_MIN = 1;
    private int BURST_TIME_MAX = 5;
    private int MEMORY_MIN     = 1;
    private int MEMORY_MAX     = 100;
    private int PRIORITY_MIN   = 0;
    private int PRIORITY_MAX   = 100;

    Queue<Process> mReadyQueue = new PriorityQueue<>(new PriorityComparator());
    List<Processor> mProcessors = initProcessors(4);
    MainMemory mMainMemory = new MainMemory(1024); // MB
    long cycle = 0;

    public void boot() {
        Thread loop = new Thread() {
            public void run() {
                loop();
            }
        };
        loop.start();
    }

    private void loop() {
        double lastCycle = System.nanoTime();
        GUI gui = new GUI(this);
        boolean isRunning = true;
        while (isRunning) {
            if (gui.isPaused && !gui.isStep) {
                Thread.yield();
                try { Thread.sleep(50); } catch (Exception e) { }
                continue;
            }
            gui.isStep = false;

            double now = System.nanoTime();

            if (now - lastCycle > gui.oneCycle) {
                System.out.println("cycle: " + cycle);
                mReadyQueue.addAll(generateReadyProcesses());
                printReadyQueue();
                mReadyQueue = dispatchToIdleProcessors(mReadyQueue);
                gui.update(this);
                executeProcessors();
                mReadyQueue = ageProcesses(mReadyQueue);
                lastCycle = System.nanoTime();
                cycle++;
            }

            Thread.yield();
            try { Thread.sleep(200); } catch (Exception e) { }
        }
    }

    private void executeProcessors() {
        for (Processor processor : mProcessors) {
            if (processor.runningProcess == null) {
                continue;
            }
            processor.execute();

            if (processor.runningProcess.burstTime == 0) {
                mMainMemory.deallocate(processor.runningProcess.memoryTable);
                processor.runningProcess = null;
            }
        }
    }

    private Queue<Process> dispatchToIdleProcessors(Queue<Process> processes) {
        for (Processor processor : mProcessors) {
            // processor not idle
            if (processor.runningProcess != null) {
                continue;
            }

            for (Process processToDispatch : processes) {
                if (processToDispatch.memoryTable == null) {
                    List<Integer> freeFrames = mMainMemory.getFreeFrames(processToDispatch.memoryUsage);
                    mMainMemory.allocate(freeFrames);
                    processToDispatch.memoryTable = freeFrames;
                }

                if (processToDispatch.memoryTable != null) {
                    processes.remove(processToDispatch);
                    processor.runningProcess = processToDispatch;
                    break;
                }

                System.out.println("not enough space for process.");
            }
        }

        return processes;
    }

    private Queue<Process> allocateReadyProcesses(Queue<Process> readyQueue) {
        Queue<Process> queue = new PriorityQueue<>(readyQueue);

        for (Process process : readyQueue) {
            if (process.memoryTable != null) {
                continue;
            }
            
            List<Integer> freeFrames = mMainMemory.getFreeFrames(process.memoryUsage);

            if (freeFrames != null) {
                mMainMemory.allocate(freeFrames);
                process.memoryTable = freeFrames;
                queue.add(process);
                break;
            }

            System.out.println("not enough space for process.");
        }

        return queue;
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
        if (mReadyQueue.size() >= 10) {
            return new PriorityQueue<>();
        }

        Queue<Process> processes = new PriorityQueue<>();
        int count = random(PRODUCTION_MIN, PRODUCTION_MAX);
        for (int i = 0; i < count; i++) {
            int priority = random(PRIORITY_MIN, PRIORITY_MAX);
            int burstTime = random(BURST_TIME_MIN, BURST_TIME_MAX);
            int memoryUsage = random(MEMORY_MIN, MEMORY_MAX);
            Color color = new Color(random(0, 255), random(0, 255), random(0, 255));
            Process process = new Process(generateID(), priority, burstTime, memoryUsage, cycle, color);
            processes.add(process);
        }
        return processes;
    }

    private void printReadyQueue() {
        System.out.println("ready queue:");
        for (Process process : mReadyQueue) {
            System.out.println("pid: " + process.id + ", p: " + process.priority + ", t: " + process.burstTime + ", m: " + process.memoryUsage);
        }
    }

    private class PriorityComparator implements Comparator<Process> {

        @Override
        public int compare(Process p1, Process p2) {
            return p1.priority - p2.priority;
        }

    }

    private Queue<Process> ageProcesses(Queue<Process> processes) {
        Queue<Process> agedProcesses = new PriorityQueue<>();
        for (Process process : processes) {
            if (process.priority > 0) {
                process.priority--;
            }
            agedProcesses.add(process);
        }
        return agedProcesses;
    }

    public static void main(String[] args) {
        OS os = new OS();
        os.boot();
    }
}
