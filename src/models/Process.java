package models;

import java.awt.Color;

// Represents a process in the scheduling system with various attributes and methods for calculation and manipulation
public class Process {
    // Common Attributes for all schedulers
    private String name;             // Name of the process
    private int arrivalTime;         // Time the process arrives in the ready queue
    private int burstTime;           // Total CPU burst time required by the process
    private int priority;            // Priority level of the process (lower value = higher priority)
    private String color;            // Visual representation (e.g., for Gantt charts)
    private int pid;                 // Unique Process ID
    private int completionTime;      // Time the process finishes execution
    private int originalBurstTime = 0; // To store the initial burst time for specific algorithms

    // FCAI-Specific Attributes
    private double fcaiFactor;       // FCAI factor used to determine scheduling order
    private int updatedQuantum;      // Dynamic quantum used for FCAI scheduling

    // Constructor to initialize the process with common attributes
    public Process(String name, int arrivalTime, int burstTime, int priority, String color, int pid) {
        this.name = name;             // Process name
        this.arrivalTime = arrivalTime; // Arrival time in the ready queue
        this.burstTime = burstTime;    // CPU burst time required
        this.priority = priority;      // Priority level
        this.color = color;            // Color representation (for UI visualization)
        this.pid = pid;                // Unique process ID
        this.originalBurstTime = burstTime; // Initial burst time for algorithms
    }

    // Getters and Setters for Common Attributes
    public String getName() {
        return name; // Returns the name of the process
    }

    public int getArrivalTime() {
        return arrivalTime; // Returns the arrival time of the process
    }

    public int getBurstTime() {
        return burstTime; // Returns the remaining burst time
    }

    public void setBurstTime(int burstTime) {
        // Sets the remaining burst time, useful after partial execution
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority; // Returns the priority level of the process
    }

    public Color getColor() {
        return Color.decode(color); // Converts the hex color string to a Color object
    }

    public int getPid() {
        return pid; // Returns the process ID
    }

    public int getCompletionTime() {
        return completionTime; // Returns the time when the process finishes
    }

    public void setCompletionTime(int completionTime) {
        // Sets the completion time of the process once it's done
        this.completionTime = completionTime;
    }

    public int getOriginalBurstTime() {
        return originalBurstTime; // Returns the initial burst time (used in algorithms)
    }

    public void setOriginalBurstTime(int originalBurstTime) {
        // Sets the initial burst time, used in algorithms like SRTF
        this.originalBurstTime = originalBurstTime;
    }

    // Common Calculations for Schedulers
    public int getWaitingTime(int completionTime) {
        // Calculates waiting time = Completion Time - Arrival Time - Burst Time
        return completionTime - arrivalTime - burstTime;
    }

    public int getTurnaroundTime(int completionTime) {
        // Calculates turnaround time = Completion Time - Arrival Time
        return completionTime - arrivalTime;
    }

    public int getWaitingTimeSRTF(int completionTime) {
        // SRTF-Specific waiting time = Completion Time - Arrival Time - Original Burst Time
        return completionTime - arrivalTime - originalBurstTime;
    }

    // FCAI-Specific Methods
    public double getFcaiFactor() {
        return fcaiFactor; // Returns the FCAI factor for scheduling order
    }

    public void setFcaiFactor(double fcaiFactor) {
        // Updates the FCAI factor used for dynamic scheduling
        this.fcaiFactor = fcaiFactor;
    }

    public int getUpdatedQuantum() {
        return updatedQuantum; // Returns the dynamically updated quantum for scheduling
    }

    public void setUpdatedQuantum(int updatedQuantum) {
        // Sets the dynamic quantum value used in FCAI scheduling
        this.updatedQuantum = updatedQuantum;
    }

    // Calculates the FCAI factor used in the scheduling algorithm
    public void calculateFcaiFactor(double v1, double v2) {
        // v1 and v2 are scaling factors for arrival time and burst time
        this.fcaiFactor = (10 - priority) + Math.ceil(arrivalTime / v1) + Math.ceil(burstTime / v2);
    }

    // Gets the waiting time for FCAI scheduling based on burst time
    public int getWaitingTimeFcai(int bursTime) {
        return getTurnaroundTime(this.completionTime) - originalBurstTime;
    }

    // Provides a string representation of the process (for debugging/visualization)
    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                ", priority=" + priority +
                ", color='" + color + '\'' +
                ", pid=" + pid +
                ", completionTime=" + completionTime +
                ", originalBurstTime=" + originalBurstTime +
                ", fcaiFactor=" + fcaiFactor +
                ", updatedQuantum=" + updatedQuantum +
                '}';
    }
}
