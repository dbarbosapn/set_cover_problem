package rect_partition.approaches;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * This is a simple approach where we keep expanding the states until we find a
 * final one with a BFS
 */
public class BFS extends Approach {

    // We save the visited states to prevent cycles in the search
    Set<State> visited = new HashSet<>();

    public BFS(int totalRectangles, Collection<Vert> verts) {
        super(totalRectangles, verts);
    }

    @Override
    public int solve() throws PartitionProblemException {
        Queue<State> queue = new LinkedList<>();
        queue.add(currentState);

        while (!queue.isEmpty()) {
            State s = queue.remove();
            if (!visited.contains(s)) {
                visited.add(s);

                List<State> neighbours = s.expand();
                this.statesExpanded += neighbours.size();

                for (State n : neighbours) {
                    if (n.isFinal()) {
                        currentState = n;
                        return currentState.getChosenVerts().size();
                    }
                    queue.add(n);
                }
            }
        }

        throw new PartitionProblemException("Unable to find a solution for this instance");
    }

}