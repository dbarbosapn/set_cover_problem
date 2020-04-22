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

    int numRectanglesToCover;

    boolean firstSolution;

    public DFS(Collection<Vert> verts, Collection<Integer> rectanglesToCover, boolean firstSolution) {
        super(verts, rectanglesToCover);
        this.numRectanglesToCover = rectanglesToCover.size();
        this.firstSolution = firstSolution;
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
                        if (firstSolution) {
                            this.currentState = n;
                            return currentState.getChosenVerts().size();
                        } else if (!currentState.isFinal()
                                || currentState.getChosenVerts().size() < currentState.getChosenVerts().size()) {

                            this.currentState = n;

                            // It is possible to prove that the solution will not be better than
                            // Math.ceil(numRectanglesToCover / 3); therefore, we will use this as a breaker

                            if (currentState.getChosenVerts().size() <= Math.ceil((double) numRectanglesToCover / 3d)) {
                                return currentState.getChosenVerts().size();
                            }
                        }
                    }
                    stack.push(n);
                }
            }
        }

        throw new PartitionProblemException("Unable to find a solution for this instance");
    }

}