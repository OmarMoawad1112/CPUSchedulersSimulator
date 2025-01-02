package algorithms;

import models.Process;
import models.ProcessExecution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class ShortestJobFirstScheduler {

    // List of processes to schedule
    private List<Process> processList;

    // Time required for context switching between processes
    private int contextSwitchTime;

    // Maximum allowed waiting time to prevent process starvation
    private static final int MAX_WAIT_TIME = 20;

    // List to store the execution order of processes
    private List<Process> executionOrder;

    // Constructor to initialize the process list and context switching time
    public ShortestJobFirstScheduler(List<Process> processList, int contextSwitchTime) {
        this.processList = processList;
        this.contextSwitchTime = contextSwitchTime;
    }

    // Schedules the processes using the Shortest Job First (SJF) scheduling algorithm.
    public List<ProcessExecution> schedule() {
        // Sort processes by arrival time, burst time, and priority as a tie-breaker
        processList.sort(Comparator.comparingInt(Process::getArrivalTime)
                .thenComparingInt(Process::getBurstTime)
                .thenComparing(Process::getPriority));

        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        executionOrder = new ArrayList<>();
        List<ProcessExecution> executionOrder2 = new ArrayList<>();
        boolean isFirstProcess = true; // Flag to handle special case for the first process

        while (!processList.isEmpty()) {
            // Find all processes that have arrived by the current time
            List<Process> availableProcesses = new ArrayList<>();
            for (Process p : processList) {
                if (p.getArrivalTime() <= currentTime) {
                    availableProcesses.add(p);
                }
            }

            // If no process is available, increment the current time
            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }

            // Check for starvation (process waiting too long)
            Process starvedProcess = null;
            for (Process process : availableProcesses) {
                if (process.getWaitingTime(currentTime) > MAX_WAIT_TIME && process.getBurstTime() > 0) {
                    starvedProcess = process;
                    break;
                }
            }

            // Select the process to execute next
            Process selectedProcess;
            if (starvedProcess != null) {
                // Handle starvation by prioritizing the starved process
                System.out.println("Process " + starvedProcess.getName() + " starved! Executing immediately.");
                selectedProcess = starvedProcess;
            } else {
                // Select the process with the shortest burst time
                selectedProcess = availableProcesses.stream()
                        .min(Comparator.comparingInt(Process::getBurstTime)
                                .thenComparingInt(Process::getArrivalTime))
                        .orElseThrow(() -> new NoSuchElementException("No process found in availableProcesses"));
            }

            // Remove the selected process from the original list
            processList.remove(selectedProcess);

            // Add selected process to the execution order
            executionOrder.add(selectedProcess);

            // Add execution details to the visualization list
            executionOrder2.add(new ProcessExecution(
                    selectedProcess.getName(),
                    selectedProcess.getBurstTime(),
                    selectedProcess.getColor(),
                    selectedProcess.getPid(),
                    selectedProcess.getPriority(),
                    currentTime // Start time of the process
            ));

            // Simulate process execution and update completion time
            int completionTime = currentTime + selectedProcess.getBurstTime();
            selectedProcess.setCompletionTime(completionTime);

            // Update current time to include burst time and context switching
            currentTime = completionTime + contextSwitchTime;

            // Calculate waiting and turnaround times
            int waitingTime = selectedProcess.getWaitingTime(completionTime);
            int turnaroundTime = selectedProcess.getTurnaroundTime(completionTime);

            // Accumulate total waiting and turnaround times
            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            // Print process execution details
            System.out.println("Executed Process: " + selectedProcess.getName());
            System.out.println("Waiting Time: " + waitingTime);
            System.out.println("Turnaround Time: " + turnaroundTime);
            System.out.println("---------------------------");
        }

        // Print average waiting time
        System.out.println("Average Waiting Time: " + calculateAverageWaitingTime());

        // Print average turnaround time
        System.out.println("Average Turnaround Time: " + calculateAverageTurnaroundTime());

        // Print execution order
        System.out.println("Execution Order: ");
        executionOrder.forEach(p -> System.out.print(p.getName() + " -> "));
        System.out.println("End");

        // Return the execution details for visualization
        return executionOrder2;
    }

    // Calculates the average waiting time for all executed processes.
    public double calculateAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (Process process : executionOrder) {
            totalWaitingTime += process.getWaitingTime(process.getCompletionTime());
        }
        return (double) totalWaitingTime / executionOrder.size();
    }

    // Calculates the average turnaround time for all executed processes.
    public double calculateAverageTurnaroundTime() {
        int totalTurnaroundTime = 0;
        for (Process process : executionOrder) {
            totalTurnaroundTime += process.getTurnaroundTime(process.getCompletionTime());
        }
        return (double) totalTurnaroundTime / executionOrder.size();
    }
}
