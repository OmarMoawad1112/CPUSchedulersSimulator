package algorithms;

import models.Process;
import models.ProcessExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

// Implements Shortest Remaining Time First (SRTF) Scheduling Algorithm
public class ShortestRemainingTimeFirstScheduler {
    public static int MAX_WAIT_TIME = 20;  // Maximum wait time for processes before being considered starved

    // List to store the order of process execution
    public List<Process> executionOrder2 = new ArrayList<>();

    // Schedules processes based on Shortest Remaining Time First (SRTF) algorithm
    public List<ProcessExecution> schedule(List<Process> processes, int contextSwitchingTime) {
        // Sort processes by arrival time initially to handle them in the order they arrive
        processes.sort((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()));

        // Priority queue to hold processes, sorted by burst time (shortest first)
        PriorityQueue<Process> queue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.getBurstTime(), p2.getBurstTime()));
        List<ProcessExecution> executionOrder = new ArrayList<>();

        int currentTime = 0;  // Current time in the simulation
        int completed = 0;    // Count of completed processes
        Process lastProcess = null; // To track the last executed process (for context switching)

        // Run the scheduler until all processes are completed
        while (completed < processes.size()) {
            // Check for processes that have arrived and add them to the queue
            for (Process process : processes) {
                if (!queue.contains(process) && process.getArrivalTime() <= currentTime && process.getBurstTime() > 0) {
                    // Set original burst time if not set yet (for SRTF)
                    if (process.getOriginalBurstTime() == 0) {
                        process.setOriginalBurstTime(process.getBurstTime());
                    }
                    queue.add(process);
                }

                // Handle starved processes (waiting too long)
                if (process.getWaitingTime(currentTime) > MAX_WAIT_TIME && process.getBurstTime() > 0) {
                    System.out.println("Process " + process.getName() + " starved! Executing immediately.");
                    currentTime += contextSwitchingTime;  // Add context switch time
                    currentTime += process.getBurstTime(); // Process completes its execution
                    process.setBurstTime(0);               // Mark the process as completed
                    process.setCompletionTime(currentTime); // Set completion time
                    executionOrder.add(new ProcessExecution(process.getName(), 1, process.getColor(), process.getPid(), process.getPriority(), currentTime));
                    executionOrder2.add(process);
                    completed++; // Increment the completed count
                    queue.remove(process); // Remove the process from the queue
                }
            }

            // If no processes are in the queue, increment time and check again
            if (queue.isEmpty()) {
                currentTime++;
                lastProcess = null;
                continue;
            }

            // Process the next process with the shortest remaining burst time
            Process currentProcess = queue.poll();

            // Handle context switching if the last process was different
            if (lastProcess != null && !lastProcess.equals(currentProcess)) {
                currentTime += contextSwitchingTime; // Add context switch time
            }

            // Log the execution of the current process
            executionOrder.add(new ProcessExecution(
                    currentProcess.getName(),
                    1, // Each unit of execution is 1 time unit
                    currentProcess.getColor(),
                    currentProcess.getPid(),
                    currentProcess.getPriority(),
                    currentTime
            ));

            // Execute one time unit for the current process
            currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
            currentTime++;

            // If the process is finished, set its completion time and mark as completed
            if (currentProcess.getBurstTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                completed++;
                executionOrder2.add(currentProcess);
            } else {
                // If the process is not finished, add it back to the queue
                queue.add(currentProcess);
            }

            lastProcess = currentProcess; // Update last processed to the current one
        }

        return executionOrder;
    }

    // Prints the execution order and calculates average waiting and turnaround times
    public void printResults(List<Process> processes, List<ProcessExecution> executionOrder) {
        // Print the order of process execution
        System.out.println("Process Execution Order:");
        executionOrder.forEach(pe -> System.out.println(pe.processName));

        // Print individual process details
        System.out.println("\nProcess Details:");
        for (Process p : processes) {
            int waitTime = p.getWaitingTimeSRTF(p.getCompletionTime());
            int turnaroundTime = p.getTurnaroundTime(p.getCompletionTime());
            System.out.println("Process: " + p.getName());
            System.out.println("Waiting Time: " + waitTime);
            System.out.println("Turnaround Time: " + turnaroundTime + '\n');
        }

        // Calculate and print average waiting and turnaround times
        double avgWait = calculateAverageWaitingTime(processes);
        double avgTurnaround = calculateAverageTurnaroundTime(processes);

        System.out.println("Average Waiting Time: " + avgWait);
        System.out.println("Average Turnaround Time: " + avgTurnaround);
    }

    // Calculates the average waiting time for all processes
    public double calculateAverageWaitingTime(List<Process> processes) {
        int totalWaitingTime = 0;
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTimeSRTF(p.getCompletionTime());
        }
        return (double) totalWaitingTime / processes.size();
    }

    // Calculates the average turnaround time for all processes
    public double calculateAverageTurnaroundTime(List<Process> processes) {
        int totalTurnaroundTime = 0;
        for (Process p : processes) {
            totalTurnaroundTime += p.getTurnaroundTime(p.getCompletionTime());
        }
        return (double) totalTurnaroundTime / processes.size();
    }
}
