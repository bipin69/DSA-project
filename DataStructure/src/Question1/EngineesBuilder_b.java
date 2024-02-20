package Question1;



import java.util.Arrays;

public class EngineesBuilder_b {
    public static int minTimeToBuildEngines(int[] engines, int splitCost) {
        // Sort engines in ascending order
        Arrays.sort(engines);

        int maxEngines = engines.length;
        int[] dp = new int[maxEngines + 1]; // Dynamic programming array

        Arrays.fill(dp, Integer.MAX_VALUE - splitCost);

        // Base case: building one engine takes its own time
        dp[1] = engines[0];

        // Iterate through each engine
        for (int i = 2; i <= maxEngines; i++) {
            // Iterate from current number of engineers down to 1 (representing splitting)
            for (int j = i; j >= 1; j--) {
                int time = Math.max(dp[j], engines[i - 1]); // Time to build current engine

                // If more than 1 engineer, consider splitting cost
                if (j > 1) {
                    time = Math.min(time, dp[j / 2] + splitCost);
                }

                // Update minimum time for current number of engineers
                dp[i] = Math.min(dp[i], time);
            }
        }

        return dp[maxEngines]; // Return minimum time for building all engines
    }

    public static void main(String[] args) {
        int[] engines = {3, 4, 5, 2};
        int splitCost = 2;

        int minTime = minTimeToBuildEngines(engines, splitCost);
        System.out.println("Minimum time needed to build all engines: " + minTime);
    }
}