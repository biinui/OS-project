package com.example;

import java.util.List;

/**
 * Created by yu on 11/29/2015.
 */
public class MainMemory {
    public List<Range> freeMemory;
    public List<Range> usedMemory;

    public MainMemory(int size) {

    }

    public double allocate(Process process) {
        return 1;
    }

    private class Range {
        public int index;
        public int size;
    }
}
