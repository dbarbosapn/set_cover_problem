package rect_partition;

import java.util.Collection;
import java.util.HashSet;
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
     * Checks the rectangles being covered in this state by going through all the
     * chosen verts. Being N = chosenVerts.size() and M = totalRectangles, the
     * complexity of this function is O(N * M)
     * 
     * @return the number of rectangles covered
     */
    public Set<Integer> rectanglesCovered() {
        Set<Integer> set = new HashSet<>();
        for (Vert v : chosenVerts) {
            for (int c : v.getRectangles()) {
                set.add(c);
            }
        }

        return set;
    }

    /**
     * Checks if this is a final state by comparing the number of rectangles covered
     * to the total number of rectangles
     * 
     * @return true if the state is final; false otherwise
     */
    public boolean isFinal() {
        return rectanglesCovered().size() == totalRectangles;
    }

    /**
     * Clones the current state and chooses a vertex in "vertsLeft".
     * 
     * @param v - the vert to clone
     * @return the new state
     * @throws PartitionProblemException
     */
    public State chooseVert(Vert v) throws PartitionProblemException {
        if (!vertsLeft.contains(v)) {
            throw new PartitionProblemException("To choose a vert, that vert should be valid and unchosen");
        }

        State clone = new State(this);
        clone.getVertsLeft().remove(v);
        clone.getChosenVerts().add(v);
        clone.getCoveredRectangles().addAll(v.getRectangles());

        for (Vert vert : clone.getVertsLeft()) {
            vert.getRectangles().removeAll(clone.getCoveredRectangles());
        }

        return clone;
    }

}