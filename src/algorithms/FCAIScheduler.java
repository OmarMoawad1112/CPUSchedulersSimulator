package algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Process;
import models.ProcessExecution;

public class FCAIScheduler {

    private List<Process> processList; // List of all processes
    private List<String> timeline;    // Execution timeline for reporting
    private LinkedList<Process> readyQueue; // Ready queue for processes
    private double v1;                // Scaling factor for Arrival Time
    private double v2;                // Scaling factor for Remaining Burst Time

    // Constructor that initializes the scheduler with a list of processes
    public FCAIScheduler(List<Process> processes) {
        this.processList = new ArrayList<>(processes);
        this.timeline = new ArrayList<>();
        this.readyQueue = new LinkedList<>();
        calculateV1();  // Calculate scaling factor for Arrival Time
        calculateV2();  // Calculate scaling factor for Burst Time
    }

    // Updates the ready queue based on the current time
    private void updateReadyQueueState(int currentTime) {
        Iterator<Process> iterator = processList.iterator();
        while (iterator.hasNext()) {
            Process process = iterator.next();
            if (process.getArrivalTime() <= currentTime && !readyQueue.contains(process)) {
                readyQueue.add(process);  // Add process to the ready queue
                iterator.remove(); // Remove from processList once added to readyQueue
            }
        }
        calculateFcaiFactor();  // Recalculate FCAI factor after updating ready queue
    }

    // Calculates the FCAI factor for processes in the ready queue
    private void calculateFcaiFactor() {
        for (Process process : readyQueue) {
            process.calculateFcaiFactor(v1, v2);  // Calculate FCAI for each process
        }
    }

    // Retrieves the process with the lowest FCAI factor from the ready queue
    private Process getBestProcessFromQueue() {
        Process bestProcess = null;
        for (Process process : readyQueue) {
            if (bestProcess == null ||
                    process.getFcaiFactor() < bestProcess.getFcaiFactor() ||
                    (process.getFcaiFactor() == bestProcess.getFcaiFactor() &&
                            process.getArrivalTime() < bestProcess.getArrivalTime())) {
                bestProcess = process;
            }
        }
        return bestProcess;  // Return the process with the lowest FCAI factor
    }

    // The main scheduling method that returns a list of process executions
    public List<ProcessExecution> schedule(int contextSwitchingTime) {
        int currentTime = 0;
        int choice = 2;  // Decision variable to choose scheduling strategy
        Process currentProcess = null;
        List<ProcessExecution> executionOrder = new ArrayList<>();

        while (!processList.isEmpty() || !readyQueue.isEmpty()) {
            updateReadyQueueState(currentTime);  // Update the ready queue based on current time

            // If the ready queue is empty, wait until new processes arrive
            while (readyQueue.isEmpty() && !processList.isEmpty()) {
                currentTime++;
                updateReadyQueueState(currentTime);
            }

            // Break if both processList and readyQueue are empty
            if (readyQueue.isEmpty()) break;

            int start = currentTime;  // Track the start time of the process

            // Choose scheduling strategy based on 'choice' variable
            if (choice == 1) {
                currentProcess = getBestProcessFromQueue();  // Get the best process based on FCAI factor
                readyQueue.remove(currentProcess);  // Remove it from the ready queue
            } else {
                currentProcess = readyQueue.poll();  // Get process in FCFS order
            }

            // Get the quantum for the current process and calculate execution time
            int quantum = currentProcess.getUpdatedQuantum();
            int nonPreemptiveTime = (int) Math.ceil(quantum * 0.4);  // Calculate non-preemptive execution time
            int executionTime = Math.min(nonPreemptiveTime, currentProcess.getBurstTime());

            // Execute the process non-preemptively
            currentTime += executionTime;
            currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
            int remainingQuantum = quantum - executionTime;

            // Continue executing the process if there is remaining burst time and quantum
            while (currentProcess.getBurstTime() > 0 && remainingQuantum > 0) {
                // If a better process arrives, stop execution and requeue
                if (getBestProcessFromQueue() != null && getBestProcessFromQueue().getFcaiFactor() < currentProcess.getFcaiFactor()) {
                    break;
                }
                currentTime++;
                remainingQuantum--;
                currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
                updateReadyQueueState(currentTime);  // Update ready queue for every time unit
            }

            // Track the execution order of the process
            executionOrder.add(new ProcessExecution(
                    currentProcess.getName(),
                    currentTime - start,
                    currentProcess.getColor(),
                    currentProcess.getPid(),
                    currentProcess.getPriority(),
                    start
            ));

            // Update process status based on its remaining burst time
            if (currentProcess.getBurstTime() == 0) {
                currentProcess.setCompletionTime(currentTime);  // Mark the process as completed
                timeline.add("Process " + currentProcess.getName() + ": from " + start + " to " + currentTime + " --> completed");
                choice = 2;
            } else if (remainingQuantum == 0) {
                currentProcess.setUpdatedQuantum(quantum + 2);  // Update quantum for the next round
                timeline.add("Process " + currentProcess.getName() + ": from " + start + " to " + currentTime +
                        ", Quantum: " + quantum + " --> " + currentProcess.getUpdatedQuantum());
                readyQueue.add(currentProcess);  // Re-add process to the ready queue
                choice = 2;
            } else {
                currentProcess.setUpdatedQuantum(quantum + remainingQuantum);  // Update quantum after execution
                timeline.add("Process " + currentProcess.getName() + ": from " + start + " to " + currentTime +
                        ", Quantum: " + quantum + " --> " + currentProcess.getUpdatedQuantum());
                readyQueue.add(currentProcess);  // Re-add process to the ready queue
                choice = 1;
            }

            // Account for context switching time
            currentTime += contextSwitchingTime;
        }
        return executionOrder;  // Return the execution order of processes
    }

    // Calculate scaling factor v1 based on the latest arrival time
    private void calculateV1() {
        double lastArriveTime = processList.stream()
                .mapToDouble(Process::getArrivalTime)
                .max()
                .orElse(0.0);
        v1 = lastArriveTime / 10.0;
    }

    // Calculate scaling factor v2 based on the maximum burst time
    private void calculateV2() {
        double maxBurstTime = processList.stream()
                .mapToDouble(Process::getBurstTime)
                .max()
                .orElse(0.0);
        v2 = maxBurstTime / 10.0;
    }

    // Print the results (timeline, waiting time, turnaround time) for the execution
    public void printResults(List<Process> processes, List<ProcessExecution> executionOrder) {
        timeline.forEach(System.out::println);  // Print the execution timeline

        // Print individual process results (waiting time, turnaround time)
        for (Process p : processes) {
            int waitTime = p.getWaitingTimeFcai(p.getOriginalBurstTime());
            int turnaroundTime = p.getTurnaroundTime(p.getCompletionTime());
            System.out.println("Process: " + p.getName());
            System.out.println("Waiting Time: " + waitTime);
            System.out.println("Turnaround Time: " + turnaroundTime + '\n');
        }

        // Calculate and print average waiting time and turnaround time
        double avgWait = calculateAverageWaitingTime(processes);
        double avgTurnaround = calculateAverageTurnaroundTime(processes);

        System.out.println("Average Waiting Time: " + avgWait);
        System.out.println("Average Turnaround Time: " + avgTurnaround);
    }

    // Calculate the average waiting time for all processes
    public double calculateAverageWaitingTime(List<Process> processes) {
        int totalWaitingTime = 0;
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTimeFcai(p.getOriginalBurstTime());
        }
        return (double) totalWaitingTime / processes.size();
    }

    // Calculate the average turnaround time for all processes
    public double calculateAverageTurnaroundTime(List<Process> processes) {
        int totalTurnaroundTime = 0;
        for (Process p : processes) {
            totalTurnaroundTime += p.getTurnaroundTime(p.getCompletionTime());
        }
        return (double) totalTurnaroundTime / processes.size();
    }
}
