package rect_partition.approaches;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * In this approach, as we traverse the neighbours of a state, after expanding
 * it, we keep track of which rectangles are being covered. From the states
 * where we get closer to the goal, we choose the one that covers the hardest
 * rectangle. This means, the rectangle that is covered the least times during
 * the search.
 */
public class GreedyHardestRectanglesFirst extends Approach {

    public GreedyHardestRectanglesFirst(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
    }

    @Override
    public int solve() throws PartitionProblemException {
        while (!currentState.isFinal()) {

            List<State> neighbours = currentState.expand();

            Map<Integer, Integer> rectangleCoverageMap = new HashMap<>();

            State candidate = null;
            int minCovers = 0;

            if (neighbours.size() == 0) {
                throw new PartitionProblemException("Unable to find a solution for this instance");
            }

            this.statesExpanded += neighbours.size();

            for (State neighbour : neighbours) {
                Set<Integer> newCovers = new HashSet<>(currentState.getRectanglesLeft());
                Set<Integer> left = neighbour.getRectanglesLeft();
                newCovers.removeAll(left);

                // The newCovers set contains the new rectangles getting covered in the
                // neighbour

                for (int r : newCovers) {
                    int timesCovered = rectangleCoverageMap.getOrDefault(r, 0) + 1;
                    rectangleCoverageMap.put(r, timesCovered);

                    // With this, we're setting the candidate to the set that covers the rectangle
                    // that was covered the least ammount of times, thus being the hardest.
                    if (timesCovered > minCovers) {
                        minCovers = timesCovered;
                        candidate = neighbour;
                    }
                }
            }

            if (candidate != null) {
                this.currentState = candidate;
            } else {
                throw new PartitionProblemException("Unable to find a solution for this instance");
            }

        }

        return currentState.getSolution();
    }

}