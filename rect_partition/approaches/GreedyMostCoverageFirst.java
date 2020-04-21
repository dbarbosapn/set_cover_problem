package rect_partition.approaches;

import java.util.Collection;

import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * In this approach, we follow a greedy strategy where in each step we choose
 * the vertex that covers the most rectangles by itself.
 */
public class GreedyMostCoverageFirst extends Approach {

    public GreedyMostCoverageFirst(int totalRectangles, Collection<Vert> verts) {
        super(totalRectangles, verts);
    }

    @Override
    public int solve() throws PartitionProblemException {

        while (!currentState.isFinal()) {
            this.statesVisited++;

            Vert chosen = null;

            // Select the vertex that covers more rectangles
            for (Vert v : currentState.getVertsLeft()) {
                if (chosen == null) {
                    chosen = v;
                } else if (v.getRectangles().size() > chosen.getRectangles().size()) {
                    chosen = v;
                }
            }

            currentState = currentState.chooseVert(chosen);
        }

        return currentState.getChosenVerts().size();
    }

}