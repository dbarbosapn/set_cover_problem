package rect_partition.approaches;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * This approach relies on choosing the next state to expand based on its cost
 * so far. Also, it creates a "bound" that prevents the search to go further
 * than the already found best solution
 */
public class BranchAndBound extends Approach {

    int numberOfRectangles;
    int bound = Integer.MAX_VALUE;
    boolean foundSolution = false;

    public BranchAndBound(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
        numberOfRectangles = rectanglesToCover.size();
    }

    @Override
    public int solve() throws PartitionProblemException {

        Set<State> visited = new HashSet<>();

        // This is a MinHeap that compares the costs of the states at insertion
        PriorityQueue<State> heap = new PriorityQueue<>((s1, s2) -> stateCost(s1) - stateCost(s2));
        heap.offer(currentState);

        while (!heap.isEmpty()) {
            State s = heap.poll();

            if (s.isFinal()) {
                foundSolution = true;

                int solution = s.getChosenVerts().size();
                if (solution < bound) {
                    bound = solution;
                    currentState = s;

                    // It is possible to prove that the solution will not be better than
                    // Math.ceil(numRectanglesToCover / 3); therefore, we will use this as a breaker
                    if (solution <= Math.ceil((double) numberOfRectangles / 3d)) {
                        return solution;
                    }
                }
            }

            // Check if the cost of this state is equal or worse than the best solution. If
            // it is, we don't need to expand it.
            if (stateCost(s) >= bound) {
                continue;
            }

            List<State> neighbours = s.expand();

            this.statesExpanded += neighbours.size();

            for (State neighbour : neighbours) {
                // In this A* approach, we're not updating cost values in the heap if we find
                // the same state again. That is because in this specific problem if we find
                // the same instance later it will always have the same cost since the cost is
                // as big as how many vertexes have been chosen. So, we just add the child
                // states to the heap and keep doing the same thing until we find a solution
                if (!visited.contains(neighbour)) {
                    heap.offer(neighbour);
                }
            }
        }

        if (foundSolution)
            return bound;

        throw new PartitionProblemException("Unable to find a solution for this instance");
    }

    /**
     * The cost of the state is the number of edges since the root. That is the same
     * as the number of chosen verts, since we choose one vertex per expansion
     * 
     * @param state to calculate the cost
     * @return the cost of the state
     */
    private int stateCost(State state) {
        return state.getChosenVerts().size();
    }

}