import models.ProcessExecution;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class GanttChart extends JPanel {
    private final List<ProcessExecution> schedule;  // List to hold process execution data
    private final String scheduleName;  // Name of the scheduling algorithm
    private final double averageWaitingTime;  // Average waiting time of the processes
    private final double averageTurnaroundTime;  // Average turnaround time of the processes

    // Constructor to initialize the Gantt chart with schedule, name, and average times
    public GanttChart(List<ProcessExecution> schedule, String scheduleName, double awt, double ata) {
        this.schedule = schedule;
        this.scheduleName = scheduleName;
        this.averageWaitingTime = awt;
        this.averageTurnaroundTime = ata;
        setBackground(Color.DARK_GRAY);  // Set the background color of the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Coordinates and spacing for drawing the chart
        int xOffset = 100;
        int yOffset = 80;
        int barHeight = 30;
        int barSpacing = 40;
        int timeUnitWidth = 40;

        // Draw the title at the top of the chart
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.drawString("CPU Scheduling Graph", xOffset, 40);

        // Calculate the maximum time unit needed to draw the chart
        int maxTime = 0;
        for (ProcessExecution exec : schedule) {
            maxTime = Math.max(maxTime, exec.startTime + exec.duration);
        }

        // Extract unique process names for creating the rows
        Set<String> uniqueProcesses = new HashSet<>();
        for (ProcessExecution exec : schedule) {
            uniqueProcesses.add(exec.processName);
        }
        int processCount = uniqueProcesses.size();

        // Adjust the panel size based on the number of processes and execution time
        int panelWidth = xOffset + (maxTime + 1) * timeUnitWidth + 100;
        int panelHeight = yOffset + processCount * barSpacing + 200;
        setPreferredSize(new Dimension(panelWidth, panelHeight));

        // Draw gridlines and time labels for the x-axis
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        // Draw vertical gridlines for each time unit
        for (int i = 0; i <= maxTime; i++) {
            int xPosition = xOffset + i * timeUnitWidth;

            // Draw the gridline
            g2d.drawLine(xPosition, yOffset - 20, xPosition, yOffset + processCount * barSpacing + 20);

            // Draw the time labels (e.g., 0, 1, 2, etc.) below the gridline
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(i), xPosition - 5, yOffset + processCount * barSpacing + 30);
        }

        // Draw the process bars and corresponding labels
        int processIndex = 0;
        for (String processName : uniqueProcesses) {
            for (ProcessExecution exec : schedule) {
                if (exec.processName.equals(processName)) {
                    int barStartX = xOffset + exec.startTime * timeUnitWidth;  // X position of the bar
                    int yPosition = yOffset + processIndex * barSpacing;  // Y position for the row

                    // Draw a rounded process bar with the color specific to the process
                    g2d.setColor(exec.color);
                    g2d.fill(new RoundRectangle2D.Double(barStartX, yPosition, exec.duration * timeUnitWidth, barHeight, 10, 10));

                    // Draw the process name inside the bar
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(exec.processName, barStartX + 5, yPosition + barHeight / 2 + 5);
                }
            }

            // Draw the process name label to the left of the bar
            g2d.setColor(Color.WHITE);
            g2d.drawString("Process: " + processName, xOffset - 90, yOffset + processIndex * barSpacing + barHeight / 2 + 5);

            processIndex++;  // Move to the next row for the next process
        }
    }

    // Create and display the GUI for the Gantt chart
    public static void createAndShowGUI(List<ProcessExecution> schedule, String scheduleName, double awt, double ata) {
        JFrame frame = new JFrame("Scheduling Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the GanttChart panel with the schedule, name, and average times
        GanttChart gridPanel = new GanttChart(schedule, scheduleName, awt, ata);
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setPreferredSize(new Dimension(1200, 800));  // Set scrollable panel size

        // Create the main panel to hold the Gantt chart and other components
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);  // Add the chart to the center

        // Create and add statistics and legend panels
        JPanel statsPanel = createStatsPanel(scheduleName, awt, ata);
        JPanel legendPanel = createLegendPanel(schedule);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        mainPanel.add(legendPanel, BorderLayout.EAST);

        // Set up the frame and make it visible
        frame.add(mainPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    // Create a panel to display scheduling statistics like average waiting and turnaround times
    private static JPanel createStatsPanel(String scheduleName, double awt, double ata) {
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.DARK_GRAY);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add the statistics label and details
        JLabel statsLabel = new JLabel("Statistics");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statsLabel.setForeground(Color.RED);
        statsPanel.add(statsLabel);

        statsPanel.add(createLabel("Schedule Name: " + scheduleName));
        statsPanel.add(createLabel("Average Waiting Time: " + String.format("%.2f", awt)));
        statsPanel.add(createLabel("Average Turnaround Time: " + String.format("%.2f", ata)));

        return statsPanel;
    }

    // Create a panel to display information about each process in the scheduling
    private static JPanel createLegendPanel(List<ProcessExecution> schedule) {
        JPanel legendPanel = new JPanel(new BorderLayout());
        legendPanel.setBackground(Color.DARK_GRAY);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // Title for the legend panel
        JLabel titleLabel = new JLabel("Processes Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.RED);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        legendPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel to hold process info like color, name, PID, and priority
        JPanel processListPanel = new JPanel();
        processListPanel.setBackground(Color.DARK_GRAY);
        processListPanel.setLayout(new BoxLayout(processListPanel, BoxLayout.Y_AXIS));

        Set<String> uniqueProcesses = new HashSet<>();
        for (ProcessExecution exec : schedule) {
            if (uniqueProcesses.add(exec.processName)) {
                // Create a panel for each process information
                JPanel processInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                processInfoPanel.setBackground(Color.DARK_GRAY);

                // Color box representing the process color
                JLabel colorBox = new JLabel();
                colorBox.setOpaque(true);
                colorBox.setBackground(exec.color);
                colorBox.setPreferredSize(new Dimension(20, 20));

                // Process name, PID, and priority display
                JLabel processLabel = new JLabel(String.format(" Name: %s | PID: %s | Priority: %s",
                        exec.processName, exec.pid, exec.priority));
                processLabel.setFont(new Font("Arial", Font.BOLD, 16));
                processLabel.setForeground(Color.WHITE);

                processInfoPanel.add(colorBox);
                processInfoPanel.add(processLabel);
                processListPanel.add(processInfoPanel);
            }
        }

        // Add the list of processes to the legend panel
        legendPanel.add(new JScrollPane(processListPanel), BorderLayout.CENTER);
        return legendPanel;
    }

    // Helper method to create labeled text for stats and other sections
    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }
}
