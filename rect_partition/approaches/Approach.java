package rect_partition.approaches;

import java.util.Collection;
import java.util.Set;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * Package private abstract class. Extended by other classes for specific
 * implementations
 */
public abstract class Approach {

    protected int statesExpanded;
    protected State currentState;

    /**
     * Public constructor
     * 
     * @param totalRectangles   in the problem set
     * @param verts             in the problem set
     * @param rectanglesToCover
     */
    public Approach(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        currentState = new State(verts, rectanglesToCover);
        statesExpanded = 1;
    }

    /**
     * Solves the problem according the the approach chosen
     * 
     * @return the number of verts picked
     */
    public abstract int solve() throws PartitionProblemException;

    public int getStatesExpanded() {
        return statesExpanded;
    }

    public Set<Vert> getChosenVerts() {
        return currentState.getChosenVerts();
    }

}