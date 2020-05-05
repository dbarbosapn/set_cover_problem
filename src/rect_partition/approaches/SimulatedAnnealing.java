package rect_partition.approaches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

public class SimulatedAnnealing extends Approach {

    public static double INITIAL_TEMP = 10000;
    public static double COOLING_RATE = 0.003;
    public static double VERTS_REMOVE_PERCENTAGE = 18;
    public static double VERTS_ADD_PERCENTAGE = 14;

    private int maxVertsRemove;
    private int maxVertsAdd;

    private Approach initialApproach;
    private State cur;

    public SimulatedAnnealing(Collection<Vert> verts, Collection<Integer> rectanglesToCover) {
        super(verts, rectanglesToCover);
        initialApproach = new GreedyMostCoverageFirst(verts, rectanglesToCover);

        maxVertsAdd = (int) (VERTS_ADD_PERCENTAGE / 100d * verts.size());
        maxVertsRemove = (int) (VERTS_REMOVE_PERCENTAGE / 100d * verts.size());
    }

    @Override
    public int solve() throws PartitionProblemException {

        findInitialSolution();

        cur = currentState;

        double temp = INITIAL_TEMP;

        // While the system is not cold
        while (temp > 1) {
            State newState = new State(cur);

            // mutate the state
            newState = perturbate(newState);

            // Check if we accept the new one with the acceptance test
            if (accept(newState, temp)) {
                cur = newState;
            }

            // Check if is the best and update
            if (cur.getSolution() < currentState.getSolution()) {
                currentState = cur;
            }

            this.statesExpanded++;

            temp *= 1 - COOLING_RATE;
        }

        return currentState.getSolution();
    }

    /**
     * This function will return if the state should be accepted. It is based on
     * probability.
     * 
     * @param newState to be evaluated
     * @param temp     the temperature factor
     * @return the boolean determining if the state should be accepted
     */
    private boolean accept(State newState, double temp) {
        int newSolution = newState.getSolution();
        int curSolution = cur.getSolution();

        double probability;

        if (newSolution < curSolution)
            probability = 1;
        else
            probability = Math.exp((curSolution - newSolution) / temp);

        return probability > Math.random();
    }

    /**
     * Perturbate the solution. In this specific implementation, we will add and
     * remove some verts, randomly
     * 
     * @param newState
     */
    private State perturbate(State newState) throws PartitionProblemException {
        int removeNum = (int) (Math.random() * maxVertsRemove);
        int addNum = (int) (Math.random() * maxVertsAdd);

        List<Vert> chosenVerts = new ArrayList<>(newState.getChosenVerts());
        List<Vert> vertsLeft = new ArrayList<>(newState.getVertsLeft());

        for (int i = 0; i < removeNum; i++) {
            if (chosenVerts.size() == 0)
                break;

            int index = (int) (Math.random() * chosenVerts.size());
            newState = newState.unchooseVert(chosenVerts.get(index));
            chosenVerts.remove(index);
        }

        for (int i = 0; i < addNum; i++) {
            if (vertsLeft.size() == 0)
                break;

            int index = (int) (Math.random() * vertsLeft.size());
            newState = newState.chooseVert(vertsLeft.get(index));
            vertsLeft.remove(index);
        }

        return newState;
    }

    /**
     * Find an initial solution with the approach defined in the constructor
     * 
     * @throws PartitionProblemException
     */
    private void findInitialSolution() throws PartitionProblemException {
        initialApproach.solve();
        this.currentState = initialApproach.currentState;
        this.cur = currentState;
    }

}