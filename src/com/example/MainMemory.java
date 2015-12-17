package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yu on 11/29/2015.
 */
public class MainMemory {
    Map<Integer, Integer> freeMap = new HashMap<>();
    Map<Integer, Integer> usedMap = new HashMap<>();

    public MainMemory(int size) {
        for (int frame = 0; frame < size; frame++) {
            freeMap.put(frame, frame);
        }
    }

    public List<Integer> getFreeFrames(int size) {
        if (freeMap.size() >= size) {
            List<Integer> frames = new ArrayList<>();
            for (Integer frame : freeMap.keySet()) {
                if (size > 0) {
                    size--;
                    frames.add(frame);
                } else {
                    return frames;
                }
            }
        }
        return null; // not enough space.
    }

    public void allocate(List<Integer> frames) {
        for (int frame : frames) {
            freeMap.remove(frame);
            usedMap.put(frame, frame);
        }
    }

    public void deallocate(List<Integer> frames) {
        for (int frame : frames) {
            usedMap.remove(frame);
            freeMap.put(frame, frame);
        }
    }

}
