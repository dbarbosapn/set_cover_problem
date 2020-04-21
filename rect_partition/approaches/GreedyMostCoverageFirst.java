package rect_partition.approaches;

import java.util.Collection;
import java.util.List;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * In this approach, we follow a greedy strategy where in each step we choose
 * the vertex that covers the most rectangles by itself.
 */
public class GreedyMostCoverageFirst extends Approach {

    public GreedyMostCoverageFirst(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
    }

    /**
     * In this specific implementation, we get all the neighbours from a state, and
     * choose the one with most rectangles covered, until we get to a final state.
     */
    @Override
    public int solve() throws PartitionProblemException {

        while (!currentState.isFinal()) {

            List<State> neighbours = currentState.expand();

            if (neighbours.size() == 0) {
                throw new PartitionProblemException("Unable to find a solution for this instance");
            }

            this.statesExpanded += neighbours.size();

            State candidate = neighbours.get(0);

            for (State neighbour : neighbours) {
                if (neighbour.getRectanglesLeft().size() < candidate.getRectanglesLeft().size()) {
                    candidate = neighbour;
                }
            }

            currentState = candidate;

        }

        return currentState.getChosenVerts().size();
    }

}