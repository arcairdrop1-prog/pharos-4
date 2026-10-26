# Pharos Batch Executor

An intelligent agent developed in **Java** for the Pharos Agent Arena, designed to group multiple independent actions into a single optimized transaction execution (Multicall equivalent), reducing gas costs and handling failures cleanly.

## 📋 Requirements Met

1. **Independent Action List (Req. 01):** Accepts a list of at least 3 independent actions (e.g., transfers, claims, or compounding)[cite: 3].
2. **Optimized Batch Execution (Req. 02):** Executes actions as a single batched transaction (Multicall)[cite: 3].
3. **Gas Savings Report (Req. 03):** Calculates and reports the gas cost reduction compared to executing each action individually[cite: 3].
4. **Partial Failure Handling (Req. 04):** Gracefully detects which action failed and explicitly logs the outcome for the remaining operations[cite: 3].

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac BatchExecutor.java
