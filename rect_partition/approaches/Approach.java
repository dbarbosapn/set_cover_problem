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

    protected int totalRectangles;
    protected int statesExpanded;
    protected State currentState;

    /**
     * Public constructor
     * 
     * @param totalRectangles in the problem set
     * @param verts           in the problem set
     */
    public Approach(int totalRectangles, Collection<Vert> verts) {
        this.totalRectangles = totalRectangles;
        currentState = new State(totalRectangles, verts);
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