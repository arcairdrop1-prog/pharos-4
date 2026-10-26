import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BatchExecutor {

    // Representation of an independent action (Requirement 01)
    public static class Action {
        String id;
        String description;
        boolean shouldFail; // Flag used to simulate a partial failure for testing (Requirement 04)

        public Action(String id, String description, boolean shouldFail) {
            this.id = id;
            this.description = description;
            this.shouldFail = shouldFail;
        }
    }

    // Representation of the batch execution result (Requirements 03 & 04)
    public static class BatchResult {
        LocalDateTime timestamp;
        int totalActions;
        int successfulActions;
        double individualGasCostUSD;
        double batchGasCostUSD;
        double gasSavingsUSD;
        String executionStatus;
        List<String> logs;

        public BatchResult(int total, int success, double indGas, double batchGas, double savings, String status, List<String> logs) {
            this.timestamp = LocalDateTime.now();
            this.totalActions = total;
            this.successfulActions = success;
            this.individualGasCostUSD = indGas;
            this.batchGasCostUSD = batchGas;
            this.gasSavingsUSD = savings;
            this.executionStatus = status;
            this.logs = logs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("=== BATCH EXECUTION REPORT [%s] ===\n", timestamp));
            sb.append(String.format("Status: %s\n", executionStatus));
            sb.append(String.format("Total Actions: %d | Successful: %d\n", totalActions, successfulActions));
            sb.append(String.format("Individual Gas Cost: $%.2f | Batch Gas Cost: $%.2f | Savings: $%.2f\n", 
                    individualGasCostUSD, batchGasCostUSD, gasSavingsUSD));
            sb.append("Detailed Logs:\n");
            for (String log : logs) {
                sb.append(" - ").append(log).append("\n");
            }
            return sb.toString();
        }
    }

    // Requirements 01, 02, 03 & 04: Batch execution logic
    public BatchResult executeBatch(List<Action> actions) {
        // Requirement 01: Accepts a list of at least 3 independent actions
        if (actions == null || actions.size() < 3) {
            throw new IllegalArgumentException("Requirement 01 violation: Batch must contain at least 3 independent actions.");
        }

        List<String> executionLogs = new ArrayList<>();
        int successCount = 0;
        
        // Gas estimation constants in USD
        double individualGasPerAction = 2.50; 
        double totalIndividualGas = actions.size() * individualGasPerAction;
        
        // Requirement 03: Optimized gas structure for batching (multicall efficiency)
        double batchBaseGas = 3.00;
        double batchMarginalGasPerAction = 0.80;
        double totalBatchGas = batchBaseGas + (actions.size() * batchMarginalGasPerAction);
        
        boolean batchFailed = false;
        String status = "SUCCESS (All actions executed in a single batch multicall)";

        // Requirement 02 & 04: Executes as a batch and handles partial/full failures clearly
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            if (action.shouldFail) {
                batchFailed = true;
                status = "PARTIAL FAILURE: Action [" + action.id + "] failed. Execution halted or reverted.";
                executionLogs.add("ERROR: Action " + action.id + " (" + action.description + ") failed during execution.");
                break; // Stops or handles remaining actions as per atomic rule
            } else {
                successCount++;
                executionLogs.add("SUCCESS: Action " + action.id + " (" + action.description + ") processed successfully.");
            }
        }

        if (batchFailed) {
            totalBatchGas = batchBaseGas + (successCount * batchMarginalGasPerAction);
        }

        // Requirement 03: Report fuel/gas savings compared to individual execution
        double gasSavings = totalIndividualGas - totalBatchGas;

        return new BatchResult(actions.size(), successCount, totalIndividualGas, totalBatchGas, gasSavings, status, executionLogs);
    }

    public static void main(String[] args) {
        BatchExecutor executor = new BatchExecutor();

        System.out.println("--- Scenario 1: Successful Batch Execution ---");
        List<Action> actions = new ArrayList<>();
        actions.add(new Action("TX-01", "Claim Staking Rewards", false));
        actions.add(new Action("TX-02", "Claim Airdrop Allocation", false));
        actions.add(new Action("TX-03", "Compound Liquidity Pool Fees", false));

        BatchResult result = executor.executeBatch(actions);
        System.out.println(result);
        
        System.out.println("\n--- Scenario 2: Partial Failure Handling ---");
        List<Action> actionsWithFailure = new ArrayList<>();
        actionsWithFailure.add(new Action("TX-11", "Claim Governance Tokens", false));
        actionsWithFailure.add(new Action("TX-12", "Execute Vault Rebalance", true)); // Simulates a failure here
        actionsWithFailure.add(new Action("TX-13", "Withdraw Yield Farm", false));

        BatchResult failureResult = executor.executeBatch(actionsWithFailure);
        System.out.println(failureResult);
    }
}
