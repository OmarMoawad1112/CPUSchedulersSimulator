package models;

import java.awt.*;

// Represents a process to be executed in a system, with associated attributes like name, duration, color, PID, priority, and start time.
public class ProcessExecution {
    // Name of the process (e.g., "Process1").
    public String processName;

    // Duration for which the process runs, in some unit of time.
    public int duration;

    // Visual color associated with the process (useful for UI representation).
    public Color color;

    // Unique process identifier (PID) for the process.
    public int pid;

    // Priority level of the process (lower numbers could represent higher priority, depending on the system).
    public int priority;

    // Time when the process starts execution (in system-specific time units).
    public int startTime;

    // Constructor to initialize a ProcessExecution object with all required properties.
    public ProcessExecution(String processName, int duration, Color color, int pid, int priority, int startTime) {
        this.processName = processName; // Sets the name of the process.
        this.duration = duration;      // Sets the execution duration of the process.
        this.color = color;            // Sets the color representing the process.
        this.pid = pid;                // Assigns a unique process ID.
        this.priority = priority;      // Assigns a priority level to the process.
        this.startTime = startTime;    // Sets the start time for execution.
    }

    // Returns the name of the process.
    public String getProcessName() {
        return processName;
    }
}
