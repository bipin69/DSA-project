package Question4;

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left, right;

    public TreeNode(int val) {
        this.val = val;
        this.left = this.right = null;
    }
}

    public class ClosestvValue_b {

    public static List<Integer> closestValues(TreeNode root, double target, int x) {
        List<Integer> result = new ArrayList<>();
        closestValuesHelper(root, target, x, result);
        return result;
    }

    private static void closestValuesHelper(TreeNode node, double target, int x, List<Integer> result) {
        if (node == null) {
            return;
        }

        closestValuesHelper(node.left, target, x, result);

        if (result.size() < x) {
            result.add(node.val);
        } else {
            double currentDiff = Math.abs(node.val - target);
            double maxDiff = Math.abs(result.get(0) - target);

            if (currentDiff < maxDiff) {
                result.remove(0);
                result.add(node.val);
            } else {
                // Since the tree is balanced, if we encounter a larger difference, we can stop searching in the right subtree
                return;
            }
        }

        closestValuesHelper(node.right, target, x, result);
    }

    public static void main(String[] args) {
        /*
         * Provided Tree:
         *       4
         *      / \
         *     2   5
         *    / \
         *   1   3
         */
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        double target = 3.8;
        int x = 2;

        List<Integer> closestValues = closestValues(root, target, x);
        System.out.println("Closest values to " + target + " are: " + closestValues); // Output: [4, 5]
    }
}
