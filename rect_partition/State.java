package rect_partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rect_partition.utils.PartitionProblemException;

public class State {

    private int totalRectangles;
    private Set<Vert> chosenVerts = new HashSet<>();
    private Set<Vert> vertsLeft = new HashSet<>();
    private Set<Integer> coveredRectangles = new HashSet<>();

    /**
     * Constructor for an empty state
     * 
     * @param totalRectangles in the state
     * @param verts           to be chosen
     */
    public State(int totalRectangles, Collection<Vert> verts) {
        this.totalRectangles = totalRectangles;
        this.vertsLeft = new HashSet<>(verts);
    }

    /**
     * Constructor for a clone state
     * 
     * @param s - the state to clone
     */
    public State(State s) {
        this.totalRectangles = s.getTotalRectangles();
        this.chosenVerts = new HashSet<>(s.getChosenVerts());
        this.vertsLeft = new HashSet<>(s.getVertsLeft());
        this.coveredRectangles = new HashSet<>(s.getCoveredRectangles());
    }

    public Set<Vert> getVertsLeft() {
        return vertsLeft;
    }

    public int getTotalRectangles() {
        return totalRectangles;
    }

    public Set<Vert> getChosenVerts() {
        return chosenVerts;
    }

    public Set<Integer> getCoveredRectangles() {
        return coveredRectangles;
    }

    /**
     * Checks if this is a final state by comparing the number of rectangles covered
     * to the total number of rectangles
     * 
     * @return true if the state is final; false otherwise
     */
    public boolean isFinal() {
        return coveredRectangles.size() == totalRectangles;
    }

    /**
     * Expands this state by getting all the vertex choices. Therefore, this is a
     * O(V) operation.
     * 
     * @return the list of neighbour states
     * @throws PartitionProblemException
     */
    public List<State> expand() throws PartitionProblemException {
        List<State> neighbours = new ArrayList<>(vertsLeft.size());

        for (Vert v : vertsLeft) {
            neighbours.add(chooseVert(v));
        }

        return neighbours;
    }

    /**
     * Gets the resulting state of choosing another vert
     * 
     * @param v - the vert to choose
     * @return the resulting state
     */
    private State chooseVert(Vert v) throws PartitionProblemException {
        State clone = new State(this);

        if (!clone.vertsLeft.contains(v)) {
            throw new PartitionProblemException("Chosen vertex must be in the available vertexes");
        } else {
            clone.vertsLeft.remove(v);
            clone.chosenVerts.add(v);
            clone.coveredRectangles.addAll(v.getRectangles());
        }

        return clone;
    }

    @Override
    public String toString() {
        return "Total Rectangles: " + totalRectangles + "\n" + "Chosen Verts: " + chosenVerts + "\n" + "Verts Left: "
                + vertsLeft + "\n" + "Covered Rectangles: " + coveredRectangles + "\n";
    }

}