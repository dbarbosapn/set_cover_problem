package rect_partition.approaches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import rect_partition.State;
import rect_partition.Vert;
import rect_partition.utils.PartitionProblemException;

/**
 * In this approach we have 3 stages:
 * 
 * Initial Solution - We find an initial solution to the problem. We're using
 * the greedy approach to get it.
 * 
 * Perturbation - We perturbate the solution by adding a few vertexes according
 * to the P factor
 * 
 * Local Search - We get the solutions close to the current one in the solution
 * space and do a BFS to find the local minimum
 */
public class IteratedLocalSearch extends Approach {

    public static final int K = 500;
    private static final int P = 4;

    private int bestSolution = Integer.MAX_VALUE;
    private boolean foundSolution = false;
    private Approach initialSolutionApproach;
    private boolean stochastic;

    public IteratedLocalSearch(Collection<Vert> verts, Collection<Integer> rectanglesToCover, boolean stochastic) {
        super(verts, rectanglesToCover);
        // We will use the GreedyMostCoverageFirst approach as the local search method.
        initialSolutionApproach = new GreedyMostCoverageFirst(verts, rectanglesToCover);
        this.stochastic = stochastic;
    }

    @Override
    public int solve() throws PartitionProblemException {

        // First, we find a solution to begin with
        findInitialSolution();

        if (!foundSolution) {
            throw new PartitionProblemException("Unable to find a solution for this instance");
        }

        // Then, we iterate K times
        for (int i = 0; i < K; i++) {
            // We perturbate the solution
            perturbation();
            // Then try to find a better solution
            localSearch();
        }

        return bestSolution;
    }

    /**
     * Find the neighbour solutions and find the local minimum with hill climbing
     * (to local minimum)
     */
    private void localSearch() throws PartitionProblemException {
        State cur = currentState;

        while (true) {
            List<State> neighbours = cur.neighbourSolutions();
            int best = Integer.MAX_VALUE;
            State next = null;

            this.statesExpanded += neighbours.size();

            for (State n : neighbours) {
                if (n.getSolution() < best) {
                    next = n;
                    best = n.getSolution();
                }
            }

            if (next == null || best >= cur.getSolution()) {
                currentState = cur;
                return;
            }

            cur = next;
        }
    }

    /**
     * The perturbation method will add P vertexes to the current solution, thus
     * finding a different worse solution that we can improve in the next step. If
     * the stochastic flag is true, we will randomly choose the verts
     */
    private void perturbation() throws PartitionProblemException {
        if (stochastic) {
            Random rnd = new Random();

            List<Vert> vertsLeft = new ArrayList<>(currentState.getVertsLeft());

            for (int i = 0; i < P; i++) {
                if (vertsLeft.size() == 0)
                    break;

                int index = rnd.nextInt(vertsLeft.size());

                currentState = currentState.chooseVert(vertsLeft.get(index));
                vertsLeft.remove(index);
            }

        } else {
            int limitCounter = 0;
            for (Vert v : currentState.getVertsLeft()) {
                if (limitCounter == P)
                    break;
                currentState = currentState.chooseVert(v);
                limitCounter++;
            }
        }
    }

    /**
     * Find the initial solution with the approach chosen in the constructor
     */
    private void findInitialSolution() throws PartitionProblemException {
        initialSolutionApproach.currentState = this.currentState;
        int solution = initialSolutionApproach.solve();

        if (solution < bestSolution) {
            bestSolution = solution;
            this.currentState = initialSolutionApproach.currentState;
            foundSolution = true;
        }

        this.statesExpanded += initialSolutionApproach.statesExpanded;
    }

}