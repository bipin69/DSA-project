package Question3;

import java.util.PriorityQueue;

public class ScoresTracker_a {
    private PriorityQueue<Double> minHeap; // stores the larger half of the scores
    private PriorityQueue<Double> maxHeap; // stores the smaller half of the scores

    public ScoresTracker_a() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>((a, b) -> Double.compare(b, a)); // max heap comparator
    }

    public void addScore(double score) {
        if (maxHeap.isEmpty() || score <= maxHeap.peek()) {
            maxHeap.offer(score);
        } else {
            minHeap.offer(score);
        }

        // Balance the heaps to ensure their sizes differ by at most 1
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double getMedianScore() {
        if (maxHeap.isEmpty() && minHeap.isEmpty()) {
            throw new IllegalStateException("No scores available.");
        }

        if (maxHeap.size() == minHeap.size()) {
            // Even number of scores, return the average of the two middle scores
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } else {
            // Odd number of scores, return the middle score
            return maxHeap.peek();
        }
    }

    public static void main(String[] args) {
        ScoresTracker_a scoreTracker = new ScoresTracker_a();
        scoreTracker.addScore(85.5);
        scoreTracker.addScore(92.3);
        scoreTracker.addScore(77.8);
        scoreTracker.addScore(90.1);
        double median1 = scoreTracker.getMedianScore();
        System.out.println("Median 1: " + median1);

        scoreTracker.addScore(81.2);
        scoreTracker.addScore(88.7);
        double median2 = scoreTracker.getMedianScore();
        System.out.println("Median 2: " + median2);
    }
}


