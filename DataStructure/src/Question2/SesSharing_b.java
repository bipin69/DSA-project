package Question2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SesSharing_b {
        public static List<Integer> findIndividuals(int n, int[][] intervals, int firstPerson) {
    Set<Integer> knownSet = new HashSet<>();
    knownSet.add(firstPerson);

    for (int[] interval : intervals) {
        int start = interval[0];
        int end = interval[1];

        // If the person who originally possessed the secret is in the set, add the individuals in the interval
        if (knownSet.contains(firstPerson)) {
            for (int i = start; i <= end; i++) {
                knownSet.add(i % n); // Use modulo to handle the circular nature of individuals
            }
        }
    }

    return new ArrayList<>(knownSet);
}

        public static void main(String[] args) {
            int n = 5;
            int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
            int firstPerson = 0;

            List<Integer> result = findIndividuals(n, intervals, firstPerson);
            System.out.println(result); // Output: [0, 1, 2, 3, 4]
        }
}

