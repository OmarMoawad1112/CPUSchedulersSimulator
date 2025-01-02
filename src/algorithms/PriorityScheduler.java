package algorithms;

import models.Process;
import models.ProcessExecution;
import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler {

    // Schedules processes using a non-preemptive priority scheduling algorithm.
    public List<ProcessExecution> schedule(List<Process> processes, int contextSwitchingTime) {
        // Sort processes based on priority first, and arrival time if priorities are equal
        processes.sort((p1, p2) -> {
            // Compare by priority
            int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
            if (priorityCompare != 0) {
                return priorityCompare; // Lower priority value comes first
            }
            // If priorities are equal, compare by arrival time
            return Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
        });

        // List to store the execution order of processes
        List<ProcessExecution> executionOrder = new ArrayList<>();

        // Tracks the current time in the scheduler
        int currentTime = 0;

        // Iterate over each process and schedule it
        for (Process process : processes) {
            // If the process has not yet arrived, wait until it does
            if (process.getArrivalTime() > currentTime) {
                currentTime = process.getArrivalTime();
            }

            // Add the process execution to the schedule
            executionOrder.add(new ProcessExecution(
                    process.getName(),        // Process name
                    process.getBurstTime(),   // Execution duration (burst time)
                    process.getColor(),       // Display color for visualization
                    process.getPid(),         // Unique process ID
                    process.getPriority(),    // Process priority
                    currentTime               // Start time of execution
            ));

            // Update the process completion time
            currentTime += process.getBurstTime();
            process.setCompletionTime(currentTime);

            // Add context switching time after the process finishes
            currentTime += contextSwitchingTime;
        }

        return executionOrder;
    }

    // Prints the results of the scheduling algorithm.
    public void printResults(List<Process> processes, List<ProcessExecution> executionOrder) {
        // Display the order of execution
        System.out.println("Process Execution Order:");
        for (ProcessExecution pe : executionOrder) {
            System.out.print(pe.getProcessName() + " -> ");
        }
        System.out.println("end");

        // Display individual process metrics
        for (Process p : processes) {
            // Calculate and display waiting time and turnaround time for each process
            int waitTime = p.getWaitingTime(p.getCompletionTime());
            int turnaroundTime = p.getTurnaroundTime(p.getCompletionTime());
            System.out.println("Process: " + p.getName());
            System.out.println("Waiting Time: " + waitTime);
            System.out.println("Turnaround Time: " + turnaroundTime + '\n');
        }

        // Calculate and display average waiting time and turnaround time
        double avgWait = calculateAverageWaitingTime(processes);
        double avgTurnaround = calculateAverageTurnaroundTime(processes);

        System.out.println("Average Waiting Time: " + avgWait);
        System.out.println("Average Turnaround Time: " + avgTurnaround);
    }

    // Calculates the average waiting time for all processes.
    public double calculateAverageWaitingTime(List<Process> processes) {
        int totalWaitingTime = 0;

        // Sum up waiting times for all processes
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime(p.getCompletionTime());
        }

        // Calculate and return the average
        return (double) totalWaitingTime / processes.size();
    }

    // Calculates the average turnaround time for all processes.
    public double calculateAverageTurnaroundTime(List<Process> processes) {
        int totalTurnaroundTime = 0;

        // Sum up turnaround times for all processes
        for (Process p : processes) {
            totalTurnaroundTime += p.getTurnaroundTime(p.getCompletionTime());
        }

        // Calculate and return the average
        return (double) totalTurnaroundTime / processes.size();
    }
}
