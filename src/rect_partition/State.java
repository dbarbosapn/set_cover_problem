package rect_partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rect_partition.utils.PartitionProblemException;

public class State {

    private Set<Vert> chosenVerts = new HashSet<>();
    private Set<Vert> vertsLeft = new HashSet<>();
    private Set<Integer> rectanglesLeft = new HashSet<>();
    private Set<Integer> rectanglesCovered = new HashSet<>();

    /**
     * Constructor for an empty state
     * 
     * @param verts             to be chosen
     * @param rectanglesToCover the rectangles to be covered
     */
    public State(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        this.vertsLeft = new HashSet<>(verts);
        this.rectanglesLeft = new HashSet<>(rectanglesToCover);
    }

    /**
     * Constructor for a clone state
     * 
     * @param s - the state to clone
     */
    public State(State s) {
        this.chosenVerts = new HashSet<>(s.getChosenVerts());
        this.vertsLeft = new HashSet<>(s.getVertsLeft());
        this.rectanglesLeft = new HashSet<>(s.getRectanglesLeft());
        this.rectanglesCovered = new HashSet<>(s.getRectanglesCovered());
    }

    public Set<Vert> getVertsLeft() {
        return vertsLeft;
    }

    public Set<Vert> getChosenVerts() {
        return chosenVerts;
    }

    public Set<Integer> getRectanglesLeft() {
        return rectanglesLeft;
    }

    public Set<Integer> getRectanglesCovered() {
        return rectanglesCovered;
    }

    /**
     * Checks if this is a final state by comparing the number of rectangles covered
     * to the total number of rectangles
     * 
     * @return true if the state is final; false otherwise
     */
    public boolean isFinal() {
        return rectanglesLeft.isEmpty();
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
    public State chooseVert(Vert v) throws PartitionProblemException {
        State clone = new State(this);

        if (!clone.vertsLeft.contains(v)) {
            throw new PartitionProblemException("Chosen vertex must be in the available vertexes");
        } else {
            clone.vertsLeft.remove(v);
            clone.chosenVerts.add(v);
            clone.rectanglesLeft.removeAll(v.getRectangles());
            clone.rectanglesCovered.addAll(v.getRectangles());
        }

        return clone;
    }

    /**
     * Gets the resulting state of unchoosing a vert
     * 
     * @param v - the vert to unchoose
     * @return the resulting state
     */
    public State unchooseVert(Vert v) throws PartitionProblemException {
        State clone = new State(this);

        if (!clone.chosenVerts.contains(v)) {
            throw new PartitionProblemException("The vertex must be in the chosen vertexes");
        } else {
            clone.vertsLeft.add(v);
            clone.chosenVerts.remove(v);
            Set<Integer> coveredRectangles = new HashSet<>();
            for (Vert vert : clone.chosenVerts) {
                coveredRectangles.addAll(vert.getRectangles());
            }
            clone.rectanglesCovered.removeAll(coveredRectangles);
            clone.rectanglesLeft.addAll(clone.rectanglesCovered);
            clone.rectanglesCovered = new HashSet<>(coveredRectangles);
        }

        return clone;
    }

    /**
     * Returns the neighbour solutions of the current state. A neighbour solution is
     * a state that is also final but with one less vertex
     * 
     * @return the list of the neighbours
     * @throws PartitionProblemException
     */
    public List<State> neighbourSolutions() throws PartitionProblemException {
        List<State> neighbours = new ArrayList<>(vertsLeft.size());

        for (Vert v : chosenVerts) {
            State unchoose = unchooseVert(v);
            if (unchoose.isFinal())
                neighbours.add(unchoose);
        }

        return neighbours;
    }

    public int getSolution() {

        // This bias will allow us to separate final solutions from non-final
        if (!this.isFinal()) {
            return (chosenVerts.size() * 2) + vertsLeft.size() + 1;
        }

        return chosenVerts.size();
    }

    @Override
    public String toString() {
        return "Rectangles Left To Be Covered: " + rectanglesLeft + "\n" + "Chosen Verts: " + chosenVerts + "\n"
                + "Verts Left: " + vertsLeft + "\n" + "Covered Rectangles: " + rectanglesCovered + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof State))
            return false;

        if (o == this)
            return true;

        State s = (State) o;

        return s.rectanglesLeft.equals(this.rectanglesLeft) && s.chosenVerts.equals(this.chosenVerts)
                && s.vertsLeft.equals(this.vertsLeft) && s.rectanglesCovered.equals(this.rectanglesCovered);
    }

    @Override
    public int hashCode() {
        return rectanglesLeft.hashCode() * chosenVerts.hashCode() * vertsLeft.hashCode() * rectanglesCovered.hashCode();
    }
}