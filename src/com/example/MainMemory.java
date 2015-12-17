package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yu on 11/29/2015.
 */
public class MainMemory {
    public List<Boolean> frameTable = new ArrayList<>();

    public MainMemory(int size) {
        for (int frame = 0; frame < size; frame++) {
            frameTable.add(frame, true);
        }
    }

    public List<Integer> getFreeFrames(int size) {
        List<Integer> frames = new ArrayList<>();
        for (int frame = 0; frame < frameTable.size(); frame++) {
            boolean isFree = frameTable.get(frame);
            if (isFree) {
                size--;
                frames.add(frame);

                if (size == 0) {
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
