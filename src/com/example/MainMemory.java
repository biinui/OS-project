package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yu on 11/29/2015.
 */
public class MainMemory {
    private double memorySize;
    private int frameSize = 4; // MB
    public List<Boolean> frameTable = new ArrayList<>();

    public MainMemory(int size) {
        memorySize = size;
        long frames = size / frameSize;
        for (int frame = 0; frame < frames; frame++) {
            frameTable.add(frame, true);
        }
    }

    public List<Integer> getFreeFrames(int size) {
        List<Integer> frames = new ArrayList<>();
        int framesNeeded = size / frameSize;
        System.out.println("frames needed: " + framesNeeded);
        for (int frame = 0; frame < frameTable.size(); frame++) {
            boolean isFree = frameTable.get(frame);
            if (isFree) {
                framesNeeded--;
                frames.add(frame);

                if (framesNeeded == 0) {
                    return frames;
                }
            }
        }
        return null; // not enough space.
    }

    public void allocate(List<Integer> frames) {
        for (int frame : frames) {
            frameTable.set(frame, false);
        }
    }

    public void deallocate(List<Integer> frames) {
        for (int frame : frames) {
            frameTable.set(frame, true);
        }
    }

}
