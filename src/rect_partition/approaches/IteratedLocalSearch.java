package rect_partition.approaches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

public class IteratedLocalSearch extends Approach {

    public static int K = 500;
    private static final double VERTS_REMOVE_PERCENTAGE = 0.18;
    private static final double VERTS_ADD_PERCENTAGE = 0.14;
    private static final double PROBABILITY_ACCEPT_WRONG_SOLUTION = 0.2;

    private int maxVertsRemove;
    private int maxVertsAdd;

    private Approach localSearchApproach;
    private State cur;

    private boolean stochastic;

    public IteratedLocalSearch(Collection<Vert> verts, Collection<Integer> rectanglesToCover, boolean stochastic) {
        super(verts, rectanglesToCover);
        localSearchApproach = new GreedyMostCoverageFirst(verts, rectanglesToCover);

        maxVertsAdd = (int) (VERTS_ADD_PERCENTAGE * verts.size());
        maxVertsRemove = (int) (VERTS_REMOVE_PERCENTAGE * verts.size());

        this.stochastic = stochastic;
    }

    @Override
    public int solve() throws PartitionProblemException {

        cur = currentState;

        // Find a solution to start with
        localSearch();

        currentState = localSearchApproach.currentState;
        cur = currentState;

        // Repeat K times
        for (int i = 0; i < K; i++) {
            State newState = new State(cur);

            // mutate the state
            newState = perturbate(newState);

            // Check if we accept the new one with the acceptance test
            if (accept(newState)) {
                cur = newState;
            }

            // Check if is the best and update
            if (cur.getSolution() < currentState.getSolution()) {
                currentState = cur;
            }

            this.statesExpanded++;
        }

        return currentState.getSolution();
    }

    /**
     * This function will return if the state should be accepted. If the stochastic
     * flag is true, we also have a probability of accepting a wrong solution to
     * allow more movement along the solution space
     * 
     * @param newState to be evaluated
     * @return the boolean determining if the state should be accepted
     */
    private boolean accept(State newState) {
        int newSolution = newState.getSolution();
        int curSolution = cur.getSolution();

        double probability = 0;

        if (newSolution < curSolution)
            probability = 1;
        else if (stochastic)
            probability = 1 - PROBABILITY_ACCEPT_WRONG_SOLUTION;

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
     * Find a local solution with the approach defined in the constructor
     * 
     * @throws PartitionProblemException
     */
    private void localSearch() throws PartitionProblemException {
        localSearchApproach.currentState = cur;
        localSearchApproach.solve();
        cur = localSearchApproach.currentState;
    }

}