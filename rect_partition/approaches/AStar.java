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
 * This approach relies on choosing the child states according to an heuristic
 * that predicts how far that state is from the solution.
 */
public class AStar extends Approach {

    int numberOfRectangles;
    int bestSolutionSoFar = Integer.MAX_VALUE;

    public AStar(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
        numberOfRectangles = rectanglesToCover.size();
    }

    @Override
    public int solve() throws PartitionProblemException {

        Set<State> visited = new HashSet<>();

        // This is a MinHeap that compares the scores of the states at insertion
        PriorityQueue<State> heap = new PriorityQueue<>((s1, s2) -> stateScore(s1) - stateScore(s2));
        heap.offer(currentState);

        while (!heap.isEmpty()) {
            State s = heap.poll();

            if (s.isFinal()) {
                int solution = s.getChosenVerts().size();
                if (solution < bestSolutionSoFar) {
                    bestSolutionSoFar = solution;
                    currentState = s;

                    // It is possible to prove that the solution will not be better than
                    // Math.ceil(numRectanglesToCover / 3); therefore, we will use this as a breaker

                    if (solution <= Math.ceil((double) numberOfRectangles / 3d)) {
                        return solution;
                    }
                }
            }

            List<State> neighbours = s.expand();

            this.statesExpanded += neighbours.size();

            for (State neighbour : neighbours) {
                // In this A* approach, we're not updating cost values in the heap if we find
                // the same state again. That is, because in this specific problem, if we find
                // the same instance later it will have a bigger cost with no doubt. Because
                // every edge in the graph has the same weight (1). So, we just add the child
                // states to the heap and keep doing the same thing until we find a solution
                if (!visited.contains(neighbour))
                    heap.offer(neighbour);
            }
        }

        throw new PartitionProblemException("Unable to find a solution for this instance");
    }

    /**
     * The state score is the sum between the cost from the root state to the
     * current one plus the value of the heuristic. Since as we progress through the
     * states we add one vert per expansion, the cost from the root to the current
     * state is also the number of chosen vertexes.
     * 
     * @param state - the state to evaluate
     * @return the score of the state
     */
    private int stateScore(State state) {
        return state.getChosenVerts().size() + heuristic(state);
    }

    /**
     * As an heuristic, we will use the number of rectangles left as the predicted
     * cost. This is an acceptable heuristic since to cover all the rectangles left
     * we will need AT LEAST one vertex for each one. So, h(s) >= cost(s)
     * 
     * @param state
     * @return
     */
    private int heuristic(State state) {
        return state.getRectanglesLeft().size();
    }

}