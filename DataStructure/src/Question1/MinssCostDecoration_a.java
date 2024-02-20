package Question1;

public class MinssCostDecoration_a {

    public static int minCostToDecorate(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) {
            return 0;
        }

        int n = costs.length;
        int k = costs[0].length;

        // Initialize variables to keep track of the minimum and second minimum cost
        int minCost = 0;
        int secondMinCost = 0;
        int lastMinIndex = -1;

        // Iterate through the venues
        for (int i = 0; i < n; i++) {
            int currentMinCost = Integer.MAX_VALUE;
            int currentSecondMinCost = Integer.MAX_VALUE;
            int currentMinIndex = -1;

            // Update the costs for the current venue
            for (int j = 0; j < k; j++) {
                int cost = costs[i][j] + (j == lastMinIndex ? secondMinCost : minCost);

                // Update the current minimum and second minimum costs
                if (cost < currentMinCost) {
                    currentSecondMinCost = currentMinCost;
                    currentMinCost = cost;
                    currentMinIndex = j;
                } else if (cost < currentSecondMinCost) {
                    currentSecondMinCost = cost;
                }
            }

            // Update the overall minimum and second minimum costs for the next iteration
            minCost = currentMinCost;
            secondMinCost = currentSecondMinCost;
            lastMinIndex = currentMinIndex;
        }

        // The minimum cost is in the last row
        return minCost;
    }

    public static void main(String[] args) {
        int[][] costMatrix = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        int result = minCostToDecorate(costMatrix);
        System.out.println(result); // Output: 7
    }
}


