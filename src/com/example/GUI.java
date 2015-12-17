package com.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by yu on 12/17/2015.
 */
public class GUI extends JFrame {
    JPanel mainPanel = new JPanel();
    JPanel cpuPanel = new JPanel();
    List<CorePanel> corePanels = new ArrayList<>();
    QueueScrollPane queueScrollPane;
    MemoryPanel memoryPanel;
    ControlPanel controlPanel;
    public boolean isPaused = true;
    public boolean isStep = false;
    double oneCycle = 1000000000; // 1 second
    boolean fast = false;

    GUI(OS os) {
        super("OS");
        setBounds(0, 0, 1000, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.add(mainPanel);
        mainPanel.setLayout(null);

        cpuPanel.setLayout(null);
        cpuPanel.setLocation(0, 0);
        cpuPanel.setSize(200, 200);
        mainPanel.add(cpuPanel);

        initCPUs();
        queueScrollPane = new QueueScrollPane();
        memoryPanel = new MemoryPanel();
        controlPanel = new ControlPanel();

        mainPanel.add(queueScrollPane);
        mainPanel.add(memoryPanel);
        mainPanel.add(controlPanel);

        setVisible(true);
    }

    private void initCPUs() {
        for (int processor = 0; processor < 4; processor++) {
            CorePanel corePanel = new CorePanel(processor);
            corePanels.add(corePanel);
            cpuPanel.add(corePanel);
        }
    }

    private void updateCPUs(List<Processor> processors) {
        for (Processor processor : processors) {
            Process process = processor.runningProcess;
            CorePanel corePanel = corePanels.get(processor.id);
            String id = "xxxxxxxx";
            String time = "x";
            Color color = Color.black;
            if (process != null) {
                id = process.id;
                time = String.valueOf(process.burstTime);
                color = process.color;
            }
            corePanel.update(id, time, color);
            corePanel.updateUI();
        }
    }

    public void update(OS os) {
        new Thread(() -> {
           SwingUtilities.invokeLater(() -> {
               updateCPUs(os.mProcessors);
               queueScrollPane.update(os.mReadyQueue);
//               memoryPanel.update(os.mProcessors);
               List<Process> processes = new ArrayList<Process>(os.mReadyQueue);
               for (Processor processor : os.mProcessors) {
                   Process process = processor.runningProcess;
                   if (process == null) {
                       continue;
                   }
                   processes.add(process);
               }
               memoryPanel.updateProcesses(processes);
           });
        }).start();
    }

    private class CorePanel extends JPanel {
        public JLabel cpuId;
        public JLabel processId;
        public JLabel processTime;

        public CorePanel(int id) {
            setLocation(0, id * 50);
            setSize(200, 50);

            cpuId = new JLabel(String.valueOf(id));
            cpuId.setLocation(0, 0);
            cpuId.setSize(50, 50);

            add(cpuId);

            processId = new JLabel("xxxxxxxx");
            processId.setLocation(50, 0);
            processId.setSize(100, 50);

            add(processId);

            processTime = new JLabel("x");
            processTime.setLocation(100, 0);
            processTime.setSize(50, 50);

            add(processTime);
        }

        public void update(String pid, String time, Color color) {
            processId.setText(pid);
            processTime.setText(time);
            Color contrast = getContrastColor(color);
            cpuId.setForeground(contrast);
            processId.setForeground(contrast);
            processTime.setForeground(contrast);
            setBackground(color);
        }
    }

    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    private class QueueScrollPane extends JScrollPane {
        JPanel queuePanel = new JPanel();

        public QueueScrollPane() {
            setLocation(250, 0);
            setSize(800, 200);
            queuePanel.setSize(800, 200);
            queuePanel.setLayout(null);
            setViewportView(queuePanel);
        }

        public void update(Queue<Process> readyQueue) {
            Queue<Process> queue = new PriorityQueue<>(readyQueue);
            int length = queue.size();
            int width = length * 75;
            queuePanel.setSize(width, 200);
            queuePanel.removeAll();
            int index = 0;
            while (queue.peek() != null) {
                Process process = queue.poll();
                ProcessPanel processPanel = new ProcessPanel(index, process);
                Color contrast = getContrastColor(process.color);
                processPanel.setBackground(process.color);
                processPanel.pPriority.setForeground(contrast);
                processPanel.pId.setForeground(contrast);
                processPanel.pTime.setForeground(contrast);
                processPanel.pMemory.setForeground(contrast);
                queuePanel.add(processPanel);
                queuePanel.updateUI();
                index++;
            }
            queuePanel.revalidate();
        }
    }

    private class ProcessPanel extends JPanel {
        public JLabel pPriority;
        public JLabel pId;
        public JLabel pTime;
        public JLabel pMemory;

        public ProcessPanel(int index, Process process) {
            setLayout(null);
            setBorder(LineBorder.createBlackLineBorder());
            setLocation(index * 75, 0);
            setSize(75, 200);

            pPriority = new JLabel(String.valueOf(process.priority));
            pPriority.setLocation(0, 0);
            pPriority.setSize(75, 50);
            add(pPriority);

            pId = new JLabel(process.id);
            pId.setLocation(0, 50);
            pId.setSize(75, 50);
            add(pId);

            pTime = new JLabel(String.valueOf(process.burstTime));
            pTime.setLocation(0, 100);
            pTime.setSize(75, 50);
            add(pTime);

            pMemory = new JLabel(String.valueOf(process.memoryUsage));
            pMemory.setLocation(0, 150);
            pMemory.setSize(75, 50);
            add(pMemory);
        }
    }

    private class MemoryPanel extends JPanel {
        int size = 1024;
        List<JPanel> memoryFrames = new ArrayList<>();

        public MemoryPanel() {
            setLayout(null);
            setBorder(LineBorder.createBlackLineBorder());
            setBounds(0, 200, 1000, 420);

            for (int frame = 0; frame < size; frame++) {
                JPanel panel = new JPanel();
                panel.setLayout(null);
                panel.setBackground(Color.lightGray);
                panel.setSize(20, 20);
                panel.setLocation((frame % 50) * 20, (frame / 50) * 20);
                memoryFrames.add(frame, panel);
                add(panel);
                panel.updateUI();
            }
        }

        public void update(List<Processor> processors) {
            for (int frame = 0; frame < size; frame++) {
                JPanel panel = memoryFrames.get(frame);
                panel.setBackground(Color.lightGray);
                panel.updateUI();
                memoryFrames.set(frame, panel);
            }

            for (Processor processor : processors) {
                Process process = processor.runningProcess;

                if (process == null) {
                    continue;
                }

                List<Integer> frames = process.memoryTable;
                Color color = colors.get(processor.id);
                for (int frame : frames) {
                    JPanel panel = memoryFrames.get(frame);
                    panel.setBackground(color);
                    memoryFrames.set(frame, panel);
                    panel.updateUI();
                }
            }

        }

        public void updateProcesses(List<Process> processes) {
            for (int frame = 0; frame < size; frame++) {
                JPanel panel = memoryFrames.get(frame);
                panel.setBackground(Color.lightGray);
                panel.updateUI();
                memoryFrames.set(frame, panel);
            }

            for (Process process : processes) {
                if (process == null || process.memoryTable == null) {
                    continue;
                }

                List<Integer> frames = process.memoryTable;
                Color color = process.color;
                for (int frame : frames) {
                    JPanel panel = memoryFrames.get(frame);
                    panel.setBackground(color);
                    memoryFrames.set(frame, panel);
                    panel.updateUI();
                }
            }

        }

    }

    private class ControlPanel extends JPanel {
        RangePanel production;
        RangePanel time;
        RangePanel memory;
        RangePanel priority;
        JButton pause;
        JButton play;
        JButton fastForward;
        JButton step;

        public ControlPanel() {
            setLayout(null);
            setBounds(0, 620, 400, 200);

            initRangePanels();
            initButtons();
        }

        private void initButtons() {
            pause = new JButton("||");
            pause.setSize(50, 50);
            pause.setLocation(200, 0);
            play = new JButton(">");
            play.setSize(50, 50);
            play.setLocation(250, 0);
            fastForward = new JButton(">>");
            fastForward.setSize(50, 50);
            fastForward.setLocation(300, 0);
            step = new JButton("|>");
            step.setSize(50, 50);
            step.setLocation(350, 0);

            pause.addActionListener(e -> {
                isPaused = true;
            });
            play.addActionListener(e -> {
                isPaused = false;
            });
            fastForward.addActionListener(e -> {
                fast = !fast;
                oneCycle = fast ? oneCycle / 2 : oneCycle * 2;
            });
            step.addActionListener(e -> {
                isStep = true;
            });

            add(pause);
            add(play);
            add(fastForward);
            add(step);
        }

        private void initRangePanels() {
            production = new RangePanel("Production");
            production.setLocation(0, 0);
            production.min.setText("");
            production.max.setText("");
            add(production);
            time = new RangePanel("Burst Time");
            time.setLocation(0, 50);
            time.min.setText("");
            time.max.setText("");
            add(time);
            memory = new RangePanel("Memory Usage");
            memory.setLocation(0, 100);
            memory.min.setText("");
            memory.max.setText("");
            add(memory);
            priority = new RangePanel("Priority");
            priority.setLocation(0, 150);
            priority.min.setText("");
            priority.max.setText("");
            add(priority);
        }

    }

    private class RangePanel extends JPanel {
        JLabel label;
        JTextField min = new JTextField("0");
        JTextField max = new JTextField("1");

        public RangePanel(String labelStr) {
            setLayout(null);
            setSize(200, 50);
            label = new JLabel(labelStr);
            label.setLocation(0, 0);
            label.setSize(100, 50);
            add(label);

            min.setLocation(100, 0);
            min.setSize(50, 50);
            add(min);

            max.setLocation(150, 0);
            max.setSize(50, 50);
            add(max);
        }
    }

    List<Color> colors = Arrays.asList(Color.cyan, Color.magenta, Color.green, Color.pink);
}
