package rect_partition.approaches;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * This approach is a very straight forward IDFS with no limit for K. We're not
 * implementing the "wait for best solution" approach because it would be an
 * extremely slow algorithm in the worst case even for small instances.
 */
public class IDFS extends Approach {

    private static final int INITIAL_K = 0;

    private boolean found = false;

    public IDFS(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
    }

    @Override
    public int solve() throws PartitionProblemException {
        int k = INITIAL_K;

        State root = currentState;

        while (true) {
            boolean remaining = DFS(root, k);
            if (found)
                return currentState.getSolution();
            else if (!remaining) {
                throw new PartitionProblemException("Unable to find a solution for this instance");
            }
            k++;
        }

    }

    private boolean DFS(State root, int k) throws PartitionProblemException {
        boolean remaining = true;

        Set<State> visited = new HashSet<>();

        Stack<State> stack = new Stack<>();
        Stack<Integer> depths = new Stack<>();
        stack.add(currentState);
        depths.add(0);

        while (!stack.isEmpty()) {
            State s = stack.pop();
            int depth = depths.pop();

            if (!visited.contains(s))
                visited.add(s);

            List<State> neighbours = s.expand();
            if (neighbours.size() == 0) {
                remaining = false;
            }

            this.statesExpanded += neighbours.size();

            for (State n : neighbours) {
                if (depth < k) {
                    if (n.isFinal()) {
                        this.currentState = n;
                        found = true;
                        return true;
                    }
                    stack.push(n);
                    depths.push(depth + 1);
                }
            }
        }

        return remaining;
    }

}