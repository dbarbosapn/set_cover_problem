package rect_partition.approaches;

import java.util.HashSet;
import java.util.Set;

import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * Package private abstract class. Extended by other classes for specific
 * implementations
 */
public abstract class CSPApproach {

    protected Set<Vert> chosenVerts = new HashSet<>();
    protected int arcsChecked = 0;

    /**
     * Solves the problem according the the approach chosen
     * 
     * @return the number of verts picked
     */
    public abstract int solve() throws PartitionProblemException;

    public int getArcsChecked() {
        return arcsChecked;
    }

    public Set<Vert> getChosenVerts() {
        return chosenVerts;
    }

}