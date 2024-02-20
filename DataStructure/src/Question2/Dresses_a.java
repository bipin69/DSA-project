package Question2;

public class Dresses_a {

    public static int minMovesToEqualize(int[] machines) {
        int totalDresses = 0;
        for (int dresses : machines) {
            totalDresses += dresses;
        }

        int numOfMachines = machines.length;
        if (totalDresses % numOfMachines != 0) {
            return -1; // Cannot equalize the number of dresses
        }

        int target = totalDresses / numOfMachines;
        int moves = 0, balance = 0;

        for (int dresses : machines) {
            balance += dresses - target;
            moves = Math.max(moves, Math.max(Math.abs(balance), dresses - target));
        }

        return moves;
    }

    public static void main(String[] args) {
        int[] machines = {1, 0, 5};
        int minMoves = minMovesToEqualize(machines);
        System.out.println("Minimum number of moves required: " + minMoves);
    }
}