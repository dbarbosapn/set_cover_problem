package rect_partition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import rect_partition.utils.Utils;
import rect_partition.approaches.AStar;
import rect_partition.approaches.Approach;
import rect_partition.approaches.BFS;
import rect_partition.approaches.BranchAndBound;
import rect_partition.approaches.DFS;
import rect_partition.approaches.GreedyHardestRectanglesFirst;
import rect_partition.approaches.GreedyMostCoverageFirst;
import rect_partition.approaches.IDFS;
import rect_partition.approaches.IteratedLocalSearch;
import rect_partition.approaches.SimulatedAnnealing;

public class PartitionProblem {

    private static final int NUM_APPROACHES = 12;
    private static final String headerText = "Welcome to the Rectangle Partition Problem.\nThis software was designed and developed by Diogo Barbosa.\n";
    private static int selectedApproach;

    public static final String LOGGER = "PartitionProblemLogger";

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);

        setupLogger();

        while (true) {
            Utils.clearWindow(headerText);

            System.out.println("Choose an approach by entering the corresponding number: ");
            // Approaches
            System.out.println("1: Greedy - State that has less rectangles left to cover first");
            System.out.println("2: Greedy - State that has the hardest rectangle covered first");
            System.out.println("3: BFS (Stop at the first solution)");
            System.out.println("4: BFS (Find the best solution)");
            System.out.println("5: DFS (Stop at the first solution)");
            System.out.println("6: DFS (Find the best solution)");
            System.out.println("7: IDS (Stop at the first solution)");
            System.out.println("8: Branch And Bound");
            System.out.println("9: A* - Rectangles left as heuristic (Find the best solution)");
            System.out.println("10: Iterated Local Search (With " + IteratedLocalSearch.K + " attempts)");
            System.out.println("11: ILS with Randomization (With " + IteratedLocalSearch.K + " attempts)");
            System.out.println("12: Simulated Annealing");

            int chosen = stdin.nextInt();
            Utils.clearWindow(headerText);

            if (chosen < 1 || chosen > NUM_APPROACHES) {
                System.out.println("Invalid number of approaches. (Must be between 1 and " + NUM_APPROACHES + ")");
                stdin.close();
                return;
            }

            selectedApproach = chosen;

            System.out.print("Insert the file path: ");

            String filePath = stdin.next();
            Scanner file;
            try {
                file = new Scanner(new File(filePath));
            } catch (IOException e) {
                Utils.logError(e);
                stdin.close();
                return;
            }

            Utils.clearWindow(headerText);

            loadSets(file, stdin);

            file.close();

            System.out.print("Do you wish to start again? (Y/N): ");

            if (!stdin.next().toLowerCase().startsWith("y"))
                break;
        }

        stdin.close();
    }

    /**
     * Sets up the logger to write at log.txt
     */
    private static void setupLogger() {
        Logger logger = Logger.getLogger(LOGGER);

        FileHandler fh;

        try {
            fh = new FileHandler("log.txt");
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.info("Logger initialized");
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * Starts reading the file and asks before each set if the users wants to
     * continue
     * 
     * @param file
     * @param stdin
     */
    private static void loadSets(Scanner file, Scanner stdin) {
        int sets = file.nextInt();

        System.out.println("Number of sets: " + sets);
        System.out.println();

        for (int i = 0; i < sets; i++) {
            System.out.print("Proceed to set n." + (i + 1) + "? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y"))
                solveSet(file, stdin, i + 1);
            else
                break;

            System.out.println();
        }

    }

    /**
     * Solves the next set in the file
     * 
     * @param file
     * @param stdin
     */
    private static void solveSet(Scanner file, Scanner stdin, int setNumber) {
        int rectangles = file.nextInt();
        Map<Integer, Vert> vertMap = new HashMap<>();
        Set<Integer> rectanglesToCover = new HashSet<>();

        readSetData(rectangles, rectanglesToCover, vertMap, file);

        Approach approach = chooseApproach(vertMap.values(), rectanglesToCover);

        try {
            int answer = approach.solve();

            Utils.clearWindow(headerText);

            System.out.println("Number of vertexes in solution: " + answer);
            System.out.println("Number of states expanded: " + approach.getStatesExpanded());
            System.out.println();
            System.out.print("Do you want to get an output of the vertexes chosen? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y")) {
                String filename = outputVertexes(approach, setNumber);
                System.out.println("Your file is located at " + filename);
            }

        } catch (Exception e) {
            Utils.logError(e);
            return;
        }
    }

    private static String outputVertexes(Approach approach, int setNumber) throws FileNotFoundException {
        final String dir = System.getProperty("user.dir");
        File directory = new File(dir + "/output");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filename = dir + "/output/vertexes-set" + setNumber + ".txt";
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false));
        for (Vert v : approach.getChosenVerts())
            writer.println(v);
        writer.close();
        return filename;
    }

    /**
     * Returns an instance of the approach chosen
     * 
     * @param verts             all the verts in the set
     * @param rectanglesToCover the rectangles to be covered
     * @return the approach
     */
    private static Approach chooseApproach(Collection<Vert> verts, Set<Integer> rectanglesToCover) {
        switch (selectedApproach) {
            case 1:
                return new GreedyMostCoverageFirst(verts, rectanglesToCover);
            case 2:
                return new GreedyHardestRectanglesFirst(verts, rectanglesToCover);
            case 3:
                return new BFS(verts, rectanglesToCover, true);
            case 4:
                return new BFS(verts, rectanglesToCover, false);
            case 5:
                return new DFS(verts, rectanglesToCover, true);
            case 6:
                return new DFS(verts, rectanglesToCover, false);
            case 7:
                return new IDFS(verts, rectanglesToCover);
            case 8:
                return new BranchAndBound(verts, rectanglesToCover);
            case 9:
                return new AStar(verts, rectanglesToCover);
            case 10:
                return new IteratedLocalSearch(verts, rectanglesToCover, false);
            case 11:
                return new IteratedLocalSearch(verts, rectanglesToCover, true);
            case 12:
                return new SimulatedAnnealing(verts, rectanglesToCover);
        }

        return null;
    }

    /**
     * Reads the set data. This includes all the rectangles and vertexes.
     * 
     * @param rectangles - the number of rectangles in the set
     * @param vertMap    - the map containing the vertexes
     * @param file       - the file scanner
     */
    private static void readSetData(int rectangles, Set<Integer> rectanglesToCover, Map<Integer, Vert> vertMap,
            Scanner file) {
        // Read the rectangles
        for (int i = 0; i < rectangles; i++) {
            int currentRect = file.nextInt();

            int numVerts = file.nextInt();

            // Read the vertexes
            for (int j = 0; j < numVerts; j++) {
                int x = file.nextInt();
                int y = file.nextInt();

                Vert v = new Vert(x, y);

                // If we've already added this vert, get its reference
                if (vertMap.containsValue(v))
                    v = vertMap.get(v.hashCode());
                else
                    vertMap.put(v.hashCode(), v);

                v.getRectangles().add(currentRect);
            }
        }

        int r = file.nextInt();
        for (int i = 0; i < r; i++) {
            rectanglesToCover.add(file.nextInt());
        }

    }

}