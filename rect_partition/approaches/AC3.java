package rect_partition.approaches;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * The modeling for this approach is:
 * 
 * Variables: Every vertex v from the vertex set V that can be picked or not,
 * therefore v € {true, false}
 * 
 * Constraints: For every rectangle r in the goal set R, V must contain at least
 * one v from r.
 * 
 * 
 */
public class AC3 extends CSPApproach {

    Queue<Arc> agenda = new LinkedList<>();
    Set<Arc> arcs = new HashSet<>();

    Set<Integer> vertSet = new HashSet<>();
    Map<Integer, Set<Integer>> rectangleSets = new HashMap<>();

    Map<Integer, Vert> vertMap = new HashMap<>();

    public AC3(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        int vertId = 1;

        // initialize the rectangle value sets
        for (int i : rectanglesToCover) {
            rectangleSets.put(i, new HashSet<>());
        }

        // set values for the vert and rectangle sets
        for (Vert v : verts) {
            vertSet.add(vertId);
            vertMap.put(vertId, v);
            for (int r : v.getRectangles()) {
                if (rectangleSets.containsKey(r)) {
                    rectangleSets.get(r).add(vertId);
                }
            }
            vertId++;
        }

        // generate the arcs
        for (Set<Integer> rectangleSet : rectangleSets.values()) {
            Arc arc = new Arc(vertSet, rectangleSet);
            arcs.add(arc);
            agenda.add(arc);
        }

    }

    @Override
    public int solve() throws PartitionProblemException {

        while (!agenda.isEmpty()) {

            if (vertSet.isEmpty())
                throw new PartitionProblemException("Unable to get arc consistency");

            Arc arc = agenda.remove();

            if (removeInconsistentValues(arc)) {
                for (Arc neighbour : arcs) {
                    if (agenda.contains(neighbour))
                        continue;

                    // this checks if is a neighbour
                    if (neighbour.left == arc.left) {
                        agenda.add(neighbour);
                    }
                }
            }

            this.arcsChecked++;
        }

        // The chosen verts set will be ~vertSet
        for (int i : vertMap.keySet()) {
            if (!vertSet.contains(i)) {
                this.getChosenVerts().add(vertMap.get(i));
            }
        }

        return this.getChosenVerts().size();
    }

    private boolean removeInconsistentValues(Arc arc) {
        boolean removed = false;

        for (int x : arc.right) {
            if (!arc.constraint()) {
                arc.left.remove(x);
                removed = true;
            }
        }

        return removed;
    }

    private class Arc {

        Set<Integer> left;
        Set<Integer> right;

        public Arc(Set<Integer> left, Set<Integer> right) {
            this.left = left;
            this.right = right;
        }

        public boolean constraint() {

            // In this specific implementation, we're only checking if the left set is
            // missing any element from the right set. If it is, it means that element
            // was picked and the arc is consistent
            return !left.containsAll(right);
        }

    }

}