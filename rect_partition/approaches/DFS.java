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
 * This is a simple approach where we keep expanding the states until we find a
 * final one with a DFS
 */
public class DFS extends Approach {

    // We save the visited states to prevent cycles in the search
    Set<State> visited = new HashSet<>();

    public DFS(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
    }

    @Override
    public int solve() throws PartitionProblemException {
        Stack<State> stack = new Stack<>();
        stack.add(currentState);

        while (!stack.isEmpty()) {
            State s = stack.pop();
            if (!visited.contains(s)) {
                visited.add(s);

                List<State> neighbours = s.expand();
                this.statesExpanded += neighbours.size();

                for (State n : neighbours) {
                    if (n.isFinal()) {
                        currentState = n;
                        return currentState.getChosenVerts().size();
                    }
                    stack.push(n);
                }
            }
        }

        throw new PartitionProblemException("Unable to find a solution for this instance");
    }

}