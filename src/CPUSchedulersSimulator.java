import algorithms.FCAIScheduler;
import algorithms.PriorityScheduler;
import algorithms.ShortestJobFirstScheduler;
import algorithms.ShortestRemainingTimeFirstScheduler;
import models.Process;
import models.ProcessExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class  CPUSchedulersSimulator {
    public static void main(String[] args) {
        // Initialize scanner for user input
        Scanner scanner = new Scanner(System.in);

        // List to store processes entered by the user
        ArrayList<Process> processes = new ArrayList<>();

        // Random object to generate random colors for processes
        Random random = new Random();

        // Ask user to input the number of processes
        System.out.print("Enter number of processes: ");
        int numProcesses = scanner.nextInt();

        // Loop to gather details of each process
        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Process " + (i + 1) + ":");

            // Get process name
            System.out.print("  Name: ");
            String name = scanner.next();

            // Get arrival time for the process
            System.out.print("  Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            // Get burst time for the process (time required for execution)
            System.out.print("  Burst Time: ");
            int burstTime = scanner.nextInt();

            // Get priority for the process (lower value = higher priority)
            System.out.print("  Priority: ");
            int priority = scanner.nextInt();

            // Generate a random color for this process
            String colorHex = generateRandomColor(random);

            // Generate a unique Process ID (PID)
            int pid = i + 1;

            // Create a new Process object and add it to the list
            processes.add(new Process(name, arrivalTime, burstTime, priority, colorHex, pid));

            // Separate each process input visually in the console
            System.out.println("-------------------------------------");
        }

        // Display available CPU scheduling algorithms
        System.out.println("Select the Scheduling Algorithm:");
        System.out.println("1. Non-preemptive Priority Scheduling");
        System.out.println("2. Non-preemptive Shortest Job First (SJF)");
        System.out.println("3. Shortest Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");
        System.out.print("Please enter your choice: ");
        int choice = scanner.nextInt();

        // Validate the user's choice to ensure it is within the valid range
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid choice. Exiting program.");
            return; // Exit the program if choice is invalid
        }

        // Ask the user for context switching time
        System.out.print("Enter context switching time: ");
        int contextSwitchingTime = scanner.nextInt();

        // Initialize variables for scheduling results
        List<ProcessExecution> schedule = null; // Stores the execution order of processes
        String scheduleName = null;            // Name of the chosen scheduling algorithm
        double averageWaitingTime = 0;         // Average waiting time for all processes
        double averageTurnaroundTime = 0;      // Average turnaround time for all processes

        // Execute the chosen scheduling algorithm based on the user's choice
        switch (choice) {
            case 1:
                // Non-preemptive Priority Scheduling
                PriorityScheduler priorityScheduler = new PriorityScheduler();
                scheduleName = "Process Execution by Priority Scheduling";

                // Generate the schedule and calculate performance metrics
                schedule = priorityScheduler.schedule(processes, contextSwitchingTime);
                averageWaitingTime = priorityScheduler.calculateAverageWaitingTime(processes);
                averageTurnaroundTime = priorityScheduler.calculateAverageTurnaroundTime(processes);

                // Display results for Priority Scheduling
                priorityScheduler.printResults(processes, schedule);
                break;

            case 2:
                // Non-preemptive Shortest Job First (SJF) Scheduling
                ShortestJobFirstScheduler sjfScheduler = new ShortestJobFirstScheduler(processes, contextSwitchingTime);
                scheduleName = "Process Execution by Shortest Job First Scheduling";

                // Generate the schedule and calculate performance metrics
                schedule = sjfScheduler.schedule();
                averageWaitingTime = sjfScheduler.calculateAverageWaitingTime();
                averageTurnaroundTime = sjfScheduler.calculateAverageTurnaroundTime();
                break;

            case 3:
                // Preemptive Shortest Remaining Time First (SRTF) Scheduling
                List<Process> executionOrder = new ArrayList<>();
                ShortestRemainingTimeFirstScheduler srtfScheduler = new ShortestRemainingTimeFirstScheduler();
                scheduleName = "Process Execution by Shortest Remaining Time First Scheduling";

                // Generate the schedule and calculate performance metrics
                schedule = srtfScheduler.schedule(processes, contextSwitchingTime);
                averageWaitingTime = srtfScheduler.calculateAverageWaitingTime(processes);
                averageTurnaroundTime = srtfScheduler.calculateAverageTurnaroundTime(processes);

                // Store the execution order for printing results
                executionOrder = srtfScheduler.executionOrder2;

                // Display results for SRTF
                srtfScheduler.printResults(executionOrder, schedule);
                break;

            case 4:
                // FCAI Scheduling
                for (Process p : processes) {
                    // Ask for Round Robin quantum for each process
                    System.out.print("Enter Round Robin Quantum for " + p.getName() + " : ");
                    int quantum = scanner.nextInt();
                    p.setUpdatedQuantum(quantum);
                }

                FCAIScheduler fcaiScheduler = new FCAIScheduler(processes);
                scheduleName = "Process Execution by FCAI Scheduling";

                // Generate the schedule and calculate performance metrics
                schedule = fcaiScheduler.schedule(contextSwitchingTime);
                averageWaitingTime = fcaiScheduler.calculateAverageWaitingTime(processes);
                averageTurnaroundTime = fcaiScheduler.calculateAverageTurnaroundTime(processes);

                // Display results for FCAI
                fcaiScheduler.printResults(processes, schedule);
                break;
        }

        // If a valid schedule is generated, display it using the Gantt chart
        if (schedule != null) {
            GanttChart.createAndShowGUI(
                    schedule,              // List of process executions
                    scheduleName,          // Name of the scheduling algorithm
                    averageWaitingTime,    // Average waiting time
                    averageTurnaroundTime  // Average turnaround time
            );
        }
    }

    // Helper method to generate random hex color codes
    private static String generateRandomColor(Random random) {
        // Generate a random integer for an RGB color
        int nextInt = random.nextInt(0xffffff + 1);

        // Convert the integer to a hex color code and return it
        return String.format("#%06x", nextInt);
    }
}
